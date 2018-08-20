package leap_skills.leapskills;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Student_Info1 extends Fragment {
    public static String batch_id, token;
    TableLayout tb;
    ProgressDialog progressDialog;
    public static final String PREFS_NAME5 = "StudentList2";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_info1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onViewCreated(view, savedInstanceState);
        tb = (TableLayout) view.findViewById(R.id.attendence_table2);

        final Typeface font = ResourcesCompat.getFont(getContext(), R.font.worksans_medium);

        SharedPreferences saved_data = getActivity().getSharedPreferences(PREFS_NAME5, 0);
        String length = saved_data.getString("LoopLength", "0");
        final int length1 = Integer.parseInt(length);
        if (length1 > 0) {
            TableRow row1[] = new TableRow[length1];
            for (int x = 0; x < length1; x++) {

                row1[x] = new TableRow(getContext());
                row1[x].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                String leap_id1 = saved_data.getString("LeapId"+String.valueOf(x), null);
                String fname = saved_data.getString("Fname"+String.valueOf(x), null);
                String lname = saved_data.getString("Lname"+String.valueOf(x), null);
                String password = saved_data.getString("Password"+String.valueOf(x), null);

                TextView tv2 = new TextView(getContext());
                tv2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                tv2.setText(leap_id1);
                tv2.setTextColor(Color.parseColor("#303F9F"));
                tv2.setTypeface(font);
                row1[x].addView(tv2, 150, 80);

                TextView tv3 = new TextView(getContext());
                tv3.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                tv3.setText(fname);
                tv3.setTextColor(Color.parseColor("#3E7C62"));
                tv3.setTypeface(font);
                row1[x].addView(tv3, LinearLayout.LayoutParams.WRAP_CONTENT, 80);

                TextView tv4 = new TextView(getContext());
                tv4.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                tv4.setText(lname);
                tv4.setTextColor(Color.parseColor("#3E7C62"));
                tv4.setTypeface(font);
                row1[x].addView(tv4, LinearLayout.LayoutParams.WRAP_CONTENT, 80);

                TextView tv5 = new TextView(getContext());
                tv5.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                tv5.setText(password);
                tv5.setTextColor(Color.parseColor("#3E7C62"));
                tv5.setTypeface(font);
                row1[x].addView(tv5, LinearLayout.LayoutParams.WRAP_CONTENT, 80);

                TextView id = (TextView) row1[x].getChildAt(0);
                final String leap_id = id.getText().toString();
                id.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        student_details(leap_id);
                    }
                });

                tb.addView(row1[x]);
            }
        } else {
            progressDialog = new ProgressDialog(getContext());
            //progressDialog.setTitle("Please Wait!");
            progressDialog.setMessage("We are fetching the data !");
            progressDialog.setCancelable(false);
            progressDialog.show();
        String arr[] = {"Leap ID", "First Name", "Last Name", "Password"};

        /*final TableRow row[] = new TableRow[1];
        TextView tv1 = null;
        for (int i = 0; i < 1; i++) {
            row[i] = new TableRow(getContext());
            row[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < arr.length; j++) {
                tv1 = new TextView(getContext());
                tv1.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                tv1.setText(arr[j]);
                tv1.setTextColor(Color.parseColor("#3E7C62"));
                tv1.setTypeface(font, Typeface.BOLD);
                row[i].addView(tv1, 100, 100);
            }
            tb.addView(row[i]);
        }*/

        String url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/batch_student/" + batch_id;

        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()==0){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Students to show!", Toast.LENGTH_SHORT).show();
                }
                try {
                    SharedPreferences data = getActivity().getSharedPreferences(PREFS_NAME5, 0);
                    SharedPreferences.Editor editor = data.edit();
                    editor.putString("LoopLength", String.valueOf(response.length()));
                    TableRow row1[] = new TableRow[response.length()];
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject details = (JSONObject) response.get(i);
                        String leap_id1 = details.getString("leap_id");
                        String fname = details.getString("first_name");
                        String lname = details.getString("last_name");
                        String password = details.getString("dob");

                        editor.putString("LeapId" + String.valueOf(i), leap_id1);
                        editor.putString("Fname" + String.valueOf(i), fname);
                        editor.putString("Lname" + String.valueOf(i), lname);
                        editor.putString("Password" + String.valueOf(i), password);

                        row1[i] = new TableRow(getContext());
                        row1[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));


                        TextView tv2 = new TextView(getContext());
                        tv2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv2.setText(leap_id1);
                        tv2.setTextColor(Color.parseColor("#303F9F"));
                        tv2.setTypeface(font);
                        row1[i].addView(tv2, 150, 80);

                        TextView tv3 = new TextView(getContext());
                        tv3.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv3.setText(fname);
                        tv3.setTextColor(Color.parseColor("#222222"));
                        tv3.setTypeface(font);
                        row1[i].addView(tv3, LinearLayout.LayoutParams.WRAP_CONTENT, 80);

                        TextView tv4 = new TextView(getContext());
                        tv4.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv4.setText(lname);
                        tv4.setTextColor(Color.parseColor("#222222"));
                        tv4.setTypeface(font);
                        row1[i].addView(tv4, LinearLayout.LayoutParams.WRAP_CONTENT, 80);

                        TextView tv5 = new TextView(getContext());
                        tv5.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv5.setText(password);
                        tv5.setTextColor(Color.parseColor("#222222"));
                        tv5.setTypeface(font);
                        row1[i].addView(tv5, LinearLayout.LayoutParams.WRAP_CONTENT, 80);

                        TextView id = (TextView) row1[i].getChildAt(0);
                        final String leap_id = id.getText().toString();
                        id.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                student_details(leap_id);
                            }
                        });

                        tb.addView(row1[i]);
                    }
                    editor.commit();
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Some Error Occurred :(", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.toString());
                progressDialog.setMessage("Something went wrong :(");
                progressDialog.setCancelable(true);
                if (progressDialog.isShowing()) {
                } else
                    Toast.makeText(getContext(), "Please try again!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params2 = new HashMap<String, String>();
                params2.put("Authentication-Token", token);
                Log.e("HEADER", params2.toString() + "\n\n\n");
                return params2;
            }
        };
        requestQueue.add(jsonArrayRequest).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                progressDialog.setCancelable(true);
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
    }

    public void student_details(String leap_id2){
        StudentDetails.leap_id = leap_id2;
        StudentDetails.token = token;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        StudentDetails studentDetails = new StudentDetails();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, studentDetails);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        trainer.flag = 4;
    }
}
