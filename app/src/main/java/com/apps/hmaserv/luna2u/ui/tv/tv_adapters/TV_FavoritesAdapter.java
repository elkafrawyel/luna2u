package com.apps.hmaserv.luna2u.ui.tv.tv_adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.data.DataViewModel;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import com.apps.hmaserv.luna2u.ui.phone.phone_activities.Player;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.apps.hmaserv.luna2u.ui.tv.tv_Fragments.TV_MainFragment.mCurrentGroupId;

public class TV_FavoritesAdapter extends
        RecyclerView.Adapter<TV_FavoritesAdapter.ViewHolder> {

    ArrayList<LiveChannelsModel> Channels=new ArrayList<>();
    DataViewModel viewModel;

    private int currentPosition;
    public TV_FavoritesAdapter(ArrayList<LiveChannelsModel> channels) {
        this.Channels = channels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewModel=new DataViewModel(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tv_search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final LiveChannelsModel model = Channels.get(position);

        ObjectAnimator elevationUpAnimator = ObjectAnimator.ofFloat(holder.itemCard, "cardElevation", 20);
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(holder.itemCard, "scaleX", 1.2f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(holder.itemCard, "scaleY", 1.2f);
        elevationUpAnimator.setDuration(200);
        scaleUpX.setDuration(200);
        scaleUpY.setDuration(200);

        ObjectAnimator elevationDownAnimator = ObjectAnimator.ofFloat(holder.itemCard, "cardElevation", 8);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(holder.itemCard, "scaleX", 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(holder.itemCard, "scaleY", 1f);
        elevationDownAnimator.setDuration(200);
        scaleDownX.setDuration(200);
        scaleDownY.setDuration(200);

        final AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.play(elevationUpAnimator).with(scaleUpX).with(scaleUpY);

        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(elevationDownAnimator).with(scaleDownX).with(scaleDownY);

        int resourceId = holder.itemView.getContext().getResources()
                .getIdentifier("choose_screen_live_image",
                        "drawable", holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(R.drawable.logo)
                .apply(new RequestOptions()
                        .placeholder(resourceId))
                .into(holder.itemImage);

        if (model.isIs_favorite()) {
            holder.itemFavIcon.setVisibility(View.VISIBLE);
        } else {
            holder.itemFavIcon.setVisibility(View.GONE);
        }

        holder.itemText.setText(model.getName());

        holder.itemCard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    currentPosition = position;
                    scaleUp.start();
                } else {
                    scaleDown.start();
                }
            }
        });

    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public int getItemCount() {
        return Channels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.search_view_card)
        CardView itemCard;
        @BindView(R.id.search_view_img)
        ImageView itemImage;
        @BindView(R.id.search_view_text)
        TextView itemText;
        @BindView(R.id.search_view_icon)
        ImageView itemFavIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    LiveChannelsModel model=Channels.get(getAdapterPosition());
                    if (model.isIs_favorite()){
                        itemFavIcon.setVisibility(View.GONE);
                        model.setIs_favorite(false);
                        viewModel.removeChannel(model);
                        Toast.makeText(itemFavIcon.getContext(), "Removed From Your Favorite List", Toast.LENGTH_SHORT).show();
                    }else {
                        itemFavIcon.setVisibility(View.VISIBLE);
                        model.setIs_favorite(true);
                        viewModel.addChannel(model);
                        Toast.makeText(itemFavIcon.getContext(), "Added To Your Favorite List", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LiveChannelsModel model=Channels.get(getAdapterPosition());

                    Intent i=new Intent(itemCard.getContext(), Player.class);
                    i.putExtra("url",model.getUrl());
                    i.putExtra("group_id",mCurrentGroupId);
                    i.putExtra("group_name",model.getGroup());
                    i.putExtra("name",model.getName());
                    itemCard.getContext().startActivity(i);
                }
            });

        }


    }
}
