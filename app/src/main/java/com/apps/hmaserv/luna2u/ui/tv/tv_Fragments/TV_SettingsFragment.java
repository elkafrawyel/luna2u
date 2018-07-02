package com.apps.hmaserv.luna2u.ui.tv.tv_Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.app.RowsSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseOnItemViewClickedListener;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;

import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.ui.tv.tv_Presenters.SettingsIconPresenter;
import com.apps.hmaserv.luna2u.ui.tv.tv_Models.TV_SettingCard;
import com.apps.hmaserv.luna2u.ui.tv.tv_activities.TV_LoginActivity;
import com.apps.hmaserv.luna2u.ui.tv.tv_dialogs.InformationDialog;
import com.apps.hmaserv.luna2u.ui.tv.tv_dialogs.PlayerDialog;

public class TV_SettingsFragment extends RowsSupportFragment {

    private ArrayObjectAdapter mRowsAdapter;
    public TV_SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCardRow();
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
                        InformationDialog dialog=new InformationDialog(getActivity());
                        dialog.show();
                        break;

                    case 1:
                        PlayerDialog playerDialog=new PlayerDialog(getActivity());
                        playerDialog.show();
                        break;

                    case 2:
                        NewApplication.getPreferencesHelper().clear();
                        TV_LoginActivity.start(getActivity());
                        break;
                }
            }
        });
    }

    private void createCardRow() {
        ListRowPresenter selector = new ListRowPresenter();
        selector.setNumRows(2);
        mRowsAdapter = new ArrayObjectAdapter(selector);

        SettingsIconPresenter settingsIconPresenter = new SettingsIconPresenter(getActivity());
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(settingsIconPresenter);

        TV_SettingCard PlayerSettingCard = new TV_SettingCard(TV_SettingCard.TYPE_PLAYER);
        TV_SettingCard InfoSettingCard = new TV_SettingCard(TV_SettingCard.TYPE_INFO);
        TV_SettingCard logOutSettingCard = new TV_SettingCard(TV_SettingCard.TYPE_LOG_OUT);

        adapter.add(InfoSettingCard);
        adapter.add(logOutSettingCard);
        adapter.add(PlayerSettingCard);


        HeaderItem headerItem = new HeaderItem("Settings");
        mRowsAdapter.add(0, new ListRow(headerItem, adapter));
        setAdapter(mRowsAdapter);

    }

}
