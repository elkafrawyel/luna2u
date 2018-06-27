package com.apps.hmaserv.luna2u.ui.tv.tv_Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.app.HeadersFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.DividerPresenter;
import android.support.v17.leanback.widget.DividerRow;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowHeaderPresenter;
import android.support.v17.leanback.widget.SectionRow;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.data.model.LiveGroupsModel;
import com.apps.hmaserv.luna2u.ui.tv.tv_Models.IconHeaderItem;
import com.apps.hmaserv.luna2u.ui.tv.tv_Presenters.IconHeaderItemPresenter;
import com.apps.hmaserv.luna2u.ui.tv.tv_activities.TV_LoginActivity;
import com.apps.hmaserv.luna2u.ui.tv.tv_dialogs.SearchDialog;
import com.apps.hmaserv.luna2u.utils.Handler;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.apps.hmaserv.luna2u.utils.VolleySingleton.JsonVolleyCallbackError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TV_LiveFragment extends BrowseFragment {
    ArrayList<LiveGroupsModel> Groups = new ArrayList<>();
    private BackgroundManager mBackgroundManager;
    ProgressBar progressBar;
    private boolean firstTime=true;
    private final RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressBar = getActivity().findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        setUpUiElements();
        LoadDataFromServer();
        setUpEventListeners();
    }

    private void setUpUiElements() {
        setBadgeDrawable(getActivity().getResources().getDrawable(
                R.drawable.logo
        ));
        setTitle("Luna2u");
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
        setSearchAffordanceColor(getActivity().getResources().getColor(R.color.text_color));
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mBackgroundManager.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        getMainFragmentRegistry().registerFragment(PageRow.class,
                new LiveFragmentFactory(mBackgroundManager));
        setHeaderPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object item) {
                if (item instanceof PageRow) {
                    return new IconHeaderItemPresenter();
                } else if (item instanceof DividerRow) {
                    return new DividerPresenter();
                }
                return new RowHeaderPresenter();
            }
        });
    }

    private void setUpEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialog dialog = new SearchDialog(getActivity());
                dialog.show();
            }
        });

    }

    private void LoadDataFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        final String code = NewApplication.getPreferencesHelper().getActivationCode();
        if (code != null) {
            CheckCodeValidation(code);
        }
    }

    private void CheckCodeValidation(final String Code_fromUser) {
        mRequestQueue.add(
                VolleySingleton.getInstance().makeStringResponse(ServerURL.Code_Url.concat(Code_fromUser),
                        new VolleySingleton.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) throws JSONException {
                                JSONObject Groups = new JSONObject(result);
                                String code = Groups.getString("code");
                                String status = Groups.getString("status");
                                String message = Groups.getString("message");

                                if (code.equals("0") && status.equals("success") && message.equals("Valid")) {
                                    LoadGroups(Code_fromUser);
                                } else if (code.equals("2") && status.equals("error") && message.equals("Subscription expired")) {
                                    Toast.makeText(getActivity(),
                                            "Your Activation Code is Expired!!",
                                            Toast.LENGTH_SHORT).show();
                                    NewApplication.getPreferencesHelper().clear();
                                    getActivity().finish();
                                    getActivity().startActivity(new Intent(getActivity(), TV_LoginActivity.class));
                                }
                            }
                        }
                        , new VolleySingleton.JsonVolleyCallbackError() {
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

    private void LoadGroups(final String code) {
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
                                }
                                if (Groups.size() > 0) {
                                    loadData();
                                }
                            }
                        }, new JsonVolleyCallbackError() {
                            @Override
                            public void onError(VolleyError error) {
                                Handler.volleyErrorHandler(error, getActivity());
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                )
        );
    }

    ArrayObjectAdapter mRowsAdapter;

    private void loadData() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);
        loadGroupData();
    }

    private void loadGroupData() {
        progressBar.setVisibility(View.GONE);
        newCreateRows(Groups);
    }

    private void newCreateRows(List<LiveGroupsModel> groups) {
        mRowsAdapter.clear();

        IconHeaderItem settingsHeaderItem = new IconHeaderItem
                (0,"0", "Settings", IconHeaderItem.TYPE_SETTINGS);
        PageRow settingsPageRow = new PageRow(settingsHeaderItem);
        mRowsAdapter.add(settingsPageRow);

        IconHeaderItem favoritesHeaderItem = new
                IconHeaderItem(1,"1","Favorites", IconHeaderItem.TYPE_FAVORITE);
        PageRow favoritesPageRow = new PageRow(favoritesHeaderItem);
        mRowsAdapter.add(favoritesPageRow);
        mRowsAdapter.add(new DividerRow());
        mRowsAdapter.add(new SectionRow(new HeaderItem("Categories")));

        for (int i = 0; i < groups.size(); i++) {
            LiveGroupsModel category = groups.get(i);
            IconHeaderItem headerItem = new IconHeaderItem
                    (i + 4,category.getId(), category.getName(), IconHeaderItem.TYPE_CATEGORY);
            PageRow pageRow1 = new PageRow(headerItem);
            mRowsAdapter.add(pageRow1);
        }

        setSelectedPosition(4);

        //header items index in the adapter
        // 0 setting
        //1 favorite
        //2 line
        //3 category title
        //4 first category header

        getHeadersFragment().setOnHeaderClickedListener(new HeadersFragment.OnHeaderClickedListener() {
            @Override
            public void onHeaderClicked(RowHeaderPresenter.ViewHolder viewHolder, Row row) {
                if (firstTime){
                    firstTime=false;
                }else {
                    setSelectedPosition((int) row.getHeaderItem().getId());
                }

            }
        });
    }

    private static class LiveFragmentFactory extends FragmentFactory {
        private final BackgroundManager mBackgroundManager;

        LiveFragmentFactory(BackgroundManager backgroundManager) {
            this.mBackgroundManager = backgroundManager;
        }

        @Override
        public Fragment createFragment(Object rowObj) {
            Row row = (Row) rowObj;
            IconHeaderItem iconHeaderItem = (IconHeaderItem) row.getHeaderItem();
            mBackgroundManager.setDrawable(null);

            if (iconHeaderItem.getId() == 0) {
                return new SettingsFragment();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("category_id", iconHeaderItem.getCategoryId());
                bundle.putLong("header_id", iconHeaderItem.getCategoryIndex());
                ChannelsFragment channelsFragment = new ChannelsFragment();
                channelsFragment.setArguments(bundle);
                return channelsFragment;
            }

        }
    }

}
