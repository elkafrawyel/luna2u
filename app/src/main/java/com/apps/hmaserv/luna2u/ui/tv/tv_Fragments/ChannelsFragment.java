package com.apps.hmaserv.luna2u.ui.tv.tv_Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.data.LunaDatabase;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import com.apps.hmaserv.luna2u.ui.phone.phone_activities.LivePlayer_activity;
import com.apps.hmaserv.luna2u.ui.tv.tv_Presenters.ChannelsCardPresenter;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ChannelsFragment extends GridFragment {

    private String categoryId;
    private static int COLUMNS;
    private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL;
    private ArrayObjectAdapter mAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        COLUMNS = 5;
        categoryId = getArguments().getString("category_id");
        setupAdapter();
        if (categoryId!=null){
            if (Objects.equals(categoryId,"1")) {
                loadFavoriteData();
            } else {
                String url = NewApplication.getPreferencesHelper().getActivationCode();
                loadData(url);
            }
        }

        getMainFragmentAdapter().getFragmentHost()
                .notifyDataReady(getMainFragmentAdapter());
    }

    private void setupAdapter() {
        VerticalGridPresenter presenter = new VerticalGridPresenter(ZOOM_FACTOR);
        presenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(presenter);

        ChannelsCardPresenter mCardViewPresenter = new ChannelsCardPresenter();
        mAdapter = new ArrayObjectAdapter(mCardViewPresenter);
        setAdapter(mAdapter);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(
                    Presenter.ViewHolder itemViewHolder,
                    Object item,
                    RowPresenter.ViewHolder rowViewHolder,
                    Row row) {

                Intent i = new Intent(getActivity(), LivePlayer_activity.class);
                i.putExtra("url", ((LiveChannelsModel) item).getUrl());
                getActivity().startActivity(i);
            }
        });

    }

    ArrayList<LiveChannelsModel> Channels = new ArrayList<>();

    private void loadData(String code) {

        Channels.clear();
        String Channels_Url=ServerURL.LiveChannels_Url.concat(code).concat("/")
                .concat(String.valueOf(categoryId));
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
                                    LiveChannelsModel temp = LunaDatabase.getInstance(getActivity())
                                            .getUserDao().getChannelById(model.getId());
                                    if (temp != null)
                                        model.setIs_favorite(true);

                                    Channels.add(model);
                                }

                                mAdapter.addAll(0, Channels);
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

    ArrayList<LiveChannelsModel> mFavList = new ArrayList<>();
    private void loadFavoriteData() {
        mAdapter.clear();
        mFavList.addAll(LunaDatabase.getInstance(getActivity()).getUserDao().getAllChannels());
        if (mFavList.size() > 0) {
            mAdapter.addAll(0, mFavList);
        } else
            Toast.makeText(getActivity(), "No Favorite Channels", Toast.LENGTH_SHORT).show();
    }
}
