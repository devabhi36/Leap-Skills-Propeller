package leap_skills.leapskills;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

public class login extends Activity {
    EditText id, password;
    String e_id, e_pass;
    public static final String PREFS_NAME = "LoginPrefs";
    boolean internet = false;
    ProgressDialog progressDialog;
    ImageView show, hide;
    CheckBox remember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        id = (EditText) findViewById(R.id.leap_id);
        password = (EditText) findViewById(R.id.dob);
        remember = (CheckBox) findViewById(R.id.remember_me);

        show = (ImageView) findViewById(R.id.show);
        hide = (ImageView) findViewById(R.id.hide);

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    hide.setVisibility(View.INVISIBLE);
                    show.setVisibility(View.VISIBLE);
                    password.setSelection(password.getText().length());

            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().length()>0){
                    password.setTransformationMethod(null);
                    show.setVisibility(View.INVISIBLE);
                    hide.setVisibility(View.VISIBLE);
                    password.setSelection(password.getText().length());
                }
                else
                    Toast.makeText(login.this, "PASSWORD FIELD EMPTY", Toast.LENGTH_SHORT).show();

            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("LOGGING IN...");
        progressDialog.setCancelable(false);

        Log.e("ERROR", "ERROR IS");

        SharedPreferences logged = getSharedPreferences(PREFS_NAME, 0);
        String send_token = logged.getString("token", "NO Value");
        String saved_id = logged.getString("id", "No Value Id");
        String saved_name = logged.getString("name", "No Value");
        String saved_user = logged.getString("role", "");

        if (logged.getString("logged", "").toString().equals("stu_logged")) {
            String savedLeapID = logged.getString("LeapID", "NO Value");
            String savedLeapPass = logged.getString("Password", "No Value");
            third.token = send_token;
            third.student_name = saved_name;
            third.id = saved_id;
            Notification.user = saved_user;
            Notification.unique_id = saved_id;
            StudentHome.unique_id = saved_id;
            StudentHome.leap_id = savedLeapID;
            StudentHome.password = savedLeapPass;
            Intent intent = new Intent(this, third.class);
            startActivity(intent);
            finish();
        }
        if (logged.getString("logged", "").toString().equals("trainer_logged")) {
            String savedLeapID = logged.getString("LeapID", "NO Value");
            trainer.token = send_token;
            trainer.trainer_name = saved_name;
            trainer.id = saved_id;
            Notification.user = saved_user;
            Notification.unique_id = saved_id;
            TrainerHome.unique_id = saved_id;
            trainer.leap_id = savedLeapID;
            Intent intent = new Intent(this, trainer.class);
            startActivity(intent);
            finish();
        }
        if (logged.getString("logged", "").toString().equals("pm_logged")) {
            pm.token = send_token;
            pm.pm_name = saved_name;
            pm.id = saved_id;
            Notification.user = saved_user;
            Notification.unique_id = saved_id;
            PMHome.unique_id = saved_id;
            Intent intent = new Intent(this, pm.class);
            startActivity(intent);
            finish();
        }

    }

    public void flogin(View v3){
        e_id = id.getText().toString().trim();
        e_pass = password.getText().toString().trim();
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            internet = true;
        }
        else
            internet = false;

        if(e_id.equals("") || e_pass.equals("")){
            Toast.makeText(this, "Please fill in all the details.", Toast.LENGTH_SHORT).show();
        }

        else {
            progressDialog.show();
            Entered entered = new Entered();
            entered.setId(e_id);
            entered.setPassword(e_pass);

            String url1 = "http://leap-opa.ap-south-1.elasticbeanstalk.com/login";

            JSONObject params = new JSONObject();
            try {
                params.put("email", entered.getId());
                params.put("password", entered.getPassword());
                Log.e("ENTERED ", params + "\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url1, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    final String[] id = new String[1];
                    try {
                        JSONObject data = response.getJSONObject("response");
                        final JSONObject user = data.getJSONObject("user");
                        final String token = user.getString("authentication_token");

                        String url2 = "http://leap-opa.ap-south-1.elasticbeanstalk.com/check_role/";

                        progressDialog.setTitle("Successfully Logged In !");
                        progressDialog.setMessage("Please wait!\nWe are fetching your data.");

                        RequestQueue rq = Volley.newRequestQueue(login.this);
                        JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String name = response.getString("name");
                                    Log.e("Name is ", name);
                                    String role = response.getString("role");
                                    Notification.user = role;
                                    if (role.equals("student")){
                                        third.token = token;
                                        StudentHome.leap_id = e_id;
                                        StudentHome.password = e_pass;
                                        id[0] = user.getString("id");
                                        third.student_name = name;
                                        third.id = id[0];
                                        StudentHome.unique_id = id[0];
                                        Notification.unique_id = id[0];
                                        //if(remember.isChecked()) {
                                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("logged", "stu_logged");
                                            editor.putString("token", token);
                                            editor.putString("id", id[0]);
                                            editor.putString("role", role);
                                            editor.putString("name", name);
                                            editor.putString("LeapID", e_id);
                                            editor.putString("Password", e_pass);
                                            editor.commit();
                                        //}
                                        Intent intent = new Intent(login.this, third.class);
                                        startActivity(intent);
                                        finish();

                                        progressDialog.dismiss();

                                    } else if(role.equals("trainer")){
                                        id[0] = user.getString("id");
                                        trainer.token = token;
                                        trainer.trainer_name = name;
                                        trainer.leap_id = e_id;
                                        trainer.id = id[0];
                                        TrainerHome.unique_id = id[0];
                                        Notification.unique_id = id[0];
                                        //if(remember.isChecked()) {
                                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("logged", "trainer_logged");
                                            editor.putString("token", token);
                                            editor.putString("id", id[0]);
                                            editor.putString("role", role);
                                            editor.putString("name", name);
                                            editor.putString("LeapID", e_id);
                                            editor.commit();
                                        //}
                                        Intent intent = new Intent(login.this, trainer.class);
                                        startActivity(intent);
                                        finish();

                                        progressDialog.dismiss();

                                    } else if(role.equals("pm")){
                                        id[0] = user.getString("id");
                                        pm.pm_name = name;
                                        pm.token = token;
                                        pm.id = id[0];
                                        PMHome.unique_id = id[0];
                                        Notification.unique_id = id[0];
                                        //if(remember.isChecked()) {
                                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("logged", "pm_logged");
                                            editor.putString("token", token);
                                            editor.putString("id", id[0]);
                                            editor.putString("role", role);
                                            editor.putString("name", name);
                                            editor.commit();
                                        //}
                                        Intent intent = new Intent(login.this, pm.class);
                                        startActivity(intent);
                                        finish();

                                        progressDialog.dismiss();

                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(login.this, "SOME ERROR HAS BEEN OCCURED PLEASE TRY AGAIN", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    progressDialog.dismiss();
                                    Log.e("ERROR" , " ERROR");
                                    Toast.makeText(login.this, "NO DATA FOUND!", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.setCancelable(true);
                                progressDialog.dismiss();
                                Log.e("Error", error.toString());
                                Toast.makeText(login.this, "Some error occurred :(", Toast.LENGTH_LONG).show();
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params2 = new HashMap<String, String>();
                                params2.put("Authentication-Token", token);
                                Log.e("HEADER", params2.toString()+"\n\n\n");
                                return params2;
                            }
                        };

                        rq.add(jsonObjectRequest1).setRetryPolicy(new RetryPolicy() {
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
                                Toast.makeText(getApplicationContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        Toast.makeText(login.this,  "Something went wrong :(", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(internet == false) {
                        progressDialog.dismiss();
                        Toast.makeText(login.this,  "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                    } else if(internet == true){
                        progressDialog.dismiss();
                        Log.e("Error", error.toString());
                        Toast.makeText(login.this,  "Incorrect Username or Password", Toast.LENGTH_LONG).show();
                    }
                }
            });
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
                    Toast.makeText(getApplicationContext(), "Slow Internet Connection", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*public void pm_login(View v4){
        Toast.makeText(this, "TO BE CODED", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, pm.class);
        startActivity(intent);
    }
    public void trainer_login(View v4){
        Toast.makeText(this, "TO BE CODED", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, trainer.class);
        startActivity(intent);
    }
    public void sign_up(View v2){
        Intent intent = new Intent(this, sign_up.class);
        startActivity(intent);
    }*/

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            System.exit(1);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 20000);
    }
}
