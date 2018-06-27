package com.apps.hmaserv.luna2u.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.apps.hmaserv.luna2u.R;

import static android.content.Context.UI_MODE_SERVICE;

public class CommonMethods {

    Context mContext;

    public CommonMethods(Context mContext) {
        this.mContext = mContext;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static int getDeviceType(Context context) {
        boolean isTouch = context.getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);

        int touchDeviceType = context.getResources().getInteger(R.integer.device_type);

        if (isTouch) {
            return touchDeviceType;
        } else {
            return ServerURL.DEVICE_TYPE_TV;
        }

    }

}
