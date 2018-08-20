package leap_skills.leapskills;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.widget.RadioButton;
import android.widget.TableRow;
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

public class pm extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public  static int flag = 1;
    public static final String PREFS_NAME = "LoginPrefs";
    public static final String PREFS_NAME1 = "FetchedData";
    public static final String PREFS_NAME2 = "StudentList";
    public static final String PREFS_NAME3 = "BatchList";
    TextView pm_name1;
    public static String pm_name, token, id, centre_id, selected_batch, batch_id, unique_id;
    TextView batch_name;
    ProgressDialog progressDialog;
    boolean internet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pm);
        batch_name = (TextView)findViewById(R.id.batch_name);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCancelable(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        pm_name1 = header.findViewById(R.id.pm_name1);
        pm_name1.setText("Hi "+pm_name+"!");

        PMHome.token = token;
        Pre_Assess_Info.token = token;
        CreateBatch.token = token;
        Student_Info_PM.token = token;
        Batch_Info1.token = token;
        Batch_Allotment.token = token;
        Batch_Information.token = token;

        progressDialog.show();

        pm_home(null);
    }

    @Override
    //modify below
    public void onBackPressed() {
        //Change the options
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(flag == 1){
            SharedPreferences saved_data = getSharedPreferences(PREFS_NAME1, 0);
            SharedPreferences.Editor editor1 = saved_data.edit();
            editor1.clear();
            editor1.commit();
            finish();
        } else if(flag == 2){
            pm_home(null);
        } else if(flag == 3){
            pm_stu_info(null);
        } else if(flag == 4){
            batch_info(null);
        } else if(flag == 5){
            batch_info1(null);
        }/* else if(flag == 6){
            view_student_problem(null);
        } else if(flag == 7){
            view_trainer_problrm(null);
        }*/
        else {
            SharedPreferences saved_data = getSharedPreferences(PREFS_NAME1, 0);
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
        if (id == R.id.pm_home) {
            pm_home(null);
        } else if (id == R.id.pre_assess) {
            pm_home(null);
        } else if (id == R.id.view_stu) {
            pm_home(null);
        } else if (id == R.id.create_batch) {
            create_batch(null);
        } /*else if (id == R.id.view_student_problem) {
            view_student_problem(null);
        } else if (id == R.id.view_trainer_problrm) {
            view_trainer_problrm(null);
        } */else if (id == R.id.pm_notification) {
            notification();
        } else if (id == R.id.assign_batch) {
            pm_home(null);
        } else if (id == R.id.pm_logout) {
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

    public void pm_home(View view){

        SharedPreferences saved_data = getSharedPreferences(PREFS_NAME2, 0);
        SharedPreferences.Editor editor = saved_data.edit();
        editor.clear();
        editor.commit();

        SharedPreferences saved_data1 = getSharedPreferences(PREFS_NAME3, 0);
        SharedPreferences.Editor editor1 = saved_data1.edit();
        editor1.clear();
        editor1.commit();

        FragmentManager fragmentManager = getSupportFragmentManager();
        PMHome pmHome = new PMHome();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, pmHome);

        progressDialog.dismiss();

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction.remove(new Batch_Allotment());
        flag = 1;
    }
        public void pre_assess(View v1){
            progressDialog.show();
            if(centre_id.equals("Select")){
                progressDialog.dismiss();
                Toast.makeText(pm.this, "Please select a centre!", Toast.LENGTH_SHORT).show();
            } else {
                Pre_Assess_Info.centre_id = centre_id;
                FragmentManager fragmentManager = getSupportFragmentManager();
                Pre_Assess_Info pre_assess_info = new Pre_Assess_Info();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content_frame, pre_assess_info);
                progressDialog.dismiss();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
                flag = 2;
            }
        }

        public void pm_stu_info(View v2){
            progressDialog.show();
            if(centre_id.equals("Select")){
                progressDialog.dismiss();
                Toast.makeText(pm.this, "Please select a centre!", Toast.LENGTH_SHORT).show();
            } else {
                Student_Info_PM.centre_id = centre_id;
                FragmentManager fragmentManager = getSupportFragmentManager();
                Student_Info_PM student_info_pm = new Student_Info_PM();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content_frame, student_info_pm);
                progressDialog.dismiss();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
                flag = 2;
            }
        }

        public void batch_info(View v1){

            progressDialog.show();

            if(centre_id.equals("Select")){

                progressDialog.dismiss();

                Toast.makeText(pm.this, "Please select a centre!", Toast.LENGTH_SHORT).show();
            } else {
                Batch_Information.centre_id = centre_id;
                FragmentManager fragmentManager = getSupportFragmentManager();
                Batch_Information batch_information = new Batch_Information();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content_frame, batch_information);

                progressDialog.dismiss();

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
                flag = 2;
            }
        }
            public void batch_info1(View v2){
                progressDialog.show();
                Batch_Info1.batch = selected_batch;
                Batch_Info1.batch_id = batch_id;
                FragmentManager fragmentManager = getSupportFragmentManager();
                Batch_Info1 batch_info1= new Batch_Info1();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content_frame, batch_info1);
                progressDialog.dismiss();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
                flag = 4;
            }
                /*public void batch_stu_details(View v3){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    StudentDetails studentDetails = new StudentDetails();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.content_frame, studentDetails);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();
                    flag = 5;
                }*/
            public void create_batch1(View v2){
                FragmentManager fragmentManager = getSupportFragmentManager();
                CreateBatch createBatch= new CreateBatch();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content_frame, createBatch);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
                flag = 4;
            }

        public void batch_allot(View v1){
            progressDialog.show();
            if(centre_id.equals("Select")){
                progressDialog.dismiss();
                Toast.makeText(pm.this, "Please select a centre!", Toast.LENGTH_SHORT).show();
            } else {
                Batch_Allotment.centre_id = centre_id;
                FragmentManager fragmentManager = getSupportFragmentManager();
                Batch_Allotment batch_allotment = new Batch_Allotment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.content_frame, batch_allotment);
                progressDialog.dismiss();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.remove(new PMHome());
                fragmentTransaction.commitAllowingStateLoss();
                flag = 2;
            }
        }

    public void create_batch(View v1){
        FragmentManager fragmentManager = getSupportFragmentManager();
        CreateBatch createBatch= new CreateBatch();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, createBatch);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 2;
    }
//        public void fcreate_batch(View v2){
//            Toast.makeText(this,"TO BE CODED", Toast.LENGTH_SHORT).show();
//        }

    public void view_student_problem(View v2){
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
        flag = 6;
    }

    public void view_trainer_problrm(View v2){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);}
        FragmentManager fragmentManager = getSupportFragmentManager();
        View_trainer_prob view_trainer_prob = new View_trainer_prob();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, view_trainer_prob);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 2;
    }
    public void trainer_prob_list(View v2){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);}
        FragmentManager fragmentManager = getSupportFragmentManager();
        TrainerDetails trainerDetails = new TrainerDetails();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, trainerDetails);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        flag = 7;
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
        } else{}
            JSONObject token1 = new JSONObject();
        try {
            token1.put("token","");
            token1.put("phone",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Token Json", token1.toString());

        String token_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/save_token";
        RequestQueue requestQueue1 = Volley.newRequestQueue(pm.this);
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

                Toast.makeText(pm.this, "Slow Internet Connection", Toast.LENGTH_SHORT).show();
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
        SharedPreferences saved_data = getSharedPreferences(PREFS_NAME1, 0);
        SharedPreferences.Editor editor1 = saved_data.edit();
        editor1.clear();
        editor1.commit();
        Intent intent = new Intent(pm.this, login.class);
        startActivity(intent);
        this.finish();
    }
}
