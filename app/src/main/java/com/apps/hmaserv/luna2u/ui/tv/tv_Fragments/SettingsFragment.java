package com.apps.hmaserv.luna2u.ui.tv.tv_Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseOnItemViewClickedListener;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;

import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.ui.tv.tv_Presenters.SettingsIconPresenter;
import com.apps.hmaserv.luna2u.ui.tv.tv_Models.SettingCard;
import com.apps.hmaserv.luna2u.ui.tv.tv_activities.TV_LoginActivity;
import com.apps.hmaserv.luna2u.ui.tv.tv_dialogs.InformationDialog;
import com.apps.hmaserv.luna2u.ui.tv.tv_dialogs.PlayerDialog;

public class SettingsFragment extends RowsFragment {

    private final ArrayObjectAdapter mRowsAdapter;
    public SettingsFragment() {
        ListRowPresenter selector = new ListRowPresenter();
        selector.setNumRows(2);
        mRowsAdapter = new ArrayObjectAdapter(selector);
        setAdapter(mRowsAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loadData();
        setOnItemViewClickedListener(new BaseOnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Object row) {
                SettingCard settingCard = (SettingCard) item;
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

    private void loadData() {
        if (isAdded()) {
            mRowsAdapter.add(0, createCardRow());
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(
                    getMainFragmentAdapter());
        }
    }

    private ListRow createCardRow() {
        SettingsIconPresenter settingsIconPresenter = new SettingsIconPresenter(getActivity());
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(settingsIconPresenter);

        SettingCard PlayerSettingCard = new SettingCard(SettingCard.TYPE_PLAYER);
        SettingCard InfoSettingCard = new SettingCard(SettingCard.TYPE_INFO);
        SettingCard logOutSettingCard = new SettingCard(SettingCard.TYPE_LOG_OUT);

        adapter.add(InfoSettingCard);
        adapter.add(logOutSettingCard);
        adapter.add(PlayerSettingCard);


        HeaderItem headerItem = new HeaderItem("Settings");
        return new ListRow(headerItem, adapter);
    }

}
