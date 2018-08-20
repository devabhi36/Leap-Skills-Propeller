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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Batch_Allotment extends Fragment {
    public static String token, centre_id;
    int batch_length, student_length;
    TableLayout tb;
    String batch_name[];
    String batch_id[];
    ArrayAdapter<String> batchAdapter;
    Button allot;
    int student_id[];
    String batch_id_is[];
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_batch_allotment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onViewCreated(view, savedInstanceState);

        allot = (Button) view.findViewById(R.id.fbatch_allot);
        progressDialog = new ProgressDialog(getContext());
        //progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("We are fetching the data !");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String batch_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/list_batch";
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, batch_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                batch_length = response.length();
                batch_name = new String[batch_length];
                batch_id = new String[batch_length];
                for (int k = 0; k<batch_length; k++){
                    try {
                        JSONObject batch = (JSONObject) response.get(k);
                        String id = batch.getString("id");
                        batch_id[k] = id;
                        String name = batch.getString("name");
                        batch_name[k] = name;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Some Error Occurred :(", Toast.LENGTH_SHORT).show();
                    }
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params2 = new HashMap<String, String>();
                params2.put("Authentication-Token", token);
                Log.e("HEADER", params2.toString() + "\n\n\n");
                return params2;
            }
        };
        requestQueue.add(jsonArrayRequest);

        tb = (TableLayout) view.findViewById(R.id.attendence_table7);
        final Typeface font = ResourcesCompat.getFont(getContext(), R.font.worksans_medium);
        String batching_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/pm_center_list_batching/"+centre_id;

        RequestQueue requestQueue1 = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest1 = new JsonArrayRequest(Request.Method.GET, batching_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {//8468001122
                student_length = response.length();
                if(student_length==0){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Students to allot batch!", Toast.LENGTH_SHORT).show();
                }
                batch_id_is = new String[student_length];
                student_id = new int[student_length];
                    try {
                        TableRow row1[] = new TableRow[response.length()];
                        for(int i=0; i<response.length(); i++){
                            final int w = i;
                            JSONObject details = (JSONObject) response.get(i);
                            String leap_id1 = details.getString("leap_id");
                            String fname = details.getString("first_name");
                            String lname = details.getString("last_name");
                            String pre_assess_level = details.getString("result_pre");
                            String clg_name = details.getString("college_name");
                            String stream = details.getString("stream");
                            student_id[i] = details.getInt("student_id");

                            row1[i] = new TableRow(getContext());
                            row1[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));


                            TextView tv2 = new TextView(getContext());
                            tv2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            tv2.setText(leap_id1);
                            tv2.setTextColor(Color.parseColor("#222222"));
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
                            tv5.setText(pre_assess_level);
                            tv5.setTextColor(Color.parseColor("#222222"));
                            tv5.setTypeface(font);
                            row1[i].addView(tv5, LinearLayout.LayoutParams.WRAP_CONTENT, 80);

                            TextView tv6 = new TextView(getContext());
                            tv6.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            tv6.setText(clg_name);
                            tv6.setTextColor(Color.parseColor("#222222"));
                            tv6.setTypeface(font);
                            row1[i].addView(tv6, LinearLayout.LayoutParams.WRAP_CONTENT, 80);

                            TextView tv7 = new TextView(getContext());
                            tv7.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            tv7.setText(stream);
                            tv7.setTextColor(Color.parseColor("#222222"));
                            tv7.setTypeface(font);
                            row1[i].addView(tv7, LinearLayout.LayoutParams.WRAP_CONTENT, 80);

                            List<String> batch1 = new ArrayList<String>();
                            batch1.add("Select");
                            for(int j = 0; j<batch_length; j++){
                                batch1.add(batch_name[j]);
                            }
                            Spinner batch_s = new Spinner(getContext());
                            batch_s.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            batchAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, batch1);
                            batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            batch_s.setAdapter(batchAdapter);
                            row1[i].addView(batch_s, LinearLayout.LayoutParams.WRAP_CONTENT, 80);


                            final Spinner s_batch = (Spinner) row1[i].getChildAt(6);
                            s_batch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    //Toast.makeText(getContext(), "Selected "+String.valueOf(w) , Toast.LENGTH_SHORT).show();
                                    if(position == 0){
                                        batch_id_is[w] = "Select";
                                        Log.e("ID IS: ", batch_id_is[w]);
                                    }
                                    else {
                                        String centre_is = (String) s_batch.getSelectedItem();
                                        Log.e("Selected Centre: ", centre_is);
                                        int j;
                                        for (j = 0; j < batch_length; j++) {
                                            if (batch_name[j].equals(centre_is))
                                                break;
                                            else
                                                continue;
                                        }
                                        batch_id_is[w] = batch_id[j];
                                        Log.e("ID IS: ", batch_id_is[w]);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            tb.addView(row1[i]);
                        }
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params2 = new HashMap<String, String>();
                params2.put("Authentication-Token", token);
                Log.e("HEADER", params2.toString() + "\n\n\n");
                return params2;
            }
        };
        requestQueue1.add(jsonArrayRequest1).setRetryPolicy(new RetryPolicy() {
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

        allot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait!");
                progressDialog.setCancelable(false);
                progressDialog.show();
                JSONObject batch_allot = new JSONObject();
                for (int m = 0; m < student_length; m++) {
                    try {
                        batch_allot.put(String.valueOf(student_id[m]), Integer.parseInt(batch_id_is[m]));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Some Error Occurred :(", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.e("Submit response id ", batch_allot.toString());
                String allot_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/allot_batch";
                RequestQueue requestQueue2 = Volley.newRequestQueue(getContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, allot_url, batch_allot, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Batch Alloted Successfully!", Toast.LENGTH_SHORT).show();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        PMHome pmHome= new PMHome();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.content_frame, pmHome);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commitAllowingStateLoss();
                        pm.flag = 1;
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
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params2 = new HashMap<String, String>();
                        params2.put("Authentication-Token", token);
                        Log.e("HEADER", params2.toString() + "\n\n\n");
                        return params2;
                    }
                };
                requestQueue2.add(jsonObjectRequest).setRetryPolicy(new RetryPolicy() {
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
        });
    }
}
