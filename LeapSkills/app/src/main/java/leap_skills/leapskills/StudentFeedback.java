package leap_skills.leapskills;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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


public class StudentFeedback extends Fragment {
    Spinner subject;
    public static String leap_id, token;
    Button submit;
    EditText body;
    ArrayAdapter<String> subjectAdapter;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_feedback, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getContext());

        subject = (Spinner) view.findViewById(R.id.feedback_option);
        submit = (Button) view.findViewById(R.id.stu_feedback1);
        body = (EditText) view.findViewById(R.id.sfeedback);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject_is = (String) subject.getSelectedItem();
                if (subject_is.equals("Select an option")) {
                    Toast.makeText(getContext(), "Please select a subject!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Please Wait!");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    String feedback_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/save_feedback";
                    String body1 = body.getText().toString();
                    JSONObject feedback = new JSONObject();
                    try {
                        feedback.put("subject", subject_is);
                        feedback.put("body", body1);
                        feedback.put("leap_id", leap_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Some error Occurred :(", Toast.LENGTH_LONG).show();
                    }
                    RequestQueue requestQueue1 = Volley.newRequestQueue(getContext());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, feedback_url, feedback, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Successfully Submitted", Toast.LENGTH_SHORT).show();

                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            StudentHome studentHome = new StudentHome();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.add(R.id.content_frame, studentHome);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commitAllowingStateLoss();
                            third.flag = 1;
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
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params2 = new HashMap<String, String>();
                            params2.put("Authentication-Token", token);
                            Log.e("HEADER", params2.toString() + "\n\n\n");
                            return params2;
                        }
                    };
                    requestQueue1.add(jsonObjectRequest).setRetryPolicy(new RetryPolicy() {
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
        });
    }
}
