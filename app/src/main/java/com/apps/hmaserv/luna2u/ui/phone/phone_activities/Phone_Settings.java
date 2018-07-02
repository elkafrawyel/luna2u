package com.apps.hmaserv.luna2u.ui.phone.phone_activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.apps.hmaserv.luna2u.ui.phone.phone_dialogs.Phone_ChoosePlayerDialog.EXO;
import static com.apps.hmaserv.luna2u.ui.phone.phone_dialogs.Phone_ChoosePlayerDialog.VLC;

public class Phone_Settings extends AppCompatActivity {

    @BindView(R.id.toolbar_settings)
    Toolbar toolbar;
    @BindView(R.id.vlc_radio)
    RadioButton VLC_Player;
    @BindView(R.id.exo_radio)
    RadioButton EXO_Player;
    @BindView(R.id.withImage_radio)
    RadioButton WithImage;
    @BindView(R.id.withoutImage_radio)
    RadioButton WithOutImage;
    @BindView(R.id.phone_settings_activity_log_out)
    TextView LogOut;
    @BindView(R.id.phone_settings_activity_info)
    TextView information;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color));
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(this, R.color.text_color), PorterDuff.Mode.SRC_ATOP);
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        Set_Current_Player();

        VLC_Player.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (EXO_Player.isChecked()){
                        EXO_Player.setChecked(false);
                    }
                    NewApplication.getPreferencesHelper().setPlayer(VLC);
                }
            }
        });


        EXO_Player.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (VLC_Player.isChecked()){
                        VLC_Player.setChecked(false);
                    }
                    NewApplication.getPreferencesHelper().setPlayer(EXO);
                }
            }
        });


        Set_Current_Image_Selection();

        WithOutImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (WithImage.isChecked()){
                        WithImage.setChecked(false);
                    }
                    NewApplication.getPreferencesHelper().setChannelImage("0");
                }
            }
        });


        WithImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (WithOutImage.isChecked()){
                        WithOutImage.setChecked(false);
                    }
                    NewApplication.getPreferencesHelper().setChannelImage("1");
                }
            }
        });
    }


    private void Set_Current_Player() {
        String player = NewApplication.getPreferencesHelper().getPlayer();
        if (player==null)
            EXO_Player.setChecked(true);
        else {
            if (player.equals(VLC))
                VLC_Player.setChecked(true);
            else
                EXO_Player.setChecked(true);
        }
    }

    private void Set_Current_Image_Selection() {
        String player = NewApplication.getPreferencesHelper().getChannelImage();
        if (player==null)
            WithImage.setChecked(true);
        else {
            if (player.equals("1"))
                WithImage.setChecked(true);
            else
                WithOutImage.setChecked(true);
        }
    }

    @OnClick(R.id.phone_settings_activity_log_out)
    void LogOut_Clicked(){
        NewApplication.getPreferencesHelper().clear();
        Phone_Splash.start(this);
    }

    @OnClick(R.id.phone_settings_activity_info)
    void Information_Clicked(){
        startActivity(new Intent(this,Phone_Information.class));
    }
}
