package com.apps.hmaserv.luna2u.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;

@Database(entities = { LiveChannelsModel.class }, version = 1,exportSchema = false)
public abstract class LunaDatabase extends RoomDatabase {

    private static final String DB_NAME = "luna.db";
    private static volatile LunaDatabase instance;

    public static synchronized LunaDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static LunaDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                LunaDatabase.class,
                DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration().build();
    }

    public abstract LunaDao getUserDao();
}


