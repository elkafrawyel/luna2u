package com.apps.hmaserv.luna2u.ui.phone.phone_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Phone_Login extends AppCompatActivity
implements VolleySingleton.VolleyCallback,VolleySingleton.JsonVolleyCallbackError{

    @BindView(R.id.login_login_btn)
    Button Login;
    @BindView(R.id.login_activation_et)
    TextView activation_code;

    private String Code_fromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_login_btn)
    void LoginClicked() {
        if (TextUtils.isEmpty(activation_code.getText().toString()))
            activation_code.setError("Field Can't be empty");
        else {
            Code_fromUser=activation_code.getText().toString();
            CheckCodeValidation(ServerURL.Code_Url.concat(Code_fromUser));
        }
    }

    private void CheckCodeValidation(String Url){
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mRequestQueue.add(
                VolleySingleton.getInstance().makeStringResponse(Url,
                        Phone_Login.this
                        , Phone_Login.this)
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
            startActivity(new Intent(Phone_Login.this, Phone_Groups.class));
            Phone_Login.this.finish();
        }else if (code.equals("2")&&status.equals("error")&&message.equals("Subscription expired")){
            MDToast.makeText(this,
                    "Make Sure You have a correct Code!!",
                    Toast.LENGTH_SHORT,MDToast.TYPE_INFO).show();
        }
    }

    @Override
    public void onError(VolleyError error) {

    }
}
