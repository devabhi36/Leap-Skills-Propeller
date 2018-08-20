package leap_skills.leapskills;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class StudentDetails extends Fragment {
    public static String token, leap_id;
    TextView stu_name, stu_leapID, stu_password,
             stu_phone, stu_batch, stu_batch_start_time,
             stu_batch_time_end2, stu_next_session, stu_app_uptake,
             stu_attendence;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        progressDialog = new ProgressDialog(getContext());
        //progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("We are fetching the data !");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/student/"+leap_id;
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String fname = response.getString("first_name");
                    String lname = response.getString("last_name");
                    String name = fname+" "+lname;
                    String leapId = response.getString("leap_id");
                    String password = response.getString("password");
                    String phone = response.getString("phone");
                    String batch = response.getString("batch_name");
                    String start = response.getString("start_time");
                    String end = response.getString("end_time");
                    String session = response.getString("session_name");
                    String uptake = response.getString("app_uptake");
                    String attendence = response.getString("attendence");

                    String app_uptake5 = "Not installed!";
                    if(!uptake.equals("-1")){
                        app_uptake5 = uptake+"%";
                    }

                    stu_name.setText(name);
                    stu_leapID.setText(leapId);
                    stu_password.setText(password);
                    stu_phone.setText(phone);
                    stu_batch.setText(batch);
                    stu_batch_start_time.setText(start);
                    stu_batch_time_end2.setText(end);
                    stu_next_session.setText(session);
                    stu_app_uptake.setText(app_uptake5);
                    stu_attendence.setText(attendence+"%");
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
                progressDialog.setMessage("Something went wrong :(");
                progressDialog.setCancelable(true);
                Log.e("Centre Error: ", error.toString());
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
    public void initViews(View v1){
        stu_name = v1.findViewById(R.id.stu_name);
        stu_leapID = v1.findViewById(R.id.stu_leapID);
        stu_password = v1.findViewById(R.id.stu_password);
        stu_phone = v1.findViewById(R.id.stu_phone);
        stu_batch = v1.findViewById(R.id.stu_batch);
        stu_batch_start_time = v1.findViewById(R.id.stu_batch_start_time);
        stu_batch_time_end2 = v1.findViewById(R.id.stu_batch_time_end2);
        stu_next_session = v1.findViewById(R.id.stu_next_session);
        stu_app_uptake = v1.findViewById(R.id.stu_app_uptake);
        stu_attendence = v1.findViewById(R.id.stu_attendence);

    }
}
