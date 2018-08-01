package com.apps.hmaserv.luna2u.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apps.hmaserv.luna2u.NewApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VolleySingleton {

    private Context context;
    private final static long CacheHitRefresh = 60 * 60 * 1000;
    private final static long CacheExpired = 2 * 24 * 60 * 60 * 1000;//2 days
    public static String RequestKey="Luna2u";
    @SuppressLint("StaticFieldLeak")
    private static VolleySingleton mInstance=null;
    private RequestQueue mRequestQueue;
    public VolleySingleton(){
        mRequestQueue= Volley.newRequestQueue(NewApplication.getAppContext());
    }

    public static synchronized VolleySingleton getInstance(){
        if (mInstance==null)
            mInstance=new VolleySingleton();

        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }

    public StringRequest makeStringResponse(final String url, final HashMap<String, String> map, final VolleyCallback callback, final JsonVolleyCallbackError errorCallback) {
        Log.i("map",map.toString());
        return new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Url", url);
                Log.d("response", response);
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorCallback.onError(error);
                Log.d("error", error.toString());
            }
        }){@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return CacheVolley(response);
            }
        };
    }

    public StringRequest makeStringResponse(final String url, final VolleyCallback callback, final JsonVolleyCallbackError errorCallback) {
        return new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Url", url);
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorCallback.onError(error);
                Log.e("error", error.toString());
            }
        }) {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return CacheVolley(response);
            }
        };

    }

    public StringRequest makeStringResponse(Boolean cache,final String url, final VolleyCallback callback, final JsonVolleyCallbackError errorCallback) {
        return new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Url", url);
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorCallback.onError(error);
                Log.e("error", error.toString());
            }
        });

    }

    @NonNull
    private Response<String> CacheVolley(NetworkResponse response) {
        try {
            Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
            if (cacheEntry == null) {
                cacheEntry = new Cache.Entry();
            }
            long now = System.currentTimeMillis();
            final long cacheExpire = now + CacheHitRefresh;
            final long ttl = now + CacheExpired;
            cacheEntry.data = response.data;
            cacheEntry.softTtl = cacheExpire;
            cacheEntry.ttl = ttl;
            String headerValue;
            headerValue = response.headers.get("Date");
            if (headerValue != null) {
                cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
            }
            headerValue = response.headers.get("Last-Modified");
            if (headerValue != null) {
                cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
            }
            cacheEntry.responseHeaders = response.headers;
            final String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            return Response.success(jsonString, cacheEntry);
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    public interface VolleyCallback {
        void onSuccess(String result) throws JSONException;
    }

    public interface JsonVolleyCallback {
        void onSuccess(JSONObject result) throws JSONException;
    }

    public interface JsonVolleyCallbackError {
        void onError(VolleyError error);
    }
}
