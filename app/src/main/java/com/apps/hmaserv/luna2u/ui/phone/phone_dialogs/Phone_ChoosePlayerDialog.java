package com.apps.hmaserv.luna2u.ui.phone.phone_dialogs;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.FloatProperty;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.apps.hmaserv.luna2u.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Phone_ChoosePlayerDialog extends Dialog {

    private Context mContext;
    public static final String VLC="vlc_player";
    public static final String EXO="exo_player";
    private IPlayerChooseCallback callback;
    private Boolean Checked=false;
    @BindView(R.id.btn_choose_Exo)
    Button btn_EXO;
    @BindView(R.id.btn_choose_Vlc)
    Button btn_VLC;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    public Phone_ChoosePlayerDialog(@NonNull Context context, IPlayerChooseCallback callback) {
        super(context);
        mContext = context;
        this.callback=callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Phone_ChoosePlayerDialog.this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        setContentView(R.layout.choose_phone_player);
        ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Checked=isChecked;
            }
        });

        btn_VLC.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btn_VLC.setBackground(mContext.getResources().getDrawable(R.drawable.phone_un_choose_btn));
                    btn_VLC.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                } else {
                    btn_VLC.setBackground(mContext.getResources().getDrawable(R.drawable.phone_choose_btn));
                    btn_VLC.setTextColor(mContext.getResources().getColor(R.color.text_color));

                }
            }
        });

        btn_EXO.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btn_EXO.setBackground(mContext.getResources().getDrawable(R.drawable.phone_un_choose_btn));
                    btn_EXO.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

                } else {
                    btn_EXO.setBackground(mContext.getResources().getDrawable(R.drawable.phone_choose_btn));
                    btn_EXO.setTextColor(mContext.getResources().getColor(R.color.text_color));
                }
            }
        });

        checkBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    checkBox.setTextColor(mContext.getResources().getColor(R.color.colorWhite));

                } else {
                    checkBox.setTextColor(mContext.getResources().getColor(R.color.text_color));
                }
            }
        });
    }

    @OnClick(R.id.btn_choose_Vlc)
    void VLC(){
        callback.whichPlayer(VLC,Checked);
        Phone_ChoosePlayerDialog.this.dismiss();
    }

    @OnClick(R.id.btn_choose_Exo)
    void EXO(){
        callback.whichPlayer(EXO,Checked);
        Phone_ChoosePlayerDialog.this.dismiss();
    }

    public interface IPlayerChooseCallback{
        void whichPlayer(String player,Boolean Ask_again);
    }
}