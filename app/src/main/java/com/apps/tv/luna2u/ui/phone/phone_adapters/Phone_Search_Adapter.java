package com.apps.tv.luna2u.ui.phone.phone_adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.tv.luna2u.R;
import com.apps.tv.luna2u.data.DataViewModel;
import com.apps.tv.luna2u.data.model.LiveChannelsModel;
import com.apps.tv.luna2u.ui.phone.phone_activities.Player;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Phone_Search_Adapter extends RecyclerView.Adapter<Phone_Search_Adapter
        .LiveChannels_ViewHolder> implements Filterable {

    private ArrayList<LiveChannelsModel> Data=new ArrayList<>();
    private ArrayList<LiveChannelsModel> FilteredData=new ArrayList<>();
    private Context mContext;
    DataViewModel viewModel;

    public Phone_Search_Adapter(ArrayList<LiveChannelsModel> data,
                                Context mContext) {
        Data = data;
        this.mContext = mContext;
        FilteredData=Data;
        viewModel=new DataViewModel(mContext);
        //viewModel = ViewModelProviders.of(getActivity).get(DataViewModel.class);
    }

    @NonNull
    @Override
    public LiveChannels_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LiveChannels_ViewHolder(LayoutInflater.from(mContext)
        .inflate(R.layout.phone_channel_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull LiveChannels_ViewHolder holder, int position) {
        final LiveChannelsModel model=FilteredData.get(position);
        holder.channelName.setText(model.getName());
        Glide.with(mContext).load(R.drawable.logo).into(holder.logo);


        if (model.isIs_favorite())
            holder.fav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_red_800_24dp));
        else
            holder.fav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_white_800_24dp));

    }

    @Override
    public int getItemCount() {
        return FilteredData.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    FilteredData = Data;
                } else {
                    ArrayList<LiveChannelsModel> filteredList = new ArrayList<>();
                    for (LiveChannelsModel row : Data) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    FilteredData = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = FilteredData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                FilteredData = (ArrayList<LiveChannelsModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class LiveChannels_ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.phone_channel_logo)
        ImageView logo;
        @BindView(R.id.phone_channel_fav_icon)
        ImageView fav;
        @BindView(R.id.phone_channel_name)
        TextView channelName;

        LiveChannels_ViewHolder (View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(mContext, Player.class);
                    i.putExtra("url",Data.get(getAdapterPosition()).getUrl());
                    //i.putExtra("group_id",);
                    i.putExtra("group_name",Data.get(getAdapterPosition()).getGroup());
                    i.putExtra("name",Data.get(getAdapterPosition()).getName());

                    mContext.startActivity(i);
                }
            });

            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LiveChannelsModel model=FilteredData.get(getAdapterPosition());
                    if (model.isIs_favorite()){
                        fav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_white_800_24dp));
                        model.setIs_favorite(false);
                        viewModel.removeChannel(model);
                    }else {
                        fav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_red_800_24dp));
                        model.setIs_favorite(true);
                        viewModel.addChannel(model);
                    }
                }
            });
        }
    }
}
