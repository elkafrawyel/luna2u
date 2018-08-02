package com.apps.tv.luna2u.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.apps.tv.luna2u.R;


public class Handler {

    public static String volleyErrorHandler(VolleyError error, Context context) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            return (context.getString(R.string.Connection_Error));
        } else if (error instanceof AuthFailureError) {
            return (context.getString(R.string.Connection_Error));
        } else if (error instanceof ServerError) {
            return (context.getString(R.string.Connection_Error));
        } else if (error instanceof NetworkError) {
            return (context.getString(R.string.Connection_Error));
        } else if (error instanceof ParseError) {
            return (context.getString(R.string.Connection_Error));
        } else {
            return (context.getString(R.string.Connection_Error));
        }
    }
}
