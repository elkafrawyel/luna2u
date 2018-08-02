package com.apps.tv.luna2u.ui.tv.tv_activities;

import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.apps.tv.luna2u.R;
import com.apps.tv.luna2u.ui.tv.tv_dialogs.LogOutDialog;

import java.util.Objects;

public class TV_LogOut_Activity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_log_out_holder);

        //use a leanback window to get inform user before logout
        GuidedStepSupportFragment.addAsRoot(
                Objects.requireNonNull(this),
                new LogOutDialog(), android.R.id.content);
    }
}
