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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PMHome extends Fragment {
    public static String token, unique_id;
    Spinner centre;
    int centre_length;
    String centre_name[];
    String centre_id[];
    String token2;
    ArrayAdapter<String> centreAdapter;
    String centre_id_is;
    ProgressDialog progressDialog;
    boolean internet = false;
    public static final String PREFS_NAME1 = "FetchedData";
    Button pre_assess1, pm_stu_info1, batch_info, batch_allot;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pm_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onViewCreated(view, savedInstanceState);
        centre = (Spinner) view.findViewById(R.id.select_centre_pm);
        pre_assess1 = (Button) view.findViewById(R.id.pre_assess1);
        pm_stu_info1 = (Button) view.findViewById(R.id.pm_stu_info1);
        batch_info = (Button) view.findViewById(R.id.batch_info);
        batch_allot = (Button) view.findViewById(R.id.batch_allot);

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
                }
                else
                    Toast.makeText(getContext(), "SOME ERROR OCCURRED :(", Toast.LENGTH_SHORT).show();
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


        SharedPreferences saved_data = getActivity().getSharedPreferences(PREFS_NAME1, 0);
        String length = saved_data.getString("LoopLength", "0");
        final int length1 = Integer.parseInt(length);
        if (length1>0) {
            centre_name = new String[length1];
            centre_id = new String[length1];
            List<String> centre1 = new ArrayList<String>();
            centre1.add("Select");
            for (int x=0; x<length1; x++){
                String option = saved_data.getString("Option"+String.valueOf(x+1), null);
                String option_id = saved_data.getString("ID"+String.valueOf(x+1), null);
                centre_name[x] = option;
                centre_id[x] = option_id;
                centre1.add(option);
                centreAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, centre1);
                centreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                centre.setAdapter(centreAdapter);
            }
            centre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        centre_id_is = "Select";
                    } else {
                        String centre_is = (String) centre.getSelectedItem();
                        Log.e("Selected Centre: ", centre_is);
                        int i;
                        for (i = 0; i < length1; i++) {
                            if (centre_name[i].equals(centre_is))
                                break;
                            else
                                continue;
                        }
                        centre_id_is = centre_id[i];
                        Log.e("ID IS: ", centre_id_is);
                        Batch_Information.centre1 = centre_is;
                    }
                    pm.centre_id = centre_id_is;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            progressDialog = new ProgressDialog(getContext());
            //progressDialog.setTitle("Please Wait!");
            progressDialog.setMessage("We are fetching the data !");
            progressDialog.setCancelable(false);
            progressDialog.show();

            String centre_url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/pm_center";

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
                    SharedPreferences data = getActivity().getSharedPreferences(PREFS_NAME1, 0);
                    SharedPreferences.Editor editor = data.edit();
                    editor.putString("LoopLength", String.valueOf(centre_length));
                    for (int i = 0; i < centre_length; i++) {
                        try {
                            JSONObject jsonObject = (JSONObject) response.get(i);
                            String centre2 = jsonObject.getString("name");
                            String centre_id1 = jsonObject.getString("id");
                            centre_name[i] = centre2;
                            centre_id[i] = centre_id1;
                            centre1.add(centre2);

                            centreAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, centre1);
                            centreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            centre.setAdapter(centreAdapter);
                            //SharedPreferences data = getActivity().getSharedPreferences(PREFS_NAME1, 0);
                            editor.putString("Option" + String.valueOf(i + 1), centre2);
                            editor.putString("ID" + String.valueOf(i + 1), centre_id1);
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            Log.e("Centre Exception: ", e.toString());
                        }
                    }
                    editor.commit();
                    progressDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (internet == false) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Please Check your internet connection!", Toast.LENGTH_LONG).show();
                    } else if (internet == true) {
                    progressDialog.setMessage("Something went wrong :(");
                    progressDialog.setCancelable(true);
                    Log.e("Centre Error: ", error.toString());
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
                    Toast.makeText(getContext(), "Slow Internet Connection :(", Toast.LENGTH_SHORT).show();
                }
            });

            centre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        centre_id_is = "Select";
                    } else {
                        String centre_is = (String) centre.getSelectedItem();
                        Log.e("Selected Centre: ", centre_is);
                        int i;
                        for (i = 0; i < centre_length; i++) {
                            if (centre_name[i].equals(centre_is))
                                break;
                            else
                                continue;
                        }
                        centre_id_is = centre_id[i];
                        Log.e("ID IS: ", centre_id_is);
                        Batch_Information.centre1 = centre_is;
                    }
                    pm.centre_id = centre_id_is;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        pre_assess1.setEnabled(true);
        pm_stu_info1.setEnabled(true);
        batch_info.setEnabled(true);
        batch_allot.setEnabled(true);

        if(internet==false){
            Log.e("ug", "pg");
            pre_assess1.setEnabled(false);
            pm_stu_info1.setEnabled(false);
            batch_info.setEnabled(false);
            batch_allot.setEnabled(false);
        }
    }
}
