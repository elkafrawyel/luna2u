package com.apps.hmaserv.luna2u.ui.tv.tv_Presenters;

import android.content.Context;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowHeaderPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.ui.tv.tv_Models.IconHeaderItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IconHeaderItemPresenter extends RowHeaderPresenter {

    private float mUnselectedAlpha;

    public IconHeaderItemPresenter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        mUnselectedAlpha = viewGroup.getResources().getFraction(R.fraction.lb_browse_header_unselect_alpha, 1, 1);
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.icon_header_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object o) {
        IconHeaderItem iconHeaderItem = (IconHeaderItem) ((PageRow) o).getHeaderItem();
        final ViewHolder vh = (ViewHolder) viewHolder;
        if (iconHeaderItem.getType() == IconHeaderItem.TYPE_FAVORITE) {
            vh.headerName.setTextColor(vh.headerName.getResources().getColor(R.color.text_color));
            vh.headerIcon.setImageResource(R.drawable.ic_favorite_red_800_24dp);
            vh.headerIcon.setVisibility(View.VISIBLE);
        } else if (iconHeaderItem.getType() == IconHeaderItem.TYPE_SETTINGS) {
            vh.headerName.setTextColor(vh.headerName.getResources().getColor(R.color.text_color));
            vh.headerIcon.setImageResource(R.drawable.ic_settings_white_24dp);
            vh.headerIcon.setVisibility(View.VISIBLE);
        } else {
            vh.headerName.setTextColor(vh.headerName.getResources().getColor(R.color.text_color));
            vh.headerIcon.setVisibility(View.GONE);
        }

        vh.headerName.setText(iconHeaderItem.getName());

    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        // no op
    }



    public class ViewHolder extends RowHeaderPresenter.ViewHolder {
        @BindView(R.id.header_icon)
        ImageView headerIcon;
        @BindView(R.id.header_label)
        TextView headerName;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            if (view.hasFocus()){
                headerName.setTextColor(headerName.getResources().getColor(R.color.text_color));
            }else {
                headerName.setTextColor(headerName.getResources().getColor(R.color.colorWhite));
            }
        }
    }
}