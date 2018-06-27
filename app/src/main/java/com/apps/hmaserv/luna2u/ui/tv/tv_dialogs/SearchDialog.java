package com.apps.hmaserv.luna2u.ui.tv.tv_dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import com.apps.hmaserv.luna2u.ui.tv.tv_adapters.SearchAdapter;
import com.apps.hmaserv.luna2u.utils.Handler;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchDialog extends Dialog{
    private Context mContext;
    private SearchAdapter adapter;
    @BindView(R.id.search_et)
    EditText search;
    @BindView(R.id.search_result_rv)
    RecyclerView search_rv;
    private ArrayList<LiveChannelsModel>Channels=new ArrayList<>();
    public SearchDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SearchDialog.this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        setContentView(R.layout.tv_search_view);
        ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        String Code= NewApplication.getPreferencesHelper().getActivationCode();
        LoadChannels(Code);
        ApplySearch();
    }
    private void ApplySearch(){
        search_rv.setHasFixedSize(true);
        search_rv.setLayoutManager(new GridLayoutManager(mContext,4));
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s))
                    adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s))
                    adapter.getFilter().filter(s.toString());
            }
        });
    }


    private void LoadChannels(String code_from_user) {
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mRequestQueue.add(
                VolleySingleton.getInstance().makeStringResponse(ServerURL.AllChannels
                                .concat(code_from_user),
                        new VolleySingleton.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) throws JSONException {
                                //Channels
                                //get Channels List By group id here
                                JSONObject Groups = new JSONObject(result);
                                JSONArray array = Groups.getJSONArray("channels");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String url = object.getString("url");
                                    String group = object.getString("group");
                                    Channels.add(new LiveChannelsModel(id, name, group, url, false));
                                }
                                adapter=new SearchAdapter(Channels);
                                search_rv.setAdapter(adapter);
                            }
                        }
                        , new VolleySingleton.JsonVolleyCallbackError() {
                            @Override
                            public void onError(VolleyError error) {
                                Handler.volleyErrorHandler(error, mContext);
                            }
                        })
        );
    }

}
