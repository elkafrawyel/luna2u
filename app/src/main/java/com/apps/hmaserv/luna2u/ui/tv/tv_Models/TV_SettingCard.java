package com.apps.hmaserv.luna2u.ui.tv.tv_Models;

import com.apps.hmaserv.luna2u.R;

public class TV_SettingCard {

    public static final int TYPE_INFO = 0;
    public static final int TYPE_PLAYER = 1;
    public static final int TYPE_LOG_OUT= 2;
    public static final int TYPE_EXIT= 3;
    public static final int TYPE_SEARCH= 4;
    public static final int TYPE_FAVORITES= 5;
    public static final int TYPE_REFRESH= 6;

    private int type;

    public TV_SettingCard(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getIconResourceId() {
        switch (type) {
            case TYPE_INFO:
                return R.drawable.ic_info_outline_white_48dp;
            case TYPE_PLAYER:
                return R.drawable.ic_replay_white_48dp;
            case TYPE_LOG_OUT:
                return R.drawable.ic_exit_to_app_white_48dp;
            case TYPE_EXIT:
                return R.drawable.ic_exit_white_48dp;
            case TYPE_SEARCH:
                return R.drawable.ic_search_white_48dp;
            case TYPE_FAVORITES:
                return R.drawable.ic_favorite_white_48dp;
            case TYPE_REFRESH:
                return R.drawable.ic_replay_white_48dp;
            default:
                return R.drawable.ic_search_white_36dp;
        }
    }

    public String getSettingLabel() {
        switch (type) {
            case TYPE_INFO:
                return "Information";
            case TYPE_PLAYER:
                return "Player";
            case TYPE_LOG_OUT:
                return "Log out";
            case TYPE_EXIT:
                return "Exit";
            case TYPE_REFRESH:
                return "Refresh";
            case TYPE_SEARCH:
                return "Search";
            case TYPE_FAVORITES:
                return "Favorites";
            default:
                return "Setting";
        }
    }

}
