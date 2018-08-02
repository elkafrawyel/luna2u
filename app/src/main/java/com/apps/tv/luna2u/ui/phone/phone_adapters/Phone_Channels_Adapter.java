package com.apps.tv.luna2u.ui.phone.phone_adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.tv.luna2u.NewApplication;
import com.apps.tv.luna2u.R;
import com.apps.tv.luna2u.data.DataViewModel;
import com.apps.tv.luna2u.data.model.LiveChannelsModel;
import com.apps.tv.luna2u.ui.phone.phone_activities.Player;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Phone_Channels_Adapter extends RecyclerView.Adapter<Phone_Channels_Adapter.Live_ViewHolder> {

    private ArrayList<LiveChannelsModel> Data=new ArrayList<>();
    private Context mContext;
    private DataViewModel viewModel;
    private String ChannelImageSelection;
    public Phone_Channels_Adapter(ArrayList<LiveChannelsModel>
                                          data, Context mContext) {
        Data = data;
        this.mContext = mContext;
        viewModel=new DataViewModel(mContext);
        ChannelImageSelection= NewApplication.getPreferencesHelper().getChannelImage();
    }

    @NonNull
    @Override
    public Live_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Live_ViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.phone_channel_item,parent,false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull Live_ViewHolder holder, int position) {
        LiveChannelsModel model=Data.get(position);
        holder.channelName.setText(model.getName());

        if (ChannelImageSelection==null||ChannelImageSelection.equals("1")){
            holder.logo.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(R.drawable.logo).into(holder.logo);
        }else
            holder.logo.setVisibility(View.GONE);

        if (model.isIs_favorite())
            holder.fav.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.ic_favorite_red_800_24dp));
        else
            holder.fav.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.ic_favorite_white_800_24dp));
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    class Live_ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.channel_item_root)
        CardView view_root;
        @BindView(R.id.phone_channel_logo)
        ImageView logo;
        @BindView(R.id.phone_channel_fav_icon)
        ImageView fav;
        @BindView(R.id.phone_channel_name)
        TextView channelName;

        Live_ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            view_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenChannel();
                }
            });

            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenChannel();
                }
            });

            channelName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenChannel();
                }
            });


            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LiveChannelsModel model=Data.get(getAdapterPosition());
                    if (model.isIs_favorite()){
                        fav.setImageDrawable(mContext.getResources().
                                getDrawable(R.drawable.ic_favorite_white_800_24dp));
                        viewModel.removeChannel(model);
                        model.setIs_favorite(false);
                    }else {
                        fav.setImageDrawable(mContext.getResources().
                                getDrawable(R.drawable.ic_favorite_red_800_24dp));
                        model.setIs_favorite(true);
                        viewModel.addChannel(model);
                    }
                }
            });

        }

        private void OpenChannel(){
            Intent i=new Intent(mContext, Player.class);
            i.putExtra("url",Data.get(getAdapterPosition()).getUrl());
            //i.putExtra("group_id",);
            i.putExtra("group_name",Data.get(getAdapterPosition()).getGroup());
            i.putExtra("name",Data.get(getAdapterPosition()).getName());
            mContext.startActivity(i);
        }
    }

}
