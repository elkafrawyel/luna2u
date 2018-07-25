package com.apps.hmaserv.luna2u.ui.tv.tv_Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.app.HeadersSupportFragment;
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
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.data.LunaDatabase;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import com.apps.hmaserv.luna2u.data.model.LiveGroupsModel;
import com.apps.hmaserv.luna2u.ui.tv.tv_Models.TV_IconHeaderItem;
import com.apps.hmaserv.luna2u.ui.tv.tv_Presenters.IconHeaderItemPresenter;
import com.apps.hmaserv.luna2u.ui.tv.tv_activities.TV_LoginActivity;
import com.apps.hmaserv.luna2u.ui.tv.tv_dialogs.SearchDialog;
import com.apps.hmaserv.luna2u.utils.Handler;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.apps.hmaserv.luna2u.utils.VolleySingleton.JsonVolleyCallbackError;
import com.google.gson.Gson;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TV_MainFragment extends BrowseSupportFragment {
    ArrayList<LiveGroupsModel> Groups = new ArrayList<>();
    ArrayList<LiveChannelsModel> Channels = new ArrayList<>();
    private boolean firstTime = true;
    public static String mCurrentGroupId;
    private final RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
    BackgroundManager mBackgroundManager;
    TextView mCurrentTime;
    ArrayObjectAdapter mRowsAdapter;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurrentTime = Objects.requireNonNull(getActivity()).findViewById(R.id.tv_CurrentTime);
        LoadDataFromServer();
        setUpEventListeners();
    }

    private void setUpUiElements() {
        setBadgeDrawable(Objects.requireNonNull(getActivity())
                .getResources().getDrawable(R.drawable.logo));
        //setTitle("Luna2u");
        setHeadersState(HEADERS_HIDDEN);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
        setSearchAffordanceColor(getActivity().getResources().getColor(R.color.text_color));
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        getMainFragmentRegistry().registerFragment(PageRow.class,
                new LiveFragmentFactory(mBackgroundManager, getContext(),
                        getHeadersSupportFragment()
                        , mCurrentTime, Channels));
        setHeaderPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object item) {
                if (item instanceof PageRow) {
                    return new IconHeaderItemPresenter();
                } else if (item instanceof DividerRow) {
                    return new DividerPresenter();
                } else
                    return new RowHeaderPresenter();
            }
        });
        prepareEntranceTransition();
    }

    public void myOnKeyDown(int key_code) {
        if (key_code == KeyEvent.KEYCODE_BACK) {
            setSelectedPosition(0);
            mCurrentGroupId = "0";
            setHeadersState(HEADERS_HIDDEN);
        }
    }

    private void setUpEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialog dialog = new SearchDialog
                        (Objects.requireNonNull(getActivity()), Channels);
                dialog.show();
            }
        });

    }

    private void LoadDataFromServer() {
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
                                    Objects.requireNonNull(getActivity()).finish();
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
        mRequestQueue.cancelAll(VolleySingleton.RequestKey);
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
                                    mThread thread=new mThread(code, new IChannelsLoaded() {
                                        @Override
                                        public void Loaded(ArrayList<LiveChannelsModel> Channels) {
                                            SetDataToAdapter();
                                        }
                                    });
                                    thread.start();
                                }
                            }
                        }, new JsonVolleyCallbackError() {
                            @Override
                            public void onError(VolleyError error) {
                                MDToast.makeText(Objects.requireNonNull(getContext()),
                                        "Can't Load Groups From Server",MDToast.TYPE_ERROR).show();
                                Handler.volleyErrorHandler(error, getActivity());
                            }
                        }
                )
        ).setTag(VolleySingleton.RequestKey);
    }

    public class mThread extends Thread{
        IChannelsLoaded iChannelsLoaded;
        String Code;
        mThread(String Code, IChannelsLoaded iChannelsLoaded) {
            super(Code);
            this.Code=Code;
            this.iChannelsLoaded = iChannelsLoaded;
        }

        @Override
            public void run() {
                super.run();
                LoadChannels(Code,iChannelsLoaded);
                Log.e("Url",Thread.currentThread().getId()+"");
            }
        };

    private void LoadChannels(String code, final IChannelsLoaded iChannelsLoaded) {
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mRequestQueue.add(
                VolleySingleton.getInstance().makeStringResponse(ServerURL.AllChannels
                                .concat(code),
                        new VolleySingleton.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) throws JSONException {
                                JSONObject Groups = new JSONObject(result);
                                JSONArray array = Groups.getJSONArray("channels");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.get(i).toString());
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String url = object.getString("url");
                                    String group = object.getString("group");

                                    LiveChannelsModel liveChannelsModels = new LiveChannelsModel(id, name, group, url, false);
                                    Channels.add(liveChannelsModels);

                                }
                                iChannelsLoaded.Loaded(Channels);
                            }
                        }
                        , new VolleySingleton.JsonVolleyCallbackError() {
                            @Override
                            public void onError(VolleyError error) {
                                MDToast.makeText(Objects.requireNonNull(getContext()),
                                        "Can't Load Channels From Server",MDToast.TYPE_ERROR).show();
                                Handler.volleyErrorHandler(error, getContext());
                            }
                        })
        );
    }

    public interface IChannelsLoaded{
        void Loaded(ArrayList<LiveChannelsModel> Channels);
    }


    private void SetDataToAdapter() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);

        TV_IconHeaderItem settingsHeaderItem = new TV_IconHeaderItem
                (0, "0",
                        "Settings", 0);
        PageRow settingsPageRow = new PageRow(settingsHeaderItem);
        mRowsAdapter.add(settingsPageRow);

        TV_IconHeaderItem favoritesHeaderItem = new
                TV_IconHeaderItem(1, "1",
                "Favorites", 1);
        PageRow favoritesPageRow = new PageRow(favoritesHeaderItem);
        mRowsAdapter.add(favoritesPageRow);
        mRowsAdapter.add(new DividerRow());
        mRowsAdapter.add(new SectionRow(new HeaderItem("Categories")));

        for (int i = 0; i < Groups.size(); i++) {
            LiveGroupsModel category = Groups.get(i);
            TV_IconHeaderItem headerItem = new TV_IconHeaderItem
                    (i + 4, category.getId(), category.getName(), 2);
            PageRow pageRow1 = new PageRow(headerItem);
            mRowsAdapter.add(pageRow1);
        }

        //header items index in the adapter
        // 0 setting
        //1 favorite
        //2 line
        //3 category title
        //4 first category header
        mCurrentGroupId = Groups.get(0).getId();

        if (getHeadersSupportFragment() != null) {
            //handling header items clicks


            getHeadersSupportFragment().setOnHeaderClickedListener(
                    new HeadersSupportFragment.OnHeaderClickedListener() {
                        @Override
                        public void onHeaderClicked(RowHeaderPresenter.ViewHolder viewHolder, Row row) {
                            if (firstTime) {
                                firstTime = false;
                            } else {
                                setSelectedPosition(getHeadersSupportFragment().getSelectedPosition());
                                mCurrentGroupId = ((TV_IconHeaderItem) row.getHeaderItem()).getCategoryId();
                            }
                        }
                    });

            getHeadersSupportFragment().setOnHeaderViewSelectedListener(new HeadersSupportFragment.OnHeaderViewSelectedListener() {
                @Override
                public void onHeaderSelected(RowHeaderPresenter.ViewHolder viewHolder, Row row) {
                    if (firstTime) {
                        firstTime = false;
                        startEntranceTransition();
                        setSelectedPosition(4);
                    } else {
                        int pos = getHeadersSupportFragment().getSelectedPosition();
                        setSelectedPosition(pos);
                        mCurrentGroupId = ((TV_IconHeaderItem) row.getHeaderItem()).getCategoryId();
                    }
                }
            });
        }


        setUpUiElements();

    }

    @Override
    public void onResume() {
        super.onResume();
        //when back from logout window
        if (mBackgroundManager != null)
            mBackgroundManager.setBitmap(BitmapFactory.decodeResource(Objects.requireNonNull(getContext()).getResources(), R.drawable.channels_tv_background));
    }

    private static class LiveFragmentFactory extends FragmentFactory {
        private final BackgroundManager mBackgroundManager;
        private Context mContext;
        HeadersSupportFragment headersSupportFragment;
        TextView mCurrentTime;
        ArrayList<LiveChannelsModel> Channels = new ArrayList<>();

        LiveFragmentFactory(BackgroundManager backgroundManager, Context mContext,
                            HeadersSupportFragment headersSupportFragment,
                            TextView mCurrentTime, ArrayList<LiveChannelsModel> Channels) {
            this.mBackgroundManager = backgroundManager;
            this.mContext = mContext;
            this.headersSupportFragment = headersSupportFragment;
            this.mCurrentTime = mCurrentTime;
            this.Channels = Channels;
        }

        @Override
        public Fragment createFragment(Object rowObj) {
            Row row = (Row) rowObj;
            TV_IconHeaderItem iconHeaderItem = (TV_IconHeaderItem) row.getHeaderItem();
            mBackgroundManager.setBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.channels_tv_background));

            if (iconHeaderItem.getId() == 0) {
                TV_SettingsFragment settingsFragment = new TV_SettingsFragment();
                settingsFragment.iFavoriteClicked = new TV_SettingsFragment.IFavoriteClicked() {
                    @Override
                    public void Handle() {
                        headersSupportFragment.setSelectedPosition(1);
                    }
                };
                settingsFragment.iSearchClicked = new TV_SettingsFragment.ISearchClicked() {
                    @Override
                    public void Handle() {
                        SearchDialog dialog = new SearchDialog(mContext, Channels);
                        dialog.show();
                    }
                };
                ShowTime();
                return settingsFragment;
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("category_id", iconHeaderItem.getCategoryId());
                bundle.putString("category_name", iconHeaderItem.getName());
                TV_ChannelsFragment channelsFragment = new TV_ChannelsFragment();
                channelsFragment.setArguments(bundle);
                HideTime();
                channelsFragment.Channels=Channels;
                return channelsFragment;
            }
        }

        void ShowTime() {
            mCurrentTime.setText(getDateFromTimeStamp(DateFormat_4));
            mCurrentTime.setVisibility(View.VISIBLE);
        }

        private void HideTime() {
            mCurrentTime.setVisibility(View.GONE);
        }

        //Wed, Jul 4, 01
        private static final String DateFormat_1 = "EEE, MMM d, yyyy";
        //12:08 PM
        public static final String DateFormat_2 = "h:mm a";
        //Wed, 4 Jul 2001 12:08:56
        public static final String DateFormat_3 = "EEE, d MMM yyyy HH:mm:ss";
        //Wed, 4 Jul 2001 12:08
        public static final String DateFormat_4 = "EEE, d MMM yyyy HH:mm";


        private String getDateFromTimeStamp(String format) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(System.currentTimeMillis());
            return DateFormat.format(format, cal).toString();
        }
    }

}
