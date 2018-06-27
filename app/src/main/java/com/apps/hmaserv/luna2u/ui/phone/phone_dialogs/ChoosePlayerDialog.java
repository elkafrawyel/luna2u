package com.apps.hmaserv.luna2u.ui.phone.phone_dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.apps.hmaserv.luna2u.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChoosePlayerDialog extends Dialog {

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
    public ChoosePlayerDialog(@NonNull Context context,IPlayerChooseCallback callback) {
        super(context);
        mContext = context;
        this.callback=callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ChoosePlayerDialog.this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        setContentView(R.layout.choose_player);
        ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Checked=isChecked;
            }
        });
    }

    @OnClick(R.id.btn_choose_Vlc)
    void VLC(){
        callback.whichPlayer(VLC,Checked);
        ChoosePlayerDialog.this.dismiss();
    }

    @OnClick(R.id.btn_choose_Exo)
    void EXO(){
        callback.whichPlayer(EXO,Checked);
        ChoosePlayerDialog.this.dismiss();
    }

    public interface IPlayerChooseCallback{
        void whichPlayer(String player,Boolean Ask_again);
    }
}