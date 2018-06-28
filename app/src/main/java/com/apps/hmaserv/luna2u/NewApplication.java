package com.apps.hmaserv.luna2u;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.apps.hmaserv.luna2u.data.local.PreferencesHelper;
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


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}