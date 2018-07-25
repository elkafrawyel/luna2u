package com.apps.hmaserv.luna2u.ui.tv.tv_Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.RowsSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseOnItemViewClickedListener;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.ui.tv.tv_Presenters.SettingsIconPresenter;
import com.apps.hmaserv.luna2u.ui.tv.tv_Models.TV_SettingCard;
import com.apps.hmaserv.luna2u.ui.tv.tv_activities.TV_LogOut_Activity;
import com.apps.hmaserv.luna2u.ui.tv.tv_dialogs.InformationDialog;
import com.apps.hmaserv.luna2u.ui.tv.tv_dialogs.PlayerDialog;
import com.apps.hmaserv.luna2u.ui.tv.tv_dialogs.SearchDialog;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.apps.hmaserv.luna2u.utils.VolleySingleton.RequestKey;

public class TV_SettingsFragment extends RowsSupportFragment {

    public IFavoriteClicked iFavoriteClicked;
    public ISearchClicked iSearchClicked;
    public TV_SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCardRow();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setOnItemViewClickedListener(new BaseOnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Object row) {
                TV_SettingCard settingCard = (TV_SettingCard) item;
                switch (settingCard.getType()) {
                    case 0:
                        InformationDialog dialog=new InformationDialog(Objects.requireNonNull(getActivity()));
                        dialog.show();
                        break;

                    case 1:
                        PlayerDialog playerDialog=new PlayerDialog(Objects.requireNonNull(getActivity()));
                        playerDialog.show();
                        break;

                    case 2:
                        //the view must be in activity to close it and cancel the dialog
                        startActivity(new Intent(getContext(), TV_LogOut_Activity.class));
                        break;

                    case 3:
                        Objects.requireNonNull(getActivity()).finishAffinity();
                        break;

                    case 4:
                        iSearchClicked.Handle();
                        break;

                    case 5:
                        iFavoriteClicked.Handle();
                        break;

                    case 6:
                        //Refresh








                        String code=NewApplication.getPreferencesHelper().getActivationCode();
                        Refresh(code);


                        break;
                }
            }
        });
    }
    private final RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
    private void Refresh(String code){
        mRequestQueue.add(
                VolleySingleton.getInstance().makeStringResponse(
                        ServerURL.Refresh_Url.concat(code),
                        new VolleySingleton.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) throws JSONException {
                                JSONObject Groups = new JSONObject(result);
                                String code = Groups.getString("code");
                                if(code.equals("0")){
                                    mRequestQueue.getCache().remove(VolleySingleton.RequestKey);
                                    final ProgressDialog progress = new ProgressDialog(getContext());
                                    progress.setTitle("Refreshing");
                                    progress.setMessage("Updating Channels and Groups,\nplease wait a while.");
                                    progress.setCancelable(false);
                                    progress.show();

                                    Runnable progressRunnable = new Runnable() {

                                        @Override
                                        public void run() {
                                            progress.cancel();
                                            MDToast.makeText(NewApplication.getAppContext(),
                                                    "success!", Toast.LENGTH_LONG,
                                                    MDToast.TYPE_SUCCESS).show();

                                        }
                                    };

                                    Handler pdCanceller = new Handler();
                                    pdCanceller.postDelayed(progressRunnable, 60*5000);

                                }else if (code.equals("2")){
                                    MDToast.makeText(NewApplication.getAppContext(),
                                            "Subscription not found!", Toast.LENGTH_LONG,
                                            MDToast.TYPE_ERROR).show();
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
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        }).setTag(RequestKey);
    }

    private void createCardRow() {
        ListRowPresenter selector = new ListRowPresenter();
        selector.setNumRows(2);
        ArrayObjectAdapter mRowsAdapter = new ArrayObjectAdapter(selector);

        SettingsIconPresenter settingsIconPresenter = new SettingsIconPresenter(getActivity());
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(settingsIconPresenter);

        TV_SettingCard PlayerSettingCard = new TV_SettingCard(TV_SettingCard.TYPE_PLAYER);
        TV_SettingCard InfoSettingCard = new TV_SettingCard(TV_SettingCard.TYPE_INFO);
        TV_SettingCard logOutSettingCard = new TV_SettingCard(TV_SettingCard.TYPE_LOG_OUT);
        TV_SettingCard ExitSettingCard = new TV_SettingCard(TV_SettingCard.TYPE_EXIT);
        TV_SettingCard SearchSettingCard = new TV_SettingCard(TV_SettingCard.TYPE_SEARCH);
        TV_SettingCard FavoritesSettingCard = new TV_SettingCard(TV_SettingCard.TYPE_FAVORITES);
        TV_SettingCard RefreshSettingCard = new TV_SettingCard(TV_SettingCard.TYPE_REFRESH);

        adapter.add(SearchSettingCard);
        adapter.add(InfoSettingCard);
        adapter.add(FavoritesSettingCard);
        adapter.add(logOutSettingCard);
        adapter.add(PlayerSettingCard);
        adapter.add(ExitSettingCard);
        adapter.add(RefreshSettingCard);

        HeaderItem headerItem = new HeaderItem("Settings");
        mRowsAdapter.add(0, new ListRow(headerItem, adapter));
        setAdapter(mRowsAdapter);

    }


    public interface IFavoriteClicked{
        void Handle();
    }


    public interface ISearchClicked{
        void Handle();
    }
}
