package com.apps.hmaserv.luna2u.ui.tv.tv_Presenters;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.ui.tv.tv_Models.SettingCard;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsIconPresenter extends Presenter {
    private Context mContext;

    public SettingsIconPresenter(Context context) {
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.tv_setting_item, parent, false);

        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setImageBackground(view.findViewById(R.id.setting_container), R.color.text_color);
                } else {
                    setImageBackground(view.findViewById(R.id.setting_container), R.color.CardView_color);
                }
            }
        });
        setImageBackground(view.findViewById(R.id.setting_container), R.color.CardView_color);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ViewHolder vh = (ViewHolder) viewHolder;
        SettingCard settingCard = (SettingCard) item;

        vh.settingText.setText(settingCard.getSettingLabel());
        vh.settingIcon.setImageResource(settingCard.getIconResourceId());
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }

    private void setImageBackground(View view, int colorId) {
        view.setBackgroundColor(mContext.getResources().getColor(colorId));
    }

    public class ViewHolder extends Presenter.ViewHolder {

        @BindView(R.id.setting_icon)
        ImageView settingIcon;
        @BindView(R.id.setting_container)
        RelativeLayout settingTextContainer;
        @BindView(R.id.setting_view_text)
        TextView settingText;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}