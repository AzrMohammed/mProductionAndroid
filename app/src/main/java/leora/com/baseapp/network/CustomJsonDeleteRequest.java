package leora.com.baseapp.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

import leora.com.baseapp.App;
import leora.com.baseapp.Constants;
import leora.com.baseapp.supportfiles.CommonMethods;
import leora.com.baseapp.supportfiles.CommonNetwork;
import leora.com.baseapp.utils.ApiUtils;
import leora.com.baseapp.utils.ValueUtils;
import leora.com.baseapp.utils.ViewUtils;

/**
 * Created by AZR on 09-03-2018.
 */

public class CustomJsonDeleteRequest {

    ProgressDialog pDialog;


    public CustomJsonDeleteRequest(final Activity activity, Boolean show_progress, String progress_text, int method, String url, JSONObject jsonRequest, final CustomDeleteResponseListener customDeleteResponseListener) {
        proceedDeleteAPI(activity, show_progress, progress_text, method, url, jsonRequest, customDeleteResponseListener);
    }


    public void proceedDeleteAPI(final Activity activity, Boolean show_progress, String progress_text, int method, final String url, JSONObject jsonRequest, final CustomDeleteResponseListener customDeleteResponseListener) {

        if (activity == null)
            show_progress = false;
        final Boolean show_progress_f = show_progress;


        try {
            RequestQueue requestQueue;
            if (activity != null)
                requestQueue = Volley.newRequestQueue(activity);
            else
                requestQueue = Volley.newRequestQueue(App.getAppContext());

            final String URL = url;

            if (show_progress_f) {
                pDialog = ViewUtils.showProgressBar(pDialog, activity, progress_text);
            }

            Log.e( "proceedDeleteAPI: ",jsonRequest+"+=" );
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, URL, jsonRequest, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {


                    Log.e("rec_response", URL + "===" + response);

                    if (show_progress_f)
                        ViewUtils.hideProgressBar(pDialog);
                    try {
                        JSONObject jsonObject = new JSONObject(response + "");

                        if (jsonObject.optBoolean("status")) {
                            if (activity != null && (!jsonObject.optString("status_message").equals(ValueUtils.RESPONSE_NO_DISPLAY)))
                                Toast.makeText(activity, jsonObject.optString("status_message"), Toast.LENGTH_SHORT).show();
                            customDeleteResponseListener.responseSuccess(jsonObject);
                        } else {
                            customDeleteResponseListener.responseFailure(jsonObject);
                            if (activity != null && (!jsonObject.optString("status_message").equals(ValueUtils.RESPONSE_NO_DISPLAY)))
                                Toast.makeText(activity, jsonObject.optString("status_message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        if (activity != null)
                            Toast.makeText(activity, "Error occurred. Kindly try again", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        Log.e("jobj_err", "===" + e);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
//                    Log.e("rec_response_err", URL+"===errorr");


                    String message = null;
                    if (volleyError instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ServerError) {
                        message = "Error occurred. Kindly try again";
//                        "The server could not be found. Please try again after some time!!"
                    } else if (volleyError instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (volleyError instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Error occurred. Kindly try again";
//                        "Connection TimeOut! Please check your internet connection."
                    }

                    Log.e("error_url", "===" + URL + "==" + message);


                    if (show_progress_f)
                        ViewUtils.hideProgressBar(pDialog);
//                    Log.e("VOLLEYfai", error.toString());
                    customDeleteResponseListener.responseError(message);
                    if (activity != null)
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    7000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            if (activity != null)
                Log.e("reqqex", url + "===" + activity.getClass().getSimpleName() + "==");
            else
                Log.e("reqqex", url + "===" + "==");
            e.printStackTrace();
        }
    }


}
