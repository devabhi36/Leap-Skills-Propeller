package leap_skills.leapskills;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateBatch extends Fragment {
    Spinner centre, cefr, trainer;
    EditText batch_name, batch_location, batch_stime, batch_etime;
    String stime="", etime="", selected_c, selected_s, selected_t;
    public static String token;
    Button create;
    ArrayAdapter<String> centreAdapter, cefrAdapter, trainerAdapter;
    int centre_length, cefr_length, trainer_length;
    String centre_name[], cefr_name[], trainer_name[];
    String centre_id[], cefr_id[], trainer_id[];
    String centre_id_is, cefr_id_is, trainer_id_is;
    ProgressDialog progressDialog;
    TextView cbtv4, cbtv5;
    boolean internet = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_batch, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onViewCreated(view, savedInstanceState);

        Log.e("Message: ", "Welcome Create Batch");

        centre = (Spinner) view.findViewById(R.id.center);
        batch_name = (EditText) view.findViewById(R.id.batch_name);
        batch_location = (EditText) view.findViewById(R.id.batch_location);
        batch_stime = (EditText) view.findViewById(R.id.batch_stime);
        batch_etime = (EditText) view.findViewById(R.id.batch_etime);
        cefr = (Spinner) view.findViewById(R.id.cefr_score);
        trainer = (Spinner) view.findViewById(R.id.cb_trainer);
        create = (Button) view.findViewById(R.id.fcreate_batch);

        cbtv4 = (TextView) view.findViewById(R.id.cbtv4);
        cbtv5 = (TextView) view.findViewById(R.id.cbtv5);

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            internet = true;
        } else
            internet = false;

        final Calendar calendar = Calendar.getInstance();
        cbtv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        batch_stime.setVisibility(View.VISIBLE);
                        batch_stime.setInputType(View.AUTOFILL_TYPE_NONE);
                        int hourOfDay1 = hourOfDay-12;
                        if(hourOfDay<10 && minute<10) {
                            batch_stime.setText("0" + hourOfDay + ":0" + minute + " AM");
                            stime = "0"+hourOfDay+":0"+minute;
                        } else if(hourOfDay<10 && minute>9) {
                            batch_stime.setText("0" + hourOfDay + ":" + minute + " AM");
                            stime = "0"+hourOfDay+":"+minute;
                        } else if(hourOfDay>9 && hourOfDay<12 && minute<10) {
                            batch_stime.setText(hourOfDay + ":0" + minute + " AM");
                            stime = hourOfDay+":0"+minute;
                        } else if(hourOfDay>9 && hourOfDay<12 && minute>9) {
                            batch_stime.setText(hourOfDay + ":" + minute + " AM");
                            stime = hourOfDay+":"+minute;
                        } if(hourOfDay==12 && minute<10) {
                            batch_stime.setText(hourOfDay + ":0" + minute + " PM");
                            stime = hourOfDay+":0"+minute;
                        } else if(hourOfDay==12 && minute>9) {
                            batch_stime.setText(hourOfDay + ":" + minute + " PM");
                            stime = hourOfDay+":"+minute;
                        } else if(12<hourOfDay && hourOfDay<22 && minute<10) {
                            batch_stime.setText("0" + hourOfDay1 + ":0" + minute + " PM");
                            stime = "0"+hourOfDay1+":0"+minute;
                        } else if(12<hourOfDay && hourOfDay<22 && minute>9) {
                            batch_stime.setText("0" + hourOfDay1 + ":" + minute + " PM");
                            stime = "0"+hourOfDay1+":"+minute;
                        } else if(hourOfDay>21 && minute<10) {
                            batch_stime.setText(hourOfDay1 + ":0" + minute + " PM");
                            stime = hourOfDay1+":0"+minute;
                        } else if(hourOfDay>21 && minute>9){
                            batch_stime.setText(hourOfDay1+":"+minute+" PM");
                            stime = hourOfDay1+":"+minute;
                        }
                        Log.e("StartTime", stime);
                    }
                }, hour, minute, false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        cbtv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        batch_etime.setVisibility(View.VISIBLE);
                        batch_etime.setInputType(View.AUTOFILL_TYPE_NONE);
                        int hourOfDay1 = hourOfDay-12;
                        if(hourOfDay<10 && minute<10) {
                            batch_etime.setText("0" + hourOfDay + ":0" + minute + " AM");
                            etime = "0"+hourOfDay+":0"+minute;
                        } else if(hourOfDay<10 && minute>9) {
                            batch_etime.setText("0" + hourOfDay + ":" + minute + " AM");
                            etime = "0"+hourOfDay+":"+minute;
                        } else if(hourOfDay>9 && hourOfDay<12 && minute<10) {
                            batch_etime.setText(hourOfDay + ":0" + minute + " AM");
                            etime = hourOfDay+":0"+minute;
                        } else if(hourOfDay>9 && hourOfDay<12 && minute>9) {
                            batch_etime.setText(hourOfDay + ":" + minute + " AM");
                            etime = hourOfDay+":"+minute;
                        } if(hourOfDay==12 && minute<10) {
                            batch_etime.setText(hourOfDay + ":0" + minute + " PM");
                            etime = hourOfDay+":0"+minute;
                        } else if(hourOfDay==12 && minute>9) {
                            batch_etime.setText(hourOfDay + ":" + minute + " PM");
                            etime = hourOfDay+":"+minute;
                        } else if(12<hourOfDay && hourOfDay<22 && minute<10) {
                            batch_etime.setText("0" + hourOfDay1 + ":0" + minute + " PM");
                            etime = "0"+hourOfDay1+":0"+minute;
                        } else if(12<hourOfDay && hourOfDay<22 && minute>9) {
                            batch_etime.setText("0" + hourOfDay1 + ":" + minute + " PM");
                            etime = "0"+hourOfDay1+":"+minute;
                        } else if(hourOfDay>21 && minute<10) {
                            batch_etime.setText(hourOfDay1 + ":0" + minute + " PM");
                            etime = hourOfDay1+":0"+minute;
                        } else if(hourOfDay>21 && minute>9){
                            batch_etime.setText(hourOfDay1+":"+minute+" PM");
                            etime = hourOfDay1+":"+minute;
                        }
                        Log.e("EndTime", etime);
                    }
                }, hour, minute, false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        progressDialog = new ProgressDialog(getContext());
        //progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("We are fetching the data !");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String centre_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/pm_center";
        String cefr_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/cefr_label";
        String trainer_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/list_trainer";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, centre_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()==0){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Centre to show!", Toast.LENGTH_SHORT).show();
                }
                Log.e("Centre: ", response.toString());
                centre_length = response.length();
                centre_name = new String[centre_length];
                centre_id = new String[centre_length];
                List<String> centre1 = new ArrayList<String>();
                centre1.add("Select");
                for (int i = 0; i<centre_length; i++){
                    try {
                        JSONObject jsonObject = (JSONObject)response.get(i);
                        String centre2 = jsonObject.getString("name");
                        String centre_id1 = jsonObject.getString("id");
                        centre_name[i] = centre2;
                        centre_id[i] = centre_id1;
                        centre1.add(centre2);

                        centreAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, centre1);
                        centreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        centre.setAdapter(centreAdapter);

                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Centre Exception: ", e.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Some Error Occurred :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Centre Error: ", error.toString());
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

        centre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                }
                else {
                    String centre_is = (String) centre.getSelectedItem();
                    Log.e("Selected Batch: ", centre_is);
                    selected_c = centre_is;
                    int i;
                    for (i = 0; i < centre_length; i++) {
                        if (centre_name[i].equals(centre_is))
                            break;
                        else
                            continue;
                    }
                    centre_id_is = centre_id[i];
                    Log.e("ID IS: ", centre_id_is);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RequestQueue requestQueue1 = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest1 = new JsonArrayRequest(Request.Method.GET, cefr_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.show();
                if(response.length()==0){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Score to show!", Toast.LENGTH_SHORT).show();
                }
                Log.e("Cefr: ", response.toString());
                cefr_length = response.length();
                cefr_name = new String[cefr_length];
                cefr_id = new String[cefr_length];
                List<String> cefr1 = new ArrayList<String>();
                cefr1.add("Select");
                for (int i = 0; i<cefr_length; i++){
                    try {
                        JSONObject jsonObject = (JSONObject)response.get(i);
                        String cefr2 = jsonObject.getString("name");
                        String cefr_id1 = jsonObject.getString("id");
                        cefr_name[i] = cefr2;
                        cefr_id[i] = cefr_id1;
                        cefr1.add(cefr2);

                        cefrAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, cefr1);
                        cefrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        cefr.setAdapter(cefrAdapter);

                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Cefr Exception: ", e.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Some Error Occurred :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Cefr Error: ", error.toString());
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

        cefr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                }
                else {
                    String cefr_is = (String) cefr.getSelectedItem();
                    Log.e("Selected Cefr: ", cefr_is);
                    selected_s = cefr_is;
                    int i;
                    for (i = 0; i < cefr_length; i++) {
                        if (cefr_name[i].equals(cefr_is))
                            break;
                        else
                            continue;
                    }
                    cefr_id_is = cefr_id[i];
                    Log.e("ID IS: ", cefr_id_is);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        RequestQueue requestQueue2 = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(Request.Method.GET, trainer_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.show();
                if(response.length()==0){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Trainer to show!", Toast.LENGTH_SHORT).show();
                }
                Log.e("Trainer: ", response.toString());
                trainer_length = response.length();
                trainer_name = new String[trainer_length];
                trainer_id = new String[trainer_length];
                List<String> trainer1 = new ArrayList<String>();
                trainer1.add("Select");
                for (int i = 0; i<trainer_length; i++){
                    try {
                        JSONObject jsonObject = (JSONObject)response.get(i);
                        String trainer2 = jsonObject.getString("name");
                        String trainer_id1 = jsonObject.getString("id");
                        trainer_name[i] = trainer2;
                        trainer_id[i] = trainer_id1;
                        trainer1.add(trainer2);

                        trainerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, trainer1);
                        trainerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        trainer.setAdapter(trainerAdapter);
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Trainer Exception: ", e.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Some Error Occurred :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Trainer Error: ", error.toString());
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
        requestQueue2.add(jsonArrayRequest2).setRetryPolicy(new RetryPolicy() {
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

        trainer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                }
                else {
                    String trainer_is = (String) trainer.getSelectedItem();
                    Log.e("Selected Trainer: ", trainer_is);
                    selected_t = trainer_is;
                    int i;
                    for (i = 0; i < trainer_length; i++) {
                        if (trainer_name[i].equals(trainer_is))
                            break;
                        else
                            continue;
                    }
                    trainer_id_is = trainer_id[i];
                    Log.e("ID IS: ", trainer_id_is);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        create.setEnabled(true);
        if(internet==false){
            create.setEnabled(false);
        }

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_batch();
            }
        });
    }

    public void create_batch(){
        if(stime.equals("")||etime.equals("")||selected_c.equals("Select")||selected_s.equals("Select")||selected_t.equals("Select")){
            Toast.makeText(getContext(), "Please fill in all the details!", Toast.LENGTH_SHORT).show();
        } else if(stime.equals(etime)){
            Toast.makeText(getContext(), "Start time is same as end time!", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setMessage("Please Wait!");
            progressDialog.setCancelable(false);
            progressDialog.show();
            String batch_name1 = batch_name.getText().toString();
            String batch_location1 = batch_location.getText().toString();
            String start_time1 = stime;
            String end_time1 = etime;

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("center_id", Integer.parseInt(centre_id_is));
                jsonObject.put("trainer_id", Integer.parseInt(trainer_id_is));
                jsonObject.put("batch_name", batch_name1);
                jsonObject.put("batch_location", batch_location1);
                jsonObject.put("start_time", start_time1);
                jsonObject.put("end_time", end_time1);
                jsonObject.put("cefr_level", Integer.parseInt(cefr_id_is));
            } catch (JSONException e) {
                e.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Some Error Occurred :(", Toast.LENGTH_SHORT).show();
            }
            Log.e("JSON OBJECT ", jsonObject.toString());

            String save_batch_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/pm_save_batch";
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, save_batch_url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("Save Batch Response: ", response.toString());
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Batch Created Successfully!", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    PMHome pmHome = new PMHome();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.content_frame, pmHome);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();
                    pm.flag = 1;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Save Batch Error: ", error.toString());
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
    }
}
