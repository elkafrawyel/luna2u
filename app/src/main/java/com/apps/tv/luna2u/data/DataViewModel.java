package com.apps.tv.luna2u.data;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;

import com.apps.tv.luna2u.data.model.LiveChannelsModel;

public class DataViewModel extends ViewModel {
    private LunaDatabase database;

    public DataViewModel(Context context) {
        database=LunaDatabase.getInstance(context);
    }

    //====================Add Channel===============================
    public void addChannel(LiveChannelsModel channel) {
        new AddChannelAsyncTask(database).execute(channel);
    }

    private static class AddChannelAsyncTask extends AsyncTask<LiveChannelsModel, Void,Void> {

        private LunaDatabase db;

        AddChannelAsyncTask(LunaDatabase lunaDatabase) {
            db = lunaDatabase;
        }

        @Override
        protected Void doInBackground(LiveChannelsModel... params) {
            db.getUserDao().insert(params);
            return null;
        }
    }

    //==================================================================


    //======================Remove Channel==============================
    public void removeChannel(LiveChannelsModel channel) {
        new RemoveChannelAsyncTask(database).execute(channel);
    }

    private static class RemoveChannelAsyncTask extends AsyncTask<LiveChannelsModel, Void,Void> {

        private LunaDatabase db;

        RemoveChannelAsyncTask(LunaDatabase lunaDatabase) {
            db = lunaDatabase;
        }

        @Override
        protected Void doInBackground(LiveChannelsModel... params) {
            db.getUserDao().delete(params);
            return null;
        }
    }
}
