package com.apps.hmaserv.luna2u.ui.tv.tv_activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.utils.CommonMethods;
import com.apps.hmaserv.luna2u.utils.Handler;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TV_LoginActivity extends Activity
        implements VolleySingleton.VolleyCallback,VolleySingleton.JsonVolleyCallbackError{


    public static void start(Context context) {
        Intent intent = new Intent(context, TV_LoginActivity.class);
        //clearing all activities before that.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @BindView(R.id.login_activity_info_container_cl)
    ConstraintLayout infoContainerLayout;
    @BindView(R.id.login_activity_logo_imgv)
    ImageView appLogo;
    @BindView(R.id.login_activity_activation_code_et)
    EditText activationCodeEditText;
    @BindView(R.id.login_activity_activate_btn)
    Button loginBtn;

    private String Code_fromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tv_login_activity);
        ButterKnife.bind(this);
        //hide keyboard from tv
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        activationCodeEditText.setHintTextColor
                (ContextCompat.getColor(this, R.color.colorWhite));
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    CommonMethods commonMethods=new CommonMethods(this);
    private void login() {
        if (commonMethods.isNetworkAvailable()) {
            final String activationCode = activationCodeEditText.getText().toString().trim();
            if (activationCode.isEmpty()) {
                MDToast.makeText(this, "ACTIVATION CODE IS EMPTY. PLEASE ENTER A VALID ONE."
                        , Toast.LENGTH_LONG,MDToast.TYPE_INFO).show();
            }else {
                Code_fromUser=activationCode;
                CheckCodeValidation(ServerURL.Code_Url.concat(Code_fromUser));
            }
        } else {
            MDToast.makeText(this,
                    "You are not connected. Please connect and try again.",
                    Toast.LENGTH_LONG,MDToast.TYPE_WARNING).show();
        }
    }

    private void CheckCodeValidation(String Url){
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mRequestQueue.add(
                VolleySingleton.getInstance().makeStringResponse(Url,
                        TV_LoginActivity.this
                        , TV_LoginActivity.this)
        ).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

    @Override
    public void onSuccess(String result) throws JSONException {
        JSONObject Groups= new JSONObject(result);
        String code=Groups.getString("code");
        String status=Groups.getString("status");
        String message=Groups.getString("message");

        if (code.equals("0")&&status.equals("success")&&message.equals("Valid")){
            NewApplication.getPreferencesHelper().setActivationCode(Code_fromUser);
            startActivity(new Intent(TV_LoginActivity.this, TV_LiveActivity.class));
            TV_LoginActivity.this.finish();
        }else if (code.equals("2")&&status.equals("error")&&message.equals("Subscription expired")){
            MDToast.makeText(this, "Make Sure You have a correct Code!!",
                    Toast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();
        }
    }

    @Override
    public void onError(VolleyError error) {
        Handler.volleyErrorHandler(error,this);
    }
}
