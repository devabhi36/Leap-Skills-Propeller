package leap_skills.leapskills;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
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

public class trainer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static int flag = 1;
    public static final String PREFS_NAME = "LoginPrefs";
    TextView trainer_name1;
    public static String trainer_name, token, id, leap_id, batch_id, batch_id1;
    ProgressDialog progressDialog;
    boolean internet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_trainer);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        trainer_name1 = header.findViewById(R.id.trainer_name1);
        trainer_name1.setText("Hi "+trainer_name+"!");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCancelable(false);

        TrainerHome.token = token;
        First_Attendence.token = token;
        Last_Attendence.token = token;
        Student_Info.token = token;
        Student_Info1.token = token;
        Batch_Info.token = token;
        Schedule.token = token;
        Schedule.role = "t";
        TrainerFeedback.leap_id = leap_id;
        TrainerFeedback.token = token;

        progressDialog.show();

        trainer_home(null);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(flag == 1){
            finish();
        } else if(flag == 2){
            trainer_home(null);
        } else if(flag == 3){
            student_info(null);
        } else if(flag == 4){
            stu_info(null);
        } else if(flag == 5){
            view_stu_problem(null);
        } else if(flag == 6){
            batch_info1(null);
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.trainer_home) {
            trainer_home(null);
        } else if (id == R.id.attendence) {
            trainer_home(null);
        } else if (id == R.id.stu_info) {
            student_info(null);
        } else if (id == R.id.trainer_schedule) {
            batch_info1(null);
        } else if (id == R.id.trainer_notification) {
            notification();
        } /*else if (id == R.id.view_stu_problem) {
            view_stu_problem(null);
        } else if (id == R.id.reg_trainer_prob) {
            reg_trainer_prob(null);
        }*/

        else if (id == R.id.trainer_feedback) {
            trainer_feedback(null);
        } else if (id == R.id.trainer_logout) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                internet = true;
            } else
                internet = false;
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void open_drawer(View v5){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    public void trainer_home(View view){
        TrainerHome.trainer_name = trainer_name;
        FragmentManager fragmentManager = getSupportFragmentManager();
        TrainerHome trainerHome = new TrainerHome();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, trainerHome);
        progressDialog.dismiss();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 1;
    }
//        public void fattendence(View v1){
//            String date = attendence_date.getText().toString();
//            First_Attendence.date = date;
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            First_Attendence first_attendence = new First_Attendence();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.add(R.id.content_frame, first_attendence);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commitAllowingStateLoss();
//            flag = 2;
//        }
//        public void lattendence(View v2){
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            Last_Attendence last_attendence = new Last_Attendence();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.add(R.id.content_frame, last_attendence);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commitAllowingStateLoss();
//            flag = 2;
//    }

    public void student_info(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Student_Info student_info = new Student_Info();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, student_info);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 2;
    }
        public void stu_info(View v1){
            progressDialog.show();
            if(batch_id.equals("Select")){
                progressDialog.dismiss();
                Toast.makeText(trainer.this, "Please select a batch!", Toast.LENGTH_SHORT).show();
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Student_Info1 student_info1 = new Student_Info1();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content_frame, student_info1);
                progressDialog.dismiss();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
                flag = 3;
            }
        }
//            public void stu_details(View v2){
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                StudentDetails studentDetails = new StudentDetails();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.add(R.id.content_frame, studentDetails);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commitAllowingStateLoss();
//                flag = 4;
//            }

    public void trainer_feedback(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        TrainerFeedback trainerFeedback = new TrainerFeedback();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, trainerFeedback);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 2;
    }

    public void view_stu_problem(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        View_student_prob view_student_prob = new View_student_prob();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, view_student_prob);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 2;
    }
        public void stu_prob_list(View v2){
            FragmentManager fragmentManager = getSupportFragmentManager();
            StudentDetails studentDetails = new StudentDetails();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content_frame, studentDetails);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
            flag = 5;
        }

    public void reg_trainer_prob(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Reg_Trainer_Prob reg_trainer_prob = new Reg_Trainer_Prob();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, reg_trainer_prob);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 2;
    }
        public void reg_trainer_prob1(View v1){
        //GET DATE AND TIME ON CLICK
        Toast.makeText(this, "PROBLEM SUBMITTED", Toast.LENGTH_SHORT).show();
        trainer_home(null);
    }

    public void batch_info1(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Batch_Info batch_info = new Batch_Info();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, batch_info);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 2;
    }

    public void schedule(View v1){
        progressDialog.show();
        if(batch_id1.equals("Select")){
            progressDialog.dismiss();
            Toast.makeText(trainer.this, "Please select a batch!", Toast.LENGTH_SHORT).show();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Schedule schedule1 = new Schedule();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content_frame, schedule1);
            progressDialog.dismiss();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
            flag = 6;
        }
    }

    private void notification() {
        Notification.token = token;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Notification notification = new Notification();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, notification);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 2;
    }

    public void logout(){
        if (internet == false) {
            Toast.makeText(getApplicationContext(), "Please Check your internet connection!", Toast.LENGTH_LONG).show();
        } else {
            JSONObject token1 = new JSONObject();
            try {
                token1.put("token", "");
                token1.put("phone", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("Token Json", token1.toString());

            String token_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/save_token";
            RequestQueue requestQueue1 = Volley.newRequestQueue(trainer.this);
            JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.POST, token_url, token1, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("Message", "Token Send");
                    Log.e("Message1", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error Token", error.toString());
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

                    Toast.makeText(trainer.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
                }
            });

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("logged");
            editor.remove("token");
            editor.remove("id");
            editor.remove("name");
            editor.clear();
            editor.commit();
            Intent intent = new Intent(trainer.this, login.class);
            startActivity(intent);
            finish();
        }
    }
}
