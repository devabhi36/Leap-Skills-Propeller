package leap_skills.leapskills;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static leap_skills.leapskills.login.PREFS_NAME;

public class third extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static int flag = 1;
    public static final String PREFS_NAME = "LoginPrefs";
    TextView student_name1;
    public static String student_name, token, id, unique_id;
    public static final String PREFS_NAME6 = "StudentDetails";
    boolean internet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_third);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        student_name1 = header.findViewById(R.id.stu_name1);
        student_name1.setText("Hi "+student_name+"!");

        StudentHome.token = token;
        Schedule.token =token;
        Schedule.role = "s";
        StudentFeedback.token = token;

        student_home(null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(flag == 1){
            SharedPreferences saved_data = getSharedPreferences(PREFS_NAME6, 0);
            SharedPreferences.Editor editor1 = saved_data.edit();
            editor1.clear();
            editor1.commit();
            finish();
        } else if(flag == 2){
            student_home(null);
        } else {
            SharedPreferences saved_data = getSharedPreferences(PREFS_NAME6, 0);
            SharedPreferences.Editor editor1 = saved_data.edit();
            editor1.clear();
            editor1.commit();
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.stu_home) {
            student_home(null);
        } else if (id == R.id.stu_schedule) {
            schedule();
        }else if (id == R.id.stu_notification) {
            notification();
        } /*else if (id == R.id.reg_stu_prob) {
            reg_stu_prob(null);
        }*/
        else if (id == R.id.stu_feedback) {
            student_feedback(null);
        } else if (id == R.id.stu_logout) {
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

    public void open_drawer(View v5){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    public void student_home(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        StudentHome studentHome = new StudentHome();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, studentHome);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 1;
    }

    public void student_feedback(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        StudentFeedback studentFeedback = new StudentFeedback();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, studentFeedback);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 2;
    }

    public void reg_stu_prob(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Reg_Stu_Prob reg_stu_prob = new Reg_Stu_Prob();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, reg_stu_prob);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 2;
    }
        public void reg_stu_prob1(View v1){
            //GET DATE AND TIME ON CLICK
            Toast.makeText(this, "PROBLEM SUBMITTED", Toast.LENGTH_SHORT).show();
            student_home(null);
        }

   public void schedule(){
       FragmentManager fragmentManager = getSupportFragmentManager();
       Schedule schedule = new Schedule();
       FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
       fragmentTransaction.add(R.id.content_frame, schedule);
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
           RequestQueue requestQueue1 = Volley.newRequestQueue(third.this);
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
                   Toast.makeText(third.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
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
           SharedPreferences saved_data = getSharedPreferences(PREFS_NAME6, 0);
           SharedPreferences.Editor editor1 = saved_data.edit();
           editor1.clear();
           editor1.commit();
           Intent intent = new Intent(third.this, login.class);
           startActivity(intent);
           finish();
       }
   }

}