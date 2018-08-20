package leap_skills.leapskills;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TrainerHome extends Fragment {

    Spinner select_batch, select_session;
    public static String token, unique_id;
    String batch_name[], batch_id[];
    String session_name[], session_id[];
    Button fattendence, lattendence;
    ArrayAdapter<String> batchAdapter, sessionAdapter;
    int batch_length, session_length;
    String id_is;
    String token2;
    EditText date;
    ProgressDialog progressDialog;
    TextView ttv3, trainer_title;
    String e_Date = "", selected_b, selected_s;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    boolean internet = false;
    public static String trainer_name;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trainer_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onViewCreated(view, savedInstanceState);
        select_batch = (Spinner) view.findViewById(R.id.select_batch);
        select_session = (Spinner) view.findViewById(R.id.select_session);
        fattendence = (Button) view.findViewById(R.id.fattendence);
        lattendence = (Button) view.findViewById(R.id.lattendence);
        ttv3 = (TextView) view.findViewById(R.id.ttv3);
        trainer_title = (TextView) view.findViewById(R.id.hi);
        trainer_title.setText("Hello "+trainer_name+"!");
        date = (EditText) view.findViewById(R.id.attendence_date);

        date.setInputType(InputType.TYPE_NULL);

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
        //progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("We are fetching the data !");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final Calendar calendar = Calendar.getInstance();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int month1 = month+1;
                date.setVisibility(View.VISIBLE);
                date.setInputType(View.AUTOFILL_TYPE_NONE);
                if(month1<10 && dayOfMonth<10){
                    date.setText("0"+dayOfMonth+"-0"+month1+"-"+year);
                    e_Date = year+"0"+month1+"0"+dayOfMonth;
                } else if(month1>9 && dayOfMonth>9){
                    date.setText(dayOfMonth+"-"+month1+"-"+year);
                    e_Date = ""+year+month1+dayOfMonth;
                } else if(month1>9 && dayOfMonth<10){
                    date.setText("0"+dayOfMonth+"-"+month1+"-"+year);
                    e_Date = year+month1+"0"+dayOfMonth;
                } else if(month1<10 && dayOfMonth>9){
                    date.setText(dayOfMonth+"-0"+month1+"-"+year);
                    e_Date = year+"0"+month1+dayOfMonth;
                }
                Log.e("Date", e_Date);

            }
        };

        fattendence.setEnabled(true);
        lattendence.setEnabled(true);

        if(internet==false){
            fattendence.setEnabled(false);
            lattendence.setEnabled(false);
        }

        String batch_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/trainer_batch";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, batch_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
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
                        progressDialog.dismiss();
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Some error Occurred :(", Toast.LENGTH_LONG).show();
                    }
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (internet == false) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                } else if(internet == true) {
                    progressDialog.setMessage("Something went wrong :(");
                    progressDialog.setCancelable(true);
                    Log.e("Error: ", error.toString());
                    if (progressDialog.isShowing()) {
                    } else
                        Toast.makeText(getContext(), "Please try again!", Toast.LENGTH_SHORT).show();
                }
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
                fattendence.setBackgroundColor(Color.parseColor("#3E7C62"));
                fattendence.setEnabled(true);
                lattendence.setBackgroundColor(Color.parseColor("#3E7C62"));
                lattendence.setEnabled(true);
                selected_b ="Select";
                selected_s="Select";
                if (position == 0) {
                    List<String> session = new ArrayList<String>();
                    session.add("Select");
                    sessionAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, session);
                    sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    select_session.setAdapter(sessionAdapter);
                }
                else {
                    progressDialog.show();
                    String batch_is = (String) select_batch.getSelectedItem();
                    Log.e("Selected Batch: ", batch_is);
                    selected_b = batch_is;
                    int i;
                    for (i = 0; i < batch_length; i++) {
                        if (batch_name[i].equals(batch_is))
                            break;
                        else
                            continue;
                    }
                    id_is = batch_id[i];
                    First_Attendence.id = id_is;
                    Last_Attendence.id = id_is;
                    Log.e("ID IS: ", id_is);
                    String session_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/batch_session/";
                    String fsession_url = session_url + id_is;
                    Log.e("SESSION URL: ", fsession_url);

                    RequestQueue requestQueue1 = Volley.newRequestQueue(getContext());
                    JsonArrayRequest jsonArrayRequest1 = new JsonArrayRequest(Request.Method.GET, fsession_url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.e("Session: ", response.toString());
                            session_length = response.length();
                            session_name = new String[session_length];
                            session_id = new String[session_length];
                            List<String> session = new ArrayList<String>();
                            session.add("Select");
                            if(response.length()==0){
                                Toast.makeText(getContext(), "No Sessions to show!", Toast.LENGTH_SHORT).show();
                                sessionAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, session);
                                sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                select_session.setAdapter(sessionAdapter);
                            }
                            for (int j = 0; j < session_length; j++) {
                                try {
                                    JSONObject session1 = (JSONObject) response.get(j);
                                    String session_name1 = session1.getString("name");
                                    String session_id1 = session1.getString("id");
                                    session_name[j] = session_name1;
                                    session_id[j] = session_id1;
                                    session.add(session_name1);

                                    sessionAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, session);
                                    sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    select_session.setAdapter(sessionAdapter);
                                } catch (JSONException e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "Some error Occurred :(", Toast.LENGTH_LONG).show();
                                }
                            }
                            progressDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (internet == false) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                            } else if(internet == true) {
                                progressDialog.setMessage("Something went wrong :(");
                                progressDialog.setCancelable(true);
                                Log.e("Error: ", error.toString());
                                if (progressDialog.isShowing()) {
                                } else
                                    Toast.makeText(getContext(), "Please try again!", Toast.LENGTH_SHORT).show();
                            }
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

                    select_session.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                selected_s="Select";
                                fattendence.setBackgroundColor(Color.parseColor("#3E7C62"));
                                fattendence.setEnabled(true);
                                lattendence.setBackgroundColor(Color.parseColor("#3E7C62"));
                                lattendence.setEnabled(true);
                            }
                            else {
                                progressDialog.setMessage("Please Wait!");
                                progressDialog.show();
                                String session_is = (String) select_session.getSelectedItem();
                                Log.e("Selected Session: ", session_is);
                                selected_s = session_is;
                                int j;
                                for (j = 0; j < session_length; j++) {
                                    if (session_name[j].equals(session_is))
                                        break;
                                    else
                                        continue;
                                }
                                String id_is1 = session_id[j];
                                First_Attendence.session_id = id_is1;
                                Last_Attendence.session_id = id_is1;
                                Log.e("Session ID: ", id_is1);
                                String attendence_check_url1 = "http://leap-opa.ap-south-1.elasticbeanstalk.com/check_batch_session?";
                                String attendence_check_url2 = "batch="+id_is+"&session="+id_is1;
                                String attendence_check_url = attendence_check_url1+attendence_check_url2;
                                Log.e("ATTENDENCE URL: ", attendence_check_url);

                                RequestQueue requestQueue2 = Volley.newRequestQueue(getContext());
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, attendence_check_url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            Log.e("ATTENDENCE RESPONSE: ", response.toString());
                                            String check_fattendence = response.getString( "first_attendace_created_at");
                                            Log.e("First Att", check_fattendence);
                                            String check_lattendence = response.getString( "last_attendace_created_at");
                                            Log.e("Last Att", check_lattendence);
                                            if(check_fattendence.length()>0){
                                                Log.e("hdbfjd", "hebfj");
                                                fattendence.setBackgroundColor(Color.parseColor("#D3D3D3"));
                                                fattendence.setEnabled(false);
                                                lattendence.setEnabled(true);
                                            } else if(check_fattendence.length()<=0){
                                                Log.e("hdbflgjd", "ABHINAYhebfj");
                                                fattendence.setEnabled(true);
                                                lattendence.setBackgroundColor(Color.parseColor("#D3D3D3"));
                                                lattendence.setEnabled(false);
                                            }
                                        } catch (JSONException e) {
                                            progressDialog.dismiss();
                                            e.printStackTrace();
                                            Toast.makeText(getContext(), "Some error Occurred :(", Toast.LENGTH_LONG).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (internet == false) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                                        } else if(internet == true) {
                                            progressDialog.setMessage("Something went wrong :(");
                                            progressDialog.setCancelable(true);
                                            Log.e("Error: ", error.toString());
                                            if (progressDialog.isShowing()) {
                                            } else
                                                Toast.makeText(getContext(), "Please try again!", Toast.LENGTH_SHORT).show();
                                        }
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
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fattendence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first_attendence();
            }
        });

        lattendence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                last_attendence();
            }
        });
    }
    public void first_attendence() {
        if (e_Date.equals("") || selected_b.equals("Select") || selected_s.equals("Select")) {
            Toast.makeText(getContext(), "Please fill in all the details!", Toast.LENGTH_SHORT).show();
        } else {
        String attendence_date = e_Date;
        First_Attendence.date = attendence_date;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        First_Attendence first_attendence = new First_Attendence();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, first_attendence);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        trainer.flag = 2;
    }
    }
    public void last_attendence(){
        if (e_Date.equals("") || selected_b.equals("Select") || selected_s.equals("Select")) {
            Toast.makeText(getContext(), "Please fill in all the details!", Toast.LENGTH_SHORT).show();
        } else {
            String attendence_date = e_Date;
            Last_Attendence.date = attendence_date;
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Last_Attendence last_attendence = new Last_Attendence();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content_frame, last_attendence);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
            trainer.flag = 2;
        }
    }
}