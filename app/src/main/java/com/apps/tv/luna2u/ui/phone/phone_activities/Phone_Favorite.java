package com.apps.tv.luna2u.ui.phone.phone_activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.apps.tv.luna2u.R;
import com.apps.tv.luna2u.data.LunaDatabase;
import com.apps.tv.luna2u.data.model.LiveChannelsModel;
import com.apps.tv.luna2u.ui.phone.phone_adapters.Phone_Channels_Adapter;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Phone_Favorite extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fav_recyclerView)
    RecyclerView fav_rv;
    Phone_Channels_Adapter adapter;
    ArrayList<LiveChannelsModel> FavList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_favorite);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        final Drawable upArrow =  ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(this, R.color.text_color), PorterDuff.Mode.SRC_ATOP);
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setTitle("Favorite Channels");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        fav_rv.setLayoutManager(gridLayoutManager);
        fav_rv.setHasFixedSize(true);
        FavList.addAll(LunaDatabase.getInstance(this).getUserDao().getAllChannels());
        if (FavList.size()>0) {
            adapter = new Phone_Channels_Adapter(FavList, this);
            fav_rv.setAdapter(adapter);
        }else
            MDToast.makeText(this, "No Favorite Channels",
                    MDToast.LENGTH_SHORT,MDToast.TYPE_INFO).show();
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
