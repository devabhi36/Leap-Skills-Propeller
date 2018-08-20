package leap_skills.leapskills;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Student_Info extends Fragment {
    Spinner select_batch;
    public static String token;
    String batch_name[], batch_id[];
    ArrayAdapter<String> batchAdapter;
    int batch_length;
    String id_is;
    ProgressDialog progressDialog;
    public static final String PREFS_NAME5 = "StudentList2";
    Button stu_info2;
    boolean internet=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences saved_data2 = getActivity().getSharedPreferences(PREFS_NAME5, 0);
        SharedPreferences.Editor editor2 = saved_data2.edit();
        editor2.clear();
        editor2.commit();

        select_batch = (Spinner) view.findViewById(R.id.select_batch1);
        stu_info2 = (Button) view.findViewById(R.id.stu_info2);

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            internet = true;
        } else
            internet = false;

        progressDialog = new ProgressDialog(getContext());
        //progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("We are fetching the data !");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String batch_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/trainer_batch";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, batch_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()==0){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Batch to show!", Toast.LENGTH_SHORT).show();
                }

                Log.e("BATCH RESPONSE: ", response.toString());

                batch_length = response.length();
                batch_name = new String[batch_length];
                batch_id = new String[batch_length];
                List<String> batch = new ArrayList<String>();
                batch.add("Select");
                for (int i = 0; i < batch_length; i++) {
                    try {
                        JSONObject batch1 = (JSONObject) response.get(i);
                        String batch_name1 = batch1.getString("name");
                        String batch_id1 = batch1.getString("id");
                        batch_name[i] = batch_name1;
                        batch_id[i] = batch_id1;
                        batch.add(batch_name1);

                        batchAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, batch);
                        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        select_batch.setAdapter(batchAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Some Error Occurred :(", Toast.LENGTH_SHORT).show();
                    }
                }
                progressDialog.dismiss();
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

        select_batch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    id_is = "Select";
                }
                else {
                    String batch_is = (String) select_batch.getSelectedItem();
                    Log.e("Selected Batch: ", batch_is);
                    int i;
                    for (i = 0; i < batch_length; i++) {
                        if (batch_name[i].equals(batch_is))
                            break;
                        else
                            continue;
                    }
                    id_is = batch_id[i];
                    Log.e("ID IS: ", id_is);
                }
                trainer.batch_id = id_is;
                Student_Info1.batch_id = id_is;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stu_info2.setEnabled(true);
        if(internet==false){
            stu_info2.setEnabled(false);
        }

    }
}
