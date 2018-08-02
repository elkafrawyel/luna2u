package com.apps.tv.luna2u.ui.phone.phone_adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.tv.luna2u.R;
import com.apps.tv.luna2u.data.model.LiveGroupsModel;
import com.apps.tv.luna2u.ui.phone.phone_activities.Phone_Channels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Phone_Groups_Adapter extends RecyclerView.Adapter<Phone_Groups_Adapter.LiveCat_viewHolder>{


    private ArrayList<LiveGroupsModel> Data=new ArrayList<>();
    private Context mContext;

    public Phone_Groups_Adapter(ArrayList<LiveGroupsModel> data, Context mContext) {
        Data = data;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public LiveCat_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LiveCat_viewHolder(LayoutInflater.from(mContext)
        .inflate(R.layout.phone_group_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull LiveCat_viewHolder holder, int position) {
        LiveGroupsModel model=Data.get(position);
        holder.LiveCatName.setText(model.getName().toUpperCase());

    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    class LiveCat_viewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.group_card)
        CardView group_card;
        @BindView(R.id.liveCat_name)
        TextView LiveCatName;
        LiveCat_viewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.group_card)
        void OpenLives(){
            Intent intent=new Intent(mContext, Phone_Channels.class);
            intent.putExtra("id",Data.get(getAdapterPosition()).getId());
            mContext.startActivity(intent);
        }

    }
}
