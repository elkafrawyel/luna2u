package com.apps.tv.luna2u.ui.tv.tv_Presenters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apps.tv.luna2u.R;
import com.apps.tv.luna2u.data.DataViewModel;
import com.apps.tv.luna2u.data.model.LiveChannelsModel;
import com.apps.tv.luna2u.ui.phone.phone_activities.Player;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static com.apps.tv.luna2u.ui.tv.tv_Fragments.TV_MainFragment.mCurrentGroupId;


public class ChannelsCardPresenter extends Presenter {
    private static int CARD_WIDTH = 300;
    private static int CARD_HEIGHT = 200;

    private Context mContext;
    private DataViewModel viewModel;


    static class ViewHolder extends Presenter.ViewHolder {
        private LiveChannelsModel mChannel;
        private ImageCardView mCardView;
        private Drawable mDefaultCardImage;

        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;
            mDefaultCardImage = view.getContext().getResources().getDrawable(R.drawable.logo);
        }

        void setChannel(LiveChannelsModel m) {
            mChannel = m;
        }

        public LiveChannelsModel getChannel() {
            return mChannel;
        }

        public ImageCardView getCardView() {
            return mCardView;
        }

        Drawable getDefaultCardImage() {
            return mDefaultCardImage;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();
        viewModel = new DataViewModel(mContext);

        ImageCardView cardView = new ImageCardView(mContext);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(final Presenter.ViewHolder viewHolder, Object item) {
        final LiveChannelsModel channel = (LiveChannelsModel) item;
        ((ViewHolder) viewHolder).setChannel(channel);

        ((ViewHolder) viewHolder).mCardView.setTitleText(channel.getName());
        ((ViewHolder) viewHolder).mCardView.setContentText(channel.getGroup());
        ((ViewHolder) viewHolder).mCardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
        ((ViewHolder) viewHolder).mCardView.setMainImage(((ViewHolder) viewHolder).getDefaultCardImage());
        if (channel.isIs_favorite()) {
            ((ViewHolder) viewHolder).mCardView.setBadgeImage(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_favorite_red_800_24dp));
        } else {
            ((ViewHolder) viewHolder).mCardView.setBadgeImage(null);
        }

        Glide.with(mContext)
                .load(mContext.getResources().getDrawable(R.drawable.logo))
                .apply(new RequestOptions().override(CARD_WIDTH, CARD_HEIGHT))
                .into(((ViewHolder) viewHolder).mCardView.getMainImageView());

        // onLongClick items
        ((ViewHolder) viewHolder).mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (channel.isIs_favorite()) {
                    ((ViewHolder) viewHolder).mCardView.setBadgeImage(null);
                    viewModel.removeChannel(channel);
                    channel.setIs_favorite(false);
                    Toast.makeText(mContext, "Removed From Your Favorite List",
                            Toast.LENGTH_SHORT).show();
                } else {
                    ((ViewHolder) viewHolder).mCardView.setBadgeImage
                            (ContextCompat.getDrawable(mContext,
                                    R.drawable.ic_favorite_red_800_24dp));
                    channel.setIs_favorite(true);
                    viewModel.addChannel(channel);
                    Toast.makeText(mContext, "Added To Your Favorite List",
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        ((ViewHolder) viewHolder).mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, Player.class);
                i.putExtra("url",channel.getUrl());
                i.putExtra("group_id",mCurrentGroupId);
                i.putExtra("group_name",channel.getGroup());
                i.putExtra("name",channel.getName());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
        // TO DO
    }
}

