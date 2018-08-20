package leap_skills.leapskills;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
//EN GURU APP KE LIYE KEY CHAHIYE




public class StudentHome extends Fragment {

    TextView name, leap_id1, password1, batch, start_time, end_time, next_session, app_uptake, attendence_count,
            pre_ass, app_support, en_guru, post_ass,
            copy1, copy2, copy3, copy4;
    Button app_link1, app_link2, app_link3, app_link4;
    public static String leap_id, password, token, unique_id;
    boolean internet = false;
    ProgressDialog progressDialog;
    String token2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onViewCreated(view, savedInstanceState);

        Log.e("MESSAGE", "WELCOME STUDENT HOME");
        StudentFeedback.leap_id = leap_id;

        initViews(view);

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            internet = true;
        } else
            internet = false;

        token2 = FirebaseInstanceId.getInstance().getToken();
        Log.e("Token_is", token2);

        JSONObject token1 = new JSONObject();
        try {
            token1.put("token",token2);
            token1.put("phone",unique_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Token Json", token1.toString());

        String token_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/save_token";
        RequestQueue requestQueue1 = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, token_url, token1, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Message", "Token Send");
                Log.e("Message1", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (internet == false) {
                    Toast.makeText(getContext(), "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getContext(), "SOME ERROR OCCURRED :(", Toast.LENGTH_LONG).show();
                Log.e("Error Token", error.toString());
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
        requestQueue1.add(jsonObjectRequest1).setRetryPolicy(new RetryPolicy() {
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
                Toast.makeText(getContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait!\nWe are fetching your data.");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String fname = response.getString("first_name");
                    String lname = response.getString("last_name");
                    String batch1 = response.getString("batch_name");
                    String id = response.getString("batch_id");
                    Schedule.batch_id = id;
                    String start_time1 = response.getString("start_time");
                    String end_time1 = response.getString("end_time");
                    String next_session1 = response.getString("session");
                    int app_uptake1 = response.getInt("app_uptake");
                    int attendence_count1 = response.getInt("attendence");
                    String pre_ass1 = response.getString("pre_ass");
                    String app_support1 = response.getString("support_app");
                    String en_guru1 = response.getString("enguru");
                    String post_ass1 = response.getString("post_ass");
                    String pre_ass2="NA";
                    if(!pre_ass1.equals("")){
                        pre_ass2 = pre_ass1;
                    }
                    String app_support2="NA";
                    if(!app_support1.equals("")){
                        app_support2 = app_support1;
                    }
                    String en_guru2="NA";
                    if(!en_guru1.equals("")){
                        en_guru2 = en_guru1;
                    }
                    String post_ass2="NA";
                    if(!post_ass1.equals("")){
                        post_ass2 = post_ass1;
                    }
                    String app_uptake5 = "Not installed!";
                    if(!String.valueOf(app_uptake1).equals("-1")){
                        app_uptake5 = String.valueOf(app_uptake1)+"%";
                    }

                    name.setText("Hi, " + fname + " " + lname);
                    leap_id1.setText(leap_id);
                    password1.setText(password);
                    batch.setText(batch1);
                    start_time.setText(start_time1);
                    end_time.setText(end_time1);
                    next_session.setText(next_session1);
                    app_uptake.setText(app_uptake5);
                    attendence_count.setText(String.valueOf(attendence_count1)+"%");
                    pre_ass.setText(pre_ass2);
                    if (pre_ass1.length()<=0 || pre_ass2.equals("NA") || internet == false){
                        app_link1.setBackgroundColor(Color.parseColor("#D3D3D3"));
                        app_link1.setEnabled(false);
                    }
                    app_support.setText(app_support2);
                    if (app_support1.length()<=0 || app_support2.equals("NA") || internet == false){
                        app_link2.setBackgroundColor(Color.parseColor("#D3D3D3"));
                        app_link2.setEnabled(false);
                    }
                    en_guru.setText(en_guru2);
                    if (en_guru1.length()<=0 || en_guru2.equals("NA") || internet == false){
                        app_link3.setBackgroundColor(Color.parseColor("#D3D3D3"));
                        app_link3.setEnabled(false);
                    }
                    post_ass.setText(post_ass2);
                    if (post_ass1.length()<=0 || post_ass2.equals("NA") || internet == false){
                        app_link4.setBackgroundColor(Color.parseColor("#D3D3D3"));
                        app_link4.setEnabled(false);
                    }
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                    Log.e("THE EXCEPTION IS ", e.toString());
                    Toast.makeText(getContext(), "SOME ERROR OCCURRED :(", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (internet == false) {
                    Toast.makeText(getContext(), "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                } else
                Toast.makeText(getContext(), "SOME ERROR OCCURRED :(", Toast.LENGTH_SHORT).show();
                Log.e("THE SOME ERROR IS ", error.toString());

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

        app_link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet==false){
                    Toast.makeText(getContext(), "No Internet Connection :(", Toast.LENGTH_SHORT).show();
                }
                    else if(internet==true) {
                    Toast.makeText(getContext(), "Please Wait!", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.aspiringminds.amcat"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
        app_link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet==false){
                    Toast.makeText(getContext(), "No Internet Connection :(", Toast.LENGTH_SHORT).show();
                }
                else if(internet==true) {
                    Toast.makeText(getContext(), "Please Wait!", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.CultureAlley.japanese.english"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
        app_link3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet==false){
                    Toast.makeText(getContext(), "No Internet Connection :(", Toast.LENGTH_SHORT).show();
                }
                else if(internet==true) {
                    Toast.makeText(getContext(), "Please Wait!", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.enguru.enterprise"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
        app_link4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet==false){
                    Toast.makeText(getContext(), "No Internet Connection :(", Toast.LENGTH_SHORT).show();
                }
                else if(internet==true) {
                    Toast.makeText(getContext(), "Please Wait!", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.aspiringminds.amcat"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        copy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet==false || pre_ass.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Nothing to copy :(", Toast.LENGTH_SHORT).show();
                }
                else if(internet==true) {
                    ClipboardManager cManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData cData = ClipData.newPlainText("copy1", pre_ass.getText());
                    cManager.setPrimaryClip(cData);
                    Toast.makeText(getContext(), "Pre-Assessment ID copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        copy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet==false || app_support.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Nothing to copy :(", Toast.LENGTH_SHORT).show();
                }
                else if(internet==true) {
                    ClipboardManager cManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData cData = ClipData.newPlainText("copy2", app_support.getText());
                    cManager.setPrimaryClip(cData);
                    Toast.makeText(getContext(), "Hello English ID copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        copy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet==false || en_guru.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Nothing to copy :(", Toast.LENGTH_SHORT).show();
                }
                else if(internet==true) {
                    ClipboardManager cManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData cData = ClipData.newPlainText("copy3", en_guru.getText());
                    cManager.setPrimaryClip(cData);
                    Toast.makeText(getContext(), "Enguru ID copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        copy4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(internet==false || post_ass.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Nothing to copy :(", Toast.LENGTH_SHORT).show();
                }
                else if(internet==true) {
                    ClipboardManager cManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData cData = ClipData.newPlainText("copy4", post_ass.getText());
                    cManager.setPrimaryClip(cData);
                    Toast.makeText(getContext(), "Post-Asessment ID copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void initViews(View view){
        name = view.findViewById(R.id.trainer_feedback2);
        leap_id1 = view.findViewById(R.id.stu_leapID1);
        password1 = view.findViewById(R.id.stu_password1);
        batch = view.findViewById(R.id.stu_batch1);
        start_time = view.findViewById(R.id.stu_batch_time_start1);
        end_time = view.findViewById(R.id.stu_batch_time_end1);
        next_session = view.findViewById(R.id.stu_next_session1);
        app_uptake = view.findViewById(R.id.stu_app_uptake1);
        attendence_count = view.findViewById(R.id.stu_attendence1);
        pre_ass = view.findViewById(R.id.stu_pre_assessment_ID1);
        app_support = view.findViewById(R.id.stu_hello_english_ID1);
        en_guru = view.findViewById(R.id.stu_en_grur_ID1);
        post_ass = view.findViewById(R.id.stu_post_assessment_ID1);
        copy1 = view.findViewById(R.id.copy1);
        copy2 = view.findViewById(R.id.copy2);
        copy3 = view.findViewById(R.id.copy3);
        copy4 = view.findViewById(R.id.copy4);
        app_link1 = view.findViewById(R.id.app_link1);
        app_link2 = view.findViewById(R.id.app_link2);
        app_link3 = view.findViewById(R.id.app_link3);
        app_link4 = view.findViewById(R.id.app_link4);
    }
}
