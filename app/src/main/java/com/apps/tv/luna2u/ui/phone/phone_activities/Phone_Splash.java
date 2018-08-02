package com.apps.tv.luna2u.ui.phone.phone_activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.apps.tv.luna2u.NewApplication;
import com.apps.tv.luna2u.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Phone_Splash extends Activity {

    @BindView(R.id.splash)
    ImageView SplashImage;


    public static void start(Context context) {
        Intent intent = new Intent(context, Phone_Splash.class);
        //clearing all activities before that.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_splash);
        ButterKnife.bind(this);

        final String Code = NewApplication.getPreferencesHelper().getActivationCode();
          Thread myThread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(3000);
                        if (Code == null) {
                            startActivity(new Intent(Phone_Splash.this, Phone_Login.class));
                            Phone_Splash.this.finish();
                        }else {
                            startActivity(new Intent(Phone_Splash.this, Phone_Groups.class));
                            Phone_Splash.this.finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            myThread.start();
    }
}
