package com.apps.hmaserv.luna2u.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesHelper {

    private static SharedPreferences mSharedPreferences;
    private static final String PREF_FILE_NAME = "luna_tv_pref_file";
    private static final String PREF_KEY_ACTIVATION_CODE = "KEY_ACTIVATION_CODE";
    private static final String PREF_KEY_USERNAME = "KEY_USERNAME";
    private static final String PREF_KEY_PASSWORD = "KEY_PASSWORD";
    private static final String PREF_KEY_EXPIRE_DATE = "KEY_EXPIRE_DATE";
    private static final String PREF_KEY_IS_SIGNED = "KEY_IS_SIGNED";
    private static final String PREF_KEY_PLAYER = "KEY_PLAYER";
    private static final String PREF_KEY_ASK = "KEY_ASK";
    private static final String PREF_KEY_CHANNEL_IMAGE = "KEY_CHANNEL_IMAGE";

    public PreferencesHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public void setActivationCode(String activationCode) {
        mSharedPreferences.edit().putString(PREF_KEY_ACTIVATION_CODE, activationCode).apply();
    }

    public void setUsername(String username) {
        mSharedPreferences.edit().putString(PREF_KEY_USERNAME, username).apply();
    }

    //1 use normal image
    //0 use no image
    public void setChannelImage(String channelImage) {
        mSharedPreferences.edit().putString(PREF_KEY_CHANNEL_IMAGE, channelImage).apply();
    }

    public void setPlayer(String player) {
        mSharedPreferences.edit().putString(PREF_KEY_PLAYER, player).apply();
    }

    public void setASK(String ASK) {
        mSharedPreferences.edit().putString(PREF_KEY_ASK, ASK).apply();
    }

    public void setPassword(String password) {
        mSharedPreferences.edit().putString(PREF_KEY_PASSWORD, password).apply();
    }

    public void setExpireDate(String expireDate) {
        mSharedPreferences.edit().putString(PREF_KEY_EXPIRE_DATE, expireDate).apply();
    }

    public void setIsSigned(boolean isSigned) {
        mSharedPreferences.edit().putBoolean(PREF_KEY_IS_SIGNED, isSigned).apply();
    }

    public String getActivationCode() {
        return mSharedPreferences.getString(PREF_KEY_ACTIVATION_CODE, null);
    }

    public String getUsername() {
        return mSharedPreferences.getString(PREF_KEY_USERNAME, null);
    }

    public String getChannelImage() {
        return mSharedPreferences.getString(PREF_KEY_CHANNEL_IMAGE, null);
    }

    public String getPassword() {
        return mSharedPreferences.getString(PREF_KEY_PASSWORD, null);
    }

    public String getExpireDate() {
        return mSharedPreferences.getString(PREF_KEY_EXPIRE_DATE, null);
    }

    public String getASK() {
        return mSharedPreferences.getString(PREF_KEY_ASK, null);
    }


    public String getPlayer() {
        return mSharedPreferences.getString(PREF_KEY_PLAYER, null);
    }

    public boolean getIsSigned() {
        return mSharedPreferences.getBoolean(PREF_KEY_IS_SIGNED, false);
    }

}
