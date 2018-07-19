package com.apps.hmaserv.luna2u.ui.tv.tv_dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.data.LunaDatabase;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import com.apps.hmaserv.luna2u.ui.tv.tv_adapters.TV_QuickListAdapter;
import com.apps.hmaserv.luna2u.ui.tv.tv_adapters.TV_SearchAdapter;
import com.apps.hmaserv.luna2u.utils.Handler;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.google.android.exoplayer2.C;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuickListDialog extends Dialog implements TV_QuickListAdapter.IItemClickHandler {
    private TV_QuickListAdapter adapter;
    @BindView(R.id.quick_list_rv)
    RecyclerView rv;
    private ArrayList<LiveChannelsModel> Channels = new ArrayList<>();
    String Group_Id;
    Context mContext;
    ISelectedItem iSelectedItem;

    public QuickListDialog(@NonNull Context context, String Group_Id,
                           ISelectedItem iSelectedItem, ArrayList<LiveChannelsModel> Channels) {
        super(context);
        mContext = context;
        this.Group_Id = Group_Id;
        this.iSelectedItem = iSelectedItem;
        this.Channels = Channels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        QuickListDialog.this.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        setContentView(R.layout.dialog_quick_list_tv);
        ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        loadData();
    }

    private void loadData() {

        if (Group_Id.equals("1")) {
            Channels.clear();
            Channels.addAll(LunaDatabase.getInstance(mContext).getUserDao().getAllChannels());
            adapter = new TV_QuickListAdapter(Channels, QuickListDialog.this);
            rv.setAdapter(adapter);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(mContext));
        } else {
            adapter = new TV_QuickListAdapter(Channels, QuickListDialog.this);
            rv.setAdapter(adapter);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(mContext));

        }
    }

    @Override
    public void ItemClicked(int position) {
        LiveChannelsModel model = Channels.get(position);
        iSelectedItem.SelectedItem(model);
        QuickListDialog.this.dismiss();
    }

    public interface ISelectedItem {
        void SelectedItem(LiveChannelsModel channel);
    }
}
