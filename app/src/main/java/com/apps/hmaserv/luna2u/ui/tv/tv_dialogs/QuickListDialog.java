package com.apps.hmaserv.luna2u.ui.tv.tv_dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.data.LunaDatabase;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import com.apps.hmaserv.luna2u.data.model.LiveGroupsModel;
import com.apps.hmaserv.luna2u.ui.tv.tv_adapters.TV_QuickListAdapter;
import com.apps.hmaserv.luna2u.utils.Handler;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuickListDialog extends Dialog implements TV_QuickListAdapter.IItemClickHandler {
    private TV_QuickListAdapter adapter;
    @BindView(R.id.quick_list_rv)
    RecyclerView rv;
    @BindView(R.id.quickListLoading)
    ProgressBar quickListLoading;
    @BindView(R.id.groupName)
    TextView mGroupName;
    @BindView(R.id.left)
    ImageView left;
    @BindView(R.id.right)
    ImageView right;
    private ArrayList<LiveChannelsModel> Channels = new ArrayList<>();
    private ArrayList<LiveGroupsModel> Groups = new ArrayList<>();
    private String Group_Id;
    private Context mContext;
    private ISelectedItem iSelectedItem;
    private int groupIndex = -1;
    private String mCode;
    private LinearLayoutManager layoutManager;
    private LiveChannelsModel mCurrentChannel;
    public QuickListDialog(@NonNull Context context, String Group_Id,
                           ISelectedItem iSelectedItem, LiveChannelsModel mCurrentChannel) {
        super(context);
        mContext = context;
        this.Group_Id = Group_Id;
        this.iSelectedItem = iSelectedItem;
        mCode = NewApplication.getPreferencesHelper().getActivationCode();
        this.mCurrentChannel=mCurrentChannel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(QuickListDialog.this.getWindow()).setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        getWindow().setGravity(Gravity.START);
        this.setCancelable(true);
        setContentView(R.layout.dialog_quick_list_tv);
        ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        quickListLoading.setVisibility(View.VISIBLE);
        layoutManager=new LinearLayoutManager(mContext);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        SetData();
    }

    private void loadChannels(String code, String groupId) {
        quickListLoading.setVisibility(View.VISIBLE);
        Channels.clear();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        String Channels_Url = ServerURL.LiveChannels_Url.concat(code).concat("/")
                .concat(String.valueOf(groupId));
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mRequestQueue.add(
                VolleySingleton.getInstance().makeStringResponse(Channels_Url,
                        new VolleySingleton.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) throws JSONException {
                                JSONObject Groups = new JSONObject(result);
                                JSONArray array = Groups.getJSONArray("channels");
                                for (int i = 0; i < array.length(); i++) {

                                    LiveChannelsModel model = new Gson().fromJson(array.get(i)
                                            .toString(), LiveChannelsModel.class);
                                    LiveChannelsModel temp = LunaDatabase.getInstance(mContext)
                                            .getUserDao().getChannelById(model.getId());
                                    if (temp != null)
                                        model.setIs_favorite(true);

                                    Channels.add(model);
                                }
                                int pos=Channels.indexOf(mCurrentChannel);
                                adapter = new TV_QuickListAdapter(Channels,
                                        QuickListDialog.this,pos);
                                rv.setAdapter(adapter);
                                rv.setHasFixedSize(true);
                                rv.setLayoutManager(new LinearLayoutManager(mContext));
                                rv.scrollToPosition(pos);


                                //quickListLoading.setVisibility(View.GONE);
                            }
                        }, new VolleySingleton.JsonVolleyCallbackError() {
                            @Override
                            public void onError(VolleyError error) {

                            }
                        })
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

    private void SetData() {
        if (Group_Id.equals("1")) {
            Channels.clear();
            Channels.addAll(LunaDatabase.getInstance(mContext).getUserDao().getAllChannels());
            int pos=Channels.indexOf(mCurrentChannel);
            adapter = new TV_QuickListAdapter(Channels,
                    QuickListDialog.this,pos);

            rv.setAdapter(adapter);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(layoutManager);
            rv.scrollToPosition(pos);
        } else {
            if (Groups.size() == 0)
                loadGroups(mCode);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_ALT_RIGHT:
            if (!Group_Id.equals("1")) {
                if (Groups.size() > 0 && groupIndex < Groups.size() - 1) {
                    groupIndex++;
                    String groupName = Groups.get(groupIndex).getName();
                    String groupId = Groups.get(groupIndex).getId();
                    loadChannels(mCode, groupId);
                    mGroupName.setText(groupName);
                } else {
                    groupIndex = 0;
                    String groupName = Groups.get(groupIndex).getName();
                    String groupId = Groups.get(groupIndex).getId();

                    loadChannels(mCode, groupId);
                    mGroupName.setText(groupName);
                }
            }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_ALT_LEFT:
                if (!Group_Id.equals("1")) {
                    if (groupIndex > 0) {
                        groupIndex--;
                        String groupName = Groups.get(groupIndex).getName();
                        String groupId = Groups.get(groupIndex).getId();
                        loadChannels(mCode, groupId);
                        mGroupName.setText(groupName);
                    } else {
                        groupIndex = Groups.size() - 1;
                        String groupName = Groups.get(groupIndex).getName();
                        String groupId = Groups.get(groupIndex).getId();
                        loadChannels(mCode, groupId);
                        mGroupName.setText(groupName);

                    }
                }
                return true;
            default:
                Log.d("OnKey", String.valueOf(keyCode));
                return super.onKeyDown(keyCode, event);
        }
    }


    private final RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

    private void loadGroups(String code) {
        Groups = new ArrayList<>();
        mRequestQueue.add(
                VolleySingleton.getInstance().makeStringResponse(
                        ServerURL.LiveGroups_Url.concat(code),
                        new VolleySingleton.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) throws JSONException {
                                JSONObject object = new JSONObject(result);
                                JSONArray array = object.getJSONArray("groups");
                                for (int i = 0; i < array.length(); i++) {
                                    LiveGroupsModel model = new Gson().fromJson(array.get(i)
                                            .toString(), LiveGroupsModel.class);
                                    Groups.add(model);
                                    if (model.getId().equals(Group_Id)) {
                                        groupIndex = i;
                                        mGroupName.setText(model.getName());
                                    }
                                }
                                loadChannels(mCode, Group_Id);
                            }
                        }, new VolleySingleton.JsonVolleyCallbackError() {
                            @Override
                            public void onError(VolleyError error) {
                                Handler.volleyErrorHandler(error, mContext);
                                quickListLoading.setVisibility(View.GONE);
                            }
                        }
                )
        );
    }

    @Override
    public void ItemClicked(int position) {
        LiveChannelsModel model = Channels.get(position);
        QuickListDialog.this.dismiss();

        if (Group_Id.equals("1")) {
            iSelectedItem.SelectedItem(Channels,model,null);
        }else {
            for (LiveGroupsModel liveGroupsModel :Groups) {
                if (liveGroupsModel.getName().equals(model.getGroup())){
                    groupIndex=Groups.indexOf(liveGroupsModel);
                    mGroupName.setText(liveGroupsModel.getName());
                    iSelectedItem.SelectedItem(Channels,model,liveGroupsModel);
                }
            }
        }

    }

    public interface ISelectedItem {
        void SelectedItem(ArrayList<LiveChannelsModel>Channels, LiveChannelsModel channel,LiveGroupsModel groupsModel);
    }
}
