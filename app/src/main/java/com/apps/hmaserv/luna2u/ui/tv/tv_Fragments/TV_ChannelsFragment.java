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
import android.util.Log;
import android.util.SparseArray;
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

    private String categoryId,categoryName;
    private static int COLUMNS;
    private ArrayObjectAdapter mAdapter;
    public ArrayList<LiveChannelsModel> Channels = new ArrayList<>();
    public ArrayList<LiveChannelsModel> SmallChannels = new ArrayList<>();
    ArrayList<LiveChannelsModel> mFavList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        COLUMNS = 4;
        int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL;
        VerticalGridPresenter presenter = new VerticalGridPresenter(ZOOM_FACTOR);
        presenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(presenter);

        if (getArguments() != null) {
            categoryName=getArguments().getString("category_name");
            categoryId = getArguments().getString("category_id");
        }

        ChannelsCardPresenter mCardViewPresenter = new ChannelsCardPresenter();
        mAdapter = new ArrayObjectAdapter(mCardViewPresenter);
        setAdapter(mAdapter);

        if (categoryId != null) {
            if (Objects.equals(categoryId, "1")) {
                mAdapter.clear();
                mFavList = (ArrayList<LiveChannelsModel>) LunaDatabase.getInstance(getActivity()).getUserDao().getAllChannels();
                if (mFavList.size() > 0) {
                    mAdapter.addAll(0, mFavList);
                } else
                    MDToast.makeText(Objects.requireNonNull(getActivity()),
                            "No Favorite Channels", Toast.LENGTH_LONG, MDToast.TYPE_INFO).show();
            } else {
                for (int i = 0; i < Channels.size(); i++) {
                    if (Channels.get(i).getGroup().equals(categoryId)) {
                        LiveChannelsModel model=Channels.get(i);
                        model.setGroup(categoryName);
                        model.setName(model.getName());

                        SmallChannels.add(model);
                    }
                }
                mAdapter.addAll(0, SmallChannels);
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

                LiveChannelsModel model=((LiveChannelsModel) item);
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

}
