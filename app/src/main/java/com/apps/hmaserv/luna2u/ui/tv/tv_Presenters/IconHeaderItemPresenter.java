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
import com.apps.hmaserv.luna2u.ui.tv.tv_Models.TV_IconHeaderItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IconHeaderItemPresenter extends RowHeaderPresenter {


    public IconHeaderItemPresenter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        if (inflater != null) {
            view = inflater.inflate(R.layout.icon_header_item, null);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object o) {
        TV_IconHeaderItem iconHeaderItem = (TV_IconHeaderItem) ((PageRow) o).getHeaderItem();
        final ViewHolder vh = (ViewHolder) viewHolder;
        if (iconHeaderItem.getType() == 1) {
            vh.headerName.setTextColor(vh.headerName.getResources().getColor(R.color.text_color));
            vh.headerIcon.setImageResource(R.drawable.ic_favorite_red_800_24dp);
            vh.headerIcon.setVisibility(View.VISIBLE);
        } else if (iconHeaderItem.getType() == 0) {
            vh.headerName.setTextColor(vh.headerName.getResources().getColor(R.color.text_color));
            vh.headerIcon.setImageResource(R.drawable.ic_settings_white_24dp);
            vh.headerIcon.setVisibility(View.VISIBLE);
        } else {
            vh.headerName.setTextColor(vh.headerName.getResources().getColor(R.color.text_color));
            vh.headerIcon.setVisibility(View.GONE);
        }

        vh.headerName.setText(iconHeaderItem.getName());

        //حل الريموت من الفيو نفسه ف ال xml ال properties بتاع الفيو ال Root

        vh.headerName.setTextColor(vh.headerName.getResources().getColor(R.color.text_color_UnSelected));
        vh.view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    vh.headerName.setTextColor(vh.headerName.getResources().getColor(R.color.text_color));

                }else {
                    vh.headerName.setTextColor(vh.headerName.getResources().getColor(R.color.text_color_UnSelected));

                }
            }
        });
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
        }
    }
}
