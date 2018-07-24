package com.apps.hmaserv.luna2u.ui.tv.tv_adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

public class TV_QuickListAdapter extends
        RecyclerView.Adapter<TV_QuickListAdapter.ViewHolder> {

    private ArrayList<LiveChannelsModel> Channels=new ArrayList<>();
    private DataViewModel viewModel;

    private int currentPosition;
    IItemClickHandler iItemClickHandler;
    Context context;
    public TV_QuickListAdapter(ArrayList<LiveChannelsModel> channels,IItemClickHandler iItemClickHandler) {
        this.iItemClickHandler=iItemClickHandler;
        this.Channels = channels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewModel=new DataViewModel(parent.getContext());
        context=parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tv_quick_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final LiveChannelsModel model = Channels.get(position);

        ObjectAnimator elevationUpAnimator = ObjectAnimator.ofFloat(holder.itemCard, "cardElevation", 20);
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(holder.itemCard, "scaleX", 1.2f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(holder.itemCard, "scaleY", 1.2f);
        elevationUpAnimator.setDuration(200);
        scaleUpX.setDuration(200);
        scaleUpY.setDuration(200);

        ObjectAnimator elevationDownAnimator = ObjectAnimator.ofFloat(holder.itemCard,
                "cardElevation", 8);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(holder.itemCard,
                "scaleX", 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(holder.itemCard,
                "scaleY", 1f);
        elevationDownAnimator.setDuration(200);
        scaleDownX.setDuration(200);
        scaleDownY.setDuration(200);

        final AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.play(elevationUpAnimator).with(scaleUpX).with(scaleUpY);

        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(elevationDownAnimator).with(scaleDownX).with(scaleDownY);

        if (model.isIs_favorite()) {
            holder.itemFavIcon.setVisibility(View.VISIBLE);
        } else {
            holder.itemFavIcon.setVisibility(View.GONE);
        }

        holder.itemText.setText(model.getName());
        holder.itemCard.getBackground().setAlpha(50);

        holder.itemText.setTextColor(context.getResources().getColor(R.color.text_color_UnSelected));

        holder.itemCard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    scaleUp.start();
                    holder.itemText.setTextColor(context.getResources().getColor(R.color.text_color));
                } else {
                    scaleDown.start();
                    holder.itemText.setTextColor(context.getResources().getColor(R.color.text_color_UnSelected));

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return Channels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.quick_item_card)
        CardView itemCard;
        @BindView(R.id.channel_view_text)
        TextView itemText;
        @BindView(R.id.channel_view_icon)
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
                    iItemClickHandler.ItemClicked(getAdapterPosition());
                }
            });

        }
    }


    public interface IItemClickHandler{
        void ItemClicked(int position);
    }
}
