package com.apps.hmaserv.luna2u.ui.tv.tv_dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.utils.CommonMethods;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;


public class InformationDialog extends Dialog
        implements VolleySingleton.VolleyCallback, VolleySingleton.JsonVolleyCallbackError{


    private Context mContext;
    @BindView(R.id.info_email)
    TextView Email;
    @BindView(R.id.info_expires)
    TextView Expires;
    @BindView(R.id.info_status)
    TextView Status;    public InformationDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        InformationDialog.this.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        setContentView(R.layout.dialog_info_tv);
        ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        String code = NewApplication.getPreferencesHelper().getActivationCode();

        if (code !=null){
            GetInformation(ServerURL.Information_Url.concat(code));
        }
    }

    private void GetInformation(String Url){
        CommonMethods commonMethods = new CommonMethods(mContext);
        if(commonMethods.isNetworkAvailable()) {
            RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
            mRequestQueue.add(
                    VolleySingleton.getInstance().makeStringResponse(Url,
                            InformationDialog.this
                            , InformationDialog.this)
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
        }else
            MDToast.makeText(mContext, "No Internet Connection Available.",
                    Toast.LENGTH_SHORT,MDToast.TYPE_INFO).show();
    }

    @Override
    public void onSuccess(String result) throws JSONException {
        JSONObject object=new JSONObject(result);
        Email.setText("Email : "+object.getString("email"));
        Status.setText("Status : "+object.getString("status"));
        Expires.setText("Expires On : "+getDateCurrentTimeZone(Long.parseLong(object.getString("expires"))));
    }

    private String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            //String myFormat = "EEE, MMM d";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }


    @Override
    public void onError(VolleyError error) {

    }
}