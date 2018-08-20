package leap_skills.leapskills;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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


public class Batch_Information extends Fragment {
    public static String token, centre_id, centre1;
    TableLayout tb;
    TextView centre;
    ProgressDialog progressDialog;
    public static final String PREFS_NAME3 = "BatchList";
    public static final String PREFS_NAME4 = "StudentList1";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_batch_information, container, false);
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences saved_data2 = getActivity().getSharedPreferences(PREFS_NAME4, 0);
        SharedPreferences.Editor editor2 = saved_data2.edit();
        editor2.clear();
        editor2.commit();

        centre = (TextView) view.findViewById(R.id.center_name);
        centre.setText(centre1);
        tb = (TableLayout) view.findViewById(R.id.attendence_table4);

        final Typeface font = ResourcesCompat.getFont(getContext(), R.font.worksans_medium);

        SharedPreferences saved_data = getActivity().getSharedPreferences(PREFS_NAME3, 0);
        String length = saved_data.getString("LoopLength", "0");
        final int length1 = Integer.parseInt(length);
        if (length1 > 0) {
            TableRow row1[] = new TableRow[length1];
            for (int x = 0; x < length1; x++) {
                row1[x] = new TableRow(getContext());
                row1[x].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                String batch_name = saved_data.getString("Name"+String.valueOf(x), null);
                int count = Integer.parseInt(saved_data.getString("Count"+String.valueOf(x), null));
                int id = Integer.parseInt(saved_data.getString("ID"+String.valueOf(x), null));
                final String id1 = String.valueOf(id);

                TextView tv2 = new TextView(getContext());
                tv2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                tv2.setText(batch_name);
                tv2.setTextColor(Color.parseColor("#3E7C62"));
                //tv2.setPaintFlags(tv2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                tv2.setTypeface(font);
                row1[x].addView(tv2, 150, 100);

                TextView tv3 = new TextView(getContext());
                tv3.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                tv3.setText(String.valueOf(count));
                tv3.setTextColor(Color.parseColor("#3E7C62"));
                tv3.setTypeface(font);
                row1[x].addView(tv3, LinearLayout.LayoutParams.WRAP_CONTENT, 100);

                TextView batch = (TextView) row1[x].getChildAt(0);
                final String batch_name1 = batch.getText().toString();
                batch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        student_details(batch_name1, id1);
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

        String url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/batch_list_pm/" + centre_id;
        Log.e("url is", url);

        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("BATCH COUNT RESPONSE", response.toString());
                if(response.length()==0){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Batch to show!", Toast.LENGTH_SHORT).show();
                }
                try {
                    SharedPreferences data = getActivity().getSharedPreferences(PREFS_NAME3, 0);
                    SharedPreferences.Editor editor = data.edit();
                    editor.putString("LoopLength", String.valueOf(response.length()));
                    TableRow row1[] = new TableRow[response.length()];
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject details = (JSONObject) response.get(i);
                        String batch_name = details.getString("name");
                        int count = details.getInt("count");
                        int id = details.getInt("id");
                        final String id1 = String.valueOf(id);

                        editor.putString("Name" + String.valueOf(i), batch_name);
                        editor.putString("Count" + String.valueOf(i), String.valueOf(count));
                        editor.putString("ID" + String.valueOf(i), String.valueOf(id));

                        row1[i] = new TableRow(getContext());
                        row1[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                        TextView tv2 = new TextView(getContext());
                        tv2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv2.setText(batch_name);
                        tv2.setTextColor(Color.parseColor("#3E7C62"));
                        //tv2.setPaintFlags(tv2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        tv2.setTypeface(font);
                        row1[i].addView(tv2, 150, 100);

                        TextView tv3 = new TextView(getContext());
                        tv3.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv3.setText(String.valueOf(count));
                        tv3.setTextColor(Color.parseColor("#3E7C62"));
                        tv3.setTypeface(font);
                        row1[i].addView(tv3, LinearLayout.LayoutParams.WRAP_CONTENT, 100);

                        TextView batch = (TextView) row1[i].getChildAt(0);
                        final String batch_name1 = batch.getText().toString();
                        batch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                student_details(batch_name1, id1);
                            }
                        });

                        tb.addView(row1[i]);
                        progressDialog.dismiss();

                    }
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Some Error Occurred :(", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error in batches", error.toString());
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
    public void student_details(String batch_name, String id){
        pm.selected_batch = batch_name;
        pm.batch_id = id;
        Batch_Info1.batch = batch_name;
        Batch_Info1.batch_id = id;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Batch_Info1 batch_info1 = new Batch_Info1();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, batch_info1);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        pm.flag = 4;
    }
}
