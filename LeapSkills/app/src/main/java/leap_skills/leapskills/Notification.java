package leap_skills.leapskills;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Notification extends Fragment {
    public static String unique_id, token, user;
    TableLayout tb;
    ProgressDialog progressDialog;
    public static final String PREFS_NAME10 = "Notification";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onViewCreated(view, savedInstanceState);
        tb = (TableLayout) view.findViewById(R.id.attendence_table10);
        SharedPreferences current = getActivity().getSharedPreferences(PREFS_NAME10, 0);
        SharedPreferences.Editor editor = current.edit();
        editor.putString("role", user);
        editor.commit();

        /*SharedPreferences current1 = getActivity().getSharedPreferences(PREFS_NAME10, 0);
        String saved_user = current1.getString("role", "");
        if(saved_user.length()>0 && saved_user.equals("student")){
            third.flag = 2;
        }
        else if(saved_user.length()>0 && saved_user.equals("trainer")){
            trainer.flag = 2;
        }
        else if(saved_user.length()>0 && saved_user.equals("pm")){
            pm.flag = 2;
        }*/

        if(user.equals("student")){
            third.flag = 2;
        }
        else if(user.equals("trainer")){
            trainer.flag = 2;
        }
        else if(user.equals("pm")){
            pm.flag = 2;
        }

        final Typeface font = ResourcesCompat.getFont(getContext(), R.font.worksans_medium);

            progressDialog = new ProgressDialog(getContext());
            //progressDialog.setTitle("Please Wait!");
            progressDialog.setMessage("We are fetching the data !");
            progressDialog.setCancelable(false);
            progressDialog.show();

        String url = "http://leap-opa.ap-south-1.elasticbeanstalk.com/get_notification/" + unique_id;

        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()==0){
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Notifications to show!", Toast.LENGTH_SHORT).show();
                }
                try {
                    TableRow row1[] = new TableRow[response.length()];
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject details = (JSONObject) response.get(i);
                        String type = details.getString("type");
                        String date = details.getString("created_at");
                        String body = details.getString("body");

                        row1[i] = new TableRow(getContext());
                        row1[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));


                        TextView tv2 = new TextView(getContext());
                        tv2.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv2.setText(date);
                        tv2.setTextColor(Color.parseColor("#222222"));
                        tv2.setTypeface(font);
                        tv2.setGravity(Gravity.CENTER);
                        row1[i].addView(tv2, 150, 100);

                        TextView tv3 = new TextView(getContext());
                        tv3.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv3.setText(type);
                        tv3.setTextColor(Color.parseColor("#222222"));
                        tv3.setTypeface(font);
                        tv3.setPadding(20,20,20,25);
                        tv3.setGravity(Gravity.CENTER);
                        row1[i].addView(tv3, LinearLayout.LayoutParams.WRAP_CONTENT, 100);

                        TextView tv4 = new TextView(getContext());
                        tv4.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        tv4.setText(body);
                        tv4.setTextColor(Color.parseColor("#222222"));
                        tv4.setTypeface(font);
                        row1[i].addView(tv4, LinearLayout.LayoutParams.WRAP_CONTENT, 100);

                        tb.addView(row1[i]);
                    }
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
                Log.e("Error: ", error.toString());
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
    }
}
