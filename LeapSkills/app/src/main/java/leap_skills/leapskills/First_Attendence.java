package leap_skills.leapskills;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class First_Attendence extends Fragment {
    TableLayout tb;
    public static String id, token, session_id, date;
    Button submit;
    int length;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first_attendence, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onViewCreated(view, savedInstanceState);

        submit = (Button) view.findViewById(R.id.sfattendence);
        tb = view.findViewById(R.id.fattendence_table);

        progressDialog = new ProgressDialog(getContext());
        //progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("We are fetching the data !");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String arr[] = {"ID", "Name", " P", "  A"};
        final Typeface font = ResourcesCompat.getFont(getContext(), R.font.worksans_medium);

        TableRow row[] = new TableRow[1];

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
                row[i].addView(tv1, 100, 50);
            }
            tb.addView(row[i]);
        }

        String url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/batch_student/"+id;

        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()==0){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Students to show!", Toast.LENGTH_SHORT).show();
                }
                try {
                        length = response.length();
                        TableRow row1[] = new TableRow[response.length()];
                        for(int i=0; i<response.length(); i++){
                            int w = i;
                        JSONObject details = (JSONObject) response.get(i);
                        String id1 = details.getString("id");
                        String fname = details.getString("first_name");
                        String lname = details.getString("last_name");

                        row1[i] = new TableRow(getContext());
                        row1[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));


                        TextView tv2 = new TextView(getContext());
                        tv2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv2.setText(id1);
                        tv2.setTextColor(Color.parseColor("#222222"));
                        tv2.setTypeface(font);
                        row1[i].addView(tv2, 100, 80);

                        TextView tv3 = new TextView(getContext());
                        String jname = fname + " " + lname;
                        tv3.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv3.setText(jname);
                        tv3.setTextColor(Color.parseColor("#222222"));
                        tv3.setTypeface(font);
                        row1[i].addView(tv3, LinearLayout.LayoutParams.WRAP_CONTENT, 80);

                        RadioButton p = new RadioButton(getContext());
                        p.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        p.setChecked(true);
                        row1[i].addView(p, LinearLayout.LayoutParams.WRAP_CONTENT+20,80);

                        RadioButton a = new RadioButton(getContext());
                        a.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        row1[i].addView(a, LinearLayout.LayoutParams.WRAP_CONTENT+20, 80);

                        final RadioButton present = (RadioButton) row1[w].getChildAt(2);
                        final RadioButton absent = (RadioButton) row1[w].getChildAt(3);

                        present.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked==true)
                                absent.setChecked(false);
                            }
                        });
                        absent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked==true)
                                present.setChecked(false);
                            }
                        });

                        tb.addView(row1[i]);
                        }
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Some error Occurred :(", Toast.LENGTH_LONG).show();
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.setMessage("Something went wrong :(");
                progressDialog.setCancelable(true);
                Log.e("Error: ", error.toString());
                if (progressDialog.isShowing()) {
                } else
                    Toast.makeText(getContext(), "Please try again!", Toast.LENGTH_SHORT).show();
            }
        }){
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    public void submit(){
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject sattendence = new JSONObject();
        for (int i = 0; i<length; i++){
            TableRow row = (TableRow) tb.getChildAt(i+1);
            TextView col0 = (TextView)row.getChildAt(0);
            String id = col0.getText().toString();
            RadioButton col2 = (RadioButton) row.getChildAt(2);
            RadioButton col3 = (RadioButton) row.getChildAt(3);
            Boolean result = null;
            if(col2.isChecked())
                result = true;
            else if(col3.isChecked())
                result = false;
            try {
                sattendence.put(id, result);
            } catch (JSONException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Some error Occurred :(", Toast.LENGTH_LONG).show();
                Log.e("CATCH Exception1: ", e.toString());

            }
        }
        Log.e("Attendance object: ", sattendence.toString());

        JSONObject fsattendence = new JSONObject();
        try {
            fsattendence.put("session", Integer.parseInt(session_id));
            fsattendence.put("type", "first");
            fsattendence.put("date", date);
            fsattendence.put("batch", Integer.parseInt(id));
            fsattendence.put("students", sattendence);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("CATCH Exception2: ", e.toString());
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Some error Occurred :(", Toast.LENGTH_LONG).show();
        }
        Log.e("FAttendance object: ", fsattendence.toString());

        String surl = "http://leap-opa.ap-south-1.elasticbeanstalk.com/save_attendance";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, surl, fsattendence, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("hfd", "iojfi");
                Log.e("Response", response.toString());
                progressDialog.dismiss();
                Toast.makeText(getContext(), "SUCCESSFULY SUBMITTED", Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                TrainerHome trainerHome = new TrainerHome();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content_frame, trainerHome);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
                trainer.flag = 1;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.setMessage("Something went wrong :(");
                progressDialog.setCancelable(true);
                Log.e("Error: ", error.toString());
                if (progressDialog.isShowing()) {
                } else
                    Toast.makeText(getContext(), "Please try again!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params2 = new HashMap<String, String>();
                params2.put("Authentication-Token", token);
                Log.e("HEADER", params2.toString() + "\n\n\n");
                return params2;
            }
        };
        requestQueue.add(jsonObjectRequest).setRetryPolicy(new RetryPolicy() {
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
