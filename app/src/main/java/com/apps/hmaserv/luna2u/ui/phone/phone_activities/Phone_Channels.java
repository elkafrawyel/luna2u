package com.apps.hmaserv.luna2u.ui.phone.phone_activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.data.LunaDatabase;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import com.apps.hmaserv.luna2u.ui.phone.phone_adapters.Phone_Channels_Adapter;
import com.apps.hmaserv.luna2u.ui.phone.phone_dialogs.Phone_SearchChannelsDialog;
import com.apps.hmaserv.luna2u.utils.Handler;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Phone_Channels extends AppCompatActivity
        implements VolleySingleton.VolleyCallback, VolleySingleton.JsonVolleyCallbackError {


    @BindView(R.id.recycler_Live)
    RecyclerView recycler_live;
    @BindView(R.id.progress_bar_live)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    Phone_Channels_Adapter adapter;
    ArrayList<LiveChannelsModel> Channels = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;

    private Boolean firstTime;
    private String Group_id;
    private String Channels_Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_channels);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        if (upArrow != null) {
            upArrow.setColorFilter(ContextCompat.getColor(this, R.color.text_color), PorterDuff.Mode.SRC_ATOP);
        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setTitle("Live Channels");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        initialize();
    }

    private void initialize() {
        layoutManager = new GridLayoutManager(this, 2);
        recycler_live.setLayoutManager(layoutManager);
        recycler_live.setHasFixedSize(true);
        Group_id = getIntent().getStringExtra("id");
        String code = NewApplication.getPreferencesHelper().getActivationCode();
        if (code != null) {
            Channels_Url = ServerURL.LiveChannels_Url.concat(code).concat("/").concat(Group_id);
            Load_LiveChannels(Channels_Url);
        }

        firstTime = true;
    }

    private void Load_LiveChannels(String Channels_Url) {
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mRequestQueue.add(
                VolleySingleton.getInstance().makeStringResponse(Channels_Url,
                        Phone_Channels.this
                        , Phone_Channels.this)
        ).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

    @Override
    public void onSuccess(String result) throws JSONException {
        JSONObject Groups = new JSONObject(result);
        JSONArray array = Groups.getJSONArray("channels");

        for (int i = 0; i < array.length(); i++) {

            LiveChannelsModel model = new Gson().fromJson(array.get(i)
                    .toString(), LiveChannelsModel.class);
            LiveChannelsModel temp = LunaDatabase.getInstance(Phone_Channels.this)
                    .getUserDao().getChannelById(model.getId());
            if (temp != null)
                model.setIs_favorite(true);

            Channels.add(model);
        }
        adapter = new Phone_Channels_Adapter(Channels, Phone_Channels.this);

        recycler_live.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(VolleyError error) {
        Handler.volleyErrorHandler(error,this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_live_channels, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            if (Channels.size() > 0) {
                firstTime = false;
                Phone_SearchChannelsDialog dialog = new Phone_SearchChannelsDialog(
                        this, Channels, new Phone_SearchChannelsDialog.ISearchDialog() {
                    @Override
                    public void DialogClosed() {
                        ApplyChanges();
                    }
                }
                );
                dialog.show();
            } else
                Toast.makeText(this, "No Channels Accessed", Toast.LENGTH_SHORT).show();
        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_favorite) {
            firstTime = false;
            Intent i = new Intent(this, Phone_Favorite.class);
            i.putExtra("id", Group_id);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplyChanges();
    }

    private void ApplyChanges() {
        if (!firstTime) {
            ArrayList<LiveChannelsModel> Favs = new ArrayList<>();
            Favs = (ArrayList<LiveChannelsModel>) LunaDatabase.getInstance(Phone_Channels.this)
                    .getUserDao().getAllChannels();

            for (LiveChannelsModel model : Channels) {
                if (Favs.contains(model)) {
                    model.setIs_favorite(true);
                } else
                    model.setIs_favorite(false);
            }
            adapter.notifyDataSetChanged();
        }
    }

}
