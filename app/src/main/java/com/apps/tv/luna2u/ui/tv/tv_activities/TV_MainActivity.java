package com.apps.tv.luna2u.ui.tv.tv_activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.tv.luna2u.NewApplication;
import com.apps.tv.luna2u.R;
import com.apps.tv.luna2u.ui.tv.tv_Fragments.TV_MainFragment;
import com.apps.tv.luna2u.utils.ServerURL;
import com.apps.tv.luna2u.utils.VolleySingleton;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import static com.apps.tv.luna2u.utils.VolleySingleton.RequestKey;

public class TV_MainActivity extends FragmentActivity {

    TV_MainFragment fragment;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tv_main_activity);

//     DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;



        fragment=new TV_MainFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.TV_MainContainer,fragment).commit();

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                fragment.myOnKeyDown(keyCode);
                return true;
            default:
                Log.d("OnKey", String.valueOf(keyCode));
                return super.onKeyDown(keyCode, event);
        }
    }
}
