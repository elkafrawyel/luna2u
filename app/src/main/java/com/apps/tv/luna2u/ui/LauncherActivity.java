package com.apps.tv.luna2u.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.apps.tv.luna2u.NewApplication;
import com.apps.tv.luna2u.ui.phone.phone_activities.Phone_Splash;
import com.apps.tv.luna2u.ui.tv.tv_activities.TV_MainActivity;
import com.apps.tv.luna2u.ui.tv.tv_activities.TV_LoginActivity;
import com.apps.tv.luna2u.utils.CommonMethods;
import com.apps.tv.luna2u.utils.ServerURL;

public class LauncherActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, LauncherActivity.class);
        //clearing all activities before that.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static final String TV = "tv";
    public static final String MOBILE = "mobile";
    public static final String TABLET = "tablet";
    public String device_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int type=CommonMethods.getDeviceType(this);
        switch (type){
            case ServerURL.DEVICE_TYPE_PHONE:
                device_type = MOBILE;
                break;

            case ServerURL.DEVICE_TYPE_TABLET:
            case ServerURL.DEVICE_TYPE_TV:
                device_type = TV;
                break;
        }


        final String Code = NewApplication.getPreferencesHelper().getActivationCode();
        if (device_type.equals(TV)) {
            if (Code == null) {
                startActivity(new Intent(LauncherActivity.this, TV_LoginActivity.class));
                LauncherActivity.this.finish();
            } else {
                startActivity(new Intent(LauncherActivity.this, TV_MainActivity.class));
                LauncherActivity.this.finish();
            }
        } else {
            startActivity(new Intent(LauncherActivity.this, Phone_Splash.class));
            LauncherActivity.this.finish();
        }
    }
}
