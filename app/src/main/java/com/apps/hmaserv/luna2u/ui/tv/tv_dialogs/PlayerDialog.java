package com.apps.hmaserv.luna2u.ui.tv.tv_dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.apps.hmaserv.luna2u.ui.phone.phone_dialogs.ChoosePlayerDialog.EXO;
import static com.apps.hmaserv.luna2u.ui.phone.phone_dialogs.ChoosePlayerDialog.VLC;


public class PlayerDialog extends Dialog{

    private Context mContext;
    @BindView(R.id.vlc_radio)
    RadioButton VLC_Player;
    @BindView(R.id.exo_radio)
    RadioButton EXO_Player;
    public PlayerDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        PlayerDialog.this.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        setContentView(R.layout.choose_player_tv);
        ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

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
}