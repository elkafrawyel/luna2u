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
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.data.LunaDatabase;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import com.apps.hmaserv.luna2u.ui.phone.phone_activities.Player;
import com.apps.hmaserv.luna2u.ui.tv.tv_Presenters.ChannelsCardPresenter;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.google.gson.Gson;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.apps.hmaserv.luna2u.ui.tv.tv_Fragments.TV_MainFragment.mCurrentGroupId;
import static com.apps.hmaserv.luna2u.utils.VolleySingleton.RequestKey;

public class TV_ChannelsFragment extends TV_GridFragment {

    private String categoryId, categoryName;
    public ArrayList<LiveChannelsModel> Channels = new ArrayList<>();
    ArrayList<LiveChannelsModel> mFavList = new ArrayList<>();
    ArrayObjectAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int COLUMNS = 6;
        int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL;
        VerticalGridPresenter presenter = new VerticalGridPresenter(ZOOM_FACTOR);
        presenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(presenter);

        if (getArguments() != null) {
            categoryName = getArguments().getString("category_name");
            categoryId = getArguments().getString("category_id");
        }


        if (categoryId != null) {
            if (mAdapter != null)
                mAdapter.clear();
            if (Objects.equals(categoryId, "1")) {
                mFavList = (ArrayList<LiveChannelsModel>) LunaDatabase.getInstance(getActivity()).getUserDao().getAllChannels();
                if (mFavList.size() > 0) {
                    ChannelsCardPresenter mCardViewPresenter = new ChannelsCardPresenter();
                    mAdapter = new ArrayObjectAdapter(mCardViewPresenter);
                    setAdapter(mAdapter);
                    for (LiveChannelsModel model:mFavList){
                        model.setGroup("Favorites");
                    }
                    mAdapter.addAll(0, mFavList);
                } else
                    MDToast.makeText(Objects.requireNonNull(getActivity()),
                            "No Favorite Channels", Toast.LENGTH_LONG, MDToast.TYPE_INFO).show();
            } else {
                GetSmallList();
            }
        }


        getMainFragmentAdapter().getFragmentHost()
                .notifyDataReady(getMainFragmentAdapter());


        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(
                    Presenter.ViewHolder itemViewHolder,
                    Object item,
                    RowPresenter.ViewHolder rowViewHolder,
                    Row row) {

                LiveChannelsModel model = ((LiveChannelsModel) item);
                Intent i = new Intent(getActivity(), Player.class);
                i.putExtra("id", model.getId());
                i.putExtra("url", model.getUrl());
                i.putExtra("group_id", mCurrentGroupId);
                i.putExtra("group_name", model.getGroup());
                i.putExtra("name", model.getName());
                i.putExtra("fav", model.isIs_favorite());

                Objects.requireNonNull(getActivity()).startActivity(i);
            }
        });
    }

    ArrayList<LiveChannelsModel> SmallChannels = new ArrayList<>();

    private void GetSmallList() {
        SmallChannels.clear();
        String Channels_Url = ServerURL.LiveChannels_Url.concat(
                NewApplication.getPreferencesHelper().getActivationCode())
                .concat("/").concat(categoryId);
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
                                    LiveChannelsModel temp = LunaDatabase.getInstance(getContext())
                                            .getUserDao().getChannelById(model.getId());
                                    if (temp != null)
                                        model.setIs_favorite(true);

                                    SmallChannels.add(model);
                                }
                                ChannelsCardPresenter mCardViewPresenter = new ChannelsCardPresenter();
                                mAdapter = new ArrayObjectAdapter(mCardViewPresenter);
                                setAdapter(mAdapter);
                                mAdapter.addAll(0, SmallChannels);

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

}
