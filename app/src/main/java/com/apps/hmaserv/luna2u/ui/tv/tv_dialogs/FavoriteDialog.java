package com.apps.hmaserv.luna2u.ui.tv.tv_dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import com.apps.hmaserv.luna2u.ui.tv.tv_adapters.TV_FavoritesAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteDialog extends Dialog {
    private Context mContext;
    private TV_FavoritesAdapter adapter;
    @BindView(R.id.search_result_rv)
    RecyclerView search_rv;
    ArrayList<LiveChannelsModel> Channels=new ArrayList<>();
    public FavoriteDialog(@NonNull Context context, ArrayList<LiveChannelsModel> Channels) {
        super(context);
        mContext = context;
        this.Channels=Channels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        FavoriteDialog.this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        setContentView(R.layout.dialog_favorite_tv);
        ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        search_rv.setHasFixedSize(true);
        search_rv.setLayoutManager(new GridLayoutManager(mContext,3));
        adapter=new TV_FavoritesAdapter(Channels);
        search_rv.setAdapter(adapter);
    }

}
