package com.apps.tv.luna2u.ui.phone.phone_activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.tv.luna2u.NewApplication;
import com.apps.tv.luna2u.R;
import com.apps.tv.luna2u.utils.CommonMethods;
import com.apps.tv.luna2u.utils.ServerURL;
import com.apps.tv.luna2u.utils.VolleySingleton;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.apps.tv.luna2u.utils.VolleySingleton.RequestKey;

public class Phone_Information extends AppCompatActivity
        implements VolleySingleton.VolleyCallback, VolleySingleton.JsonVolleyCallbackError{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.info_email)
    TextView Email;
    @BindView(R.id.info_expires)
    TextView Expires;
    @BindView(R.id.info_status)
    TextView Status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_information);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        final Drawable upArrow =  ContextCompat.getDrawable(this, R.drawable
                .abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(this, R.color.text_color), PorterDuff.Mode.SRC_ATOP);
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setTitle("Phone_Information");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String code = NewApplication.getPreferencesHelper().getActivationCode();

        if (code !=null){
            GetInformation(ServerURL.Information_Url.concat(code));
        }
    }

    CommonMethods commonMethods;
    private void GetInformation(String Url){
        commonMethods=new CommonMethods(this);
        if(commonMethods.isNetworkAvailable()) {
            RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
            mRequestQueue.add(
                    VolleySingleton.getInstance().makeStringResponse(Url,
                            Phone_Information.this
                            , Phone_Information.this)
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
            }).setTag(RequestKey);
        }else
            MDToast.makeText(this, "No Internet Connection Available.",
                    Toast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();
    }

    @Override
    public void onSuccess(String result) throws JSONException {
        JSONObject object=new JSONObject(result);
        Email.setText(getString(R.string.email).concat(object.getString("email")));
        Status.setText(getString(R.string.status).concat(object.getString("status")));
        Expires.setText(getString(R.string.expires).concat(getDateCurrentTimeZone
                (Long.parseLong(object.getString("expires")))));
    }

    public  String getDateCurrentTimeZone(long timestamp) {
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
            MDToast.makeText(this, "Error catching some information",
                    Toast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
        }
        return "";
    }


    @Override
    public void onError(VolleyError error) {

    }
}
