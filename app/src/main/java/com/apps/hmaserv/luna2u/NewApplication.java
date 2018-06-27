package com.apps.hmaserv.luna2u;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.apps.hmaserv.luna2u.data.local.PreferencesHelper;
import com.apps.hmaserv.luna2u.utils.CommonMethods;
import com.facebook.stetho.Stetho;

public class NewApplication extends Application {
    private static NewApplication mInstance;
    private static PreferencesHelper preferencesHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Stetho.initializeWithDefaults(this);
    }

    public static PreferencesHelper getPreferencesHelper() {
        if (preferencesHelper==null)
            preferencesHelper = new PreferencesHelper(getAppContext());

        return preferencesHelper;
    }

    public static Context getAppContext(){
        return mInstance.getApplicationContext();
    }

}