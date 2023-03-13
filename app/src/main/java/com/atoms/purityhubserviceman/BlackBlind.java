package com.atoms.purityhubserviceman;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by BlackBlind567@Github on 08/08/19.
 */

public class BlackBlind {

    private static final String TAG = "RespBlind";
    private RequestQueue requestQueue;
    private String url;
    private Map<String, String> params;
    private Map<String, String> headers;
    private String authId;
    private String token;
    private String authPassword;
    private Boolean headersValue;
    private Context context;

    public BlackBlind(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.params = new HashMap<>();
        this.headers = new HashMap<>();
    }

    public void addParams(String key, String value) {
        params.put(key, value);
        Log.d(TAG, "params: " + params);
    }

    public void requestUrl(String url){
        this.url = url;
        Log.e(TAG, "url: " + url);
    }

    public void headersRequired(Boolean check) {
        this.headersValue = check;
    }

    public void authToken(String token) {
        this.token = token;
    }

    public void addHeaders(String key, String value) {
        headers.put(key, value);
    }

    public void basicAuthId(String id) {
        this.authId = id;
    }

    public void basicAuthPassword(String password) {
        this.authPassword = password;
    }

    public void executeRequest(int method, final VolleyCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response2: " + response.toString());
                        callback.getResponse(response.toString());


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "error2: " + error.toString());
                error.printStackTrace();
                callback.getError(error.toString());

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headersValue) {
//                    String auth = authId + ":" + authPassword;
//                    byte[] data = auth.getBytes();
//                    String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
                    headers.put("Authorization", "Bearer " + token);
//                    headers.put("accept-language","EN");
//                    headers.put("Content-type","application/json");
//                    headers.put("Accept","application/json");
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.getCache().clear();
        requestQueue.add(jsonObjectRequest);
    }
}