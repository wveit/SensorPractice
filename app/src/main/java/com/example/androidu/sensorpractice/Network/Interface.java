package com.example.androidu.sensorpractice.Network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidu.sensorpractice.R;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by billp on 9/27/17.
 */

public final class Interface {
    private static Interface mInstance;
    private RequestQueue mQueue;
    private static Context context;
    private static String YELP_ACCESS_TOKEN;

    private static final String TAG = Interface.class.getSimpleName();
    private static final String YELP_SEARCH_API_URL = "https://api.yelp.com/v3/businesses/search";

    private static final String YELP_SEARCH_PARAM_TERM = "term";
    private static final String YELP_SEARCH_PARAM_LOCATION = "location";
    private static final String YELP_SEARCH_PARAM_LATITUDE = "latitude";
    private static final String YELP_SEARCH_PARAM_LONGITUDE = "longitude";
    private static final String YELP_SEARCH_PARAM_RADIUS = "radius";
    private static final String YELP_SEARCH_PARAM_CATEGORIES = "categories";
    private static final String YELP_SEARCH_PARAM_LOCALE = "locale";
    private static final String YELP_SEARCH_PARAM_LIMIT = "limit";
    private static final String YELP_SEARCH_PARAM_OFFSET = "offset";
    private static final String YELP_SEARCH_PARAM_SORT_BY = "sort_by";
    private static final String YELP_SEARCH_PARAM_PRICE = "price";
    private static final String YELP_SEARCH_PARAM_OPEN_NOW = "open_now";
    private static final String YELP_SEARCH_PARAM_OPEN_AT = "open_at";
    private static final String YELP_SEARCH_PARAM_ATTRIBUTES = "attributes";

    private Interface(Context context) {
        this.context = context;
        mQueue = getRequestQueue();
    }

    public static synchronized Interface getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Interface(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mQueue;
    }

    public StringRequest authenticateYelp(final AuthenticationCallback ac) {
        // Instantiate the RequestQueue.
        RequestQueue queue = getRequestQueue();
        String url ="https://api.yelp.com/oauth2/token";

        final String yelp_id = context.getString(R.string.yelp_fusion_id);
        final String yelp_secret = context.getString(R.string.yelp_fusion_secret);

        // Request a string response from the provided URL.
        StringRequest authRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "data:\n" + response);
                try {
                    YELP_ACCESS_TOKEN = JSON.parseYelpAuthInfo(response);
                    ac.onSuccess();
                }
                catch (JSONException e) {
                    Log.d(TAG, "Yelp API authentication failed!");
                    Log.d(TAG, "Message: " + e.toString());
                    ac.onFailure();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Network error:\n" + error.networkResponse.toString());
                ac.onFailure();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("grant_type", "client_credentials");
                params.put("client_id", yelp_id);
                params.put("client_secret", yelp_secret);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(authRequest);

        // Returns the instance of the request in case we need to cancel it
        return authRequest;
    }

    public StringRequest queryYelp(double lat, double lon, final NetworkCallback nc) {
        // Instantiate the RequestQueue.
        RequestQueue queue = getRequestQueue();
        String url ="https://api.yelp.com/v3/businesses/search?latitude=" + (int)Math.round(lat) + "&longitude=" + (int)Math.round(lon) + "&radius=1000";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "response:\n" + response);
                        try {
                            String[] names = JSON.parseYelpSearchInfo(response);
                            nc.onResponse(names[0]);
                        } catch (JSONException e) {
                            Log.d(TAG, "data malformed");
                            nc.onError("Data malformed");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "response:\n" + String.valueOf(error.networkResponse.data));
                        nc.onError(error.toString());
                    }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + YELP_ACCESS_TOKEN);

                return headers;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        // Returns the instance of the request in case we need to cancel it
        return stringRequest;
    }

    public static void cancelQuery(StringRequest sr) {
        sr.cancel();
    }

    public interface AuthenticationCallback {
        void onSuccess();
        void onFailure();
    }

    public interface NetworkCallback {
        void onResponse(String response);
        void onError(String error);
    }
}
