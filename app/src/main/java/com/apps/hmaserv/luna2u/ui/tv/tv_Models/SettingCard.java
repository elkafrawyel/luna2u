package com.apps.hmaserv.luna2u.ui.tv.tv_Models;

import com.apps.hmaserv.luna2u.R;

public class SettingCard {

    public static final int TYPE_INFO = 0;
    public static final int TYPE_PLAYER = 1;
    public static final int TYPE_LOG_OUT= 2;

    private int type;

    public SettingCard(int type) {
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
            default:
                return "Setting";
        }
    }

}
