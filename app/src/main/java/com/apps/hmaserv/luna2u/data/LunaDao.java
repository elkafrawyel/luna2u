package com.apps.hmaserv.luna2u.data;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import java.util.List;

@Dao
public interface LunaDao {
    @Query("SELECT * FROM channels")
    List<LiveChannelsModel> getAllChannels();

    @Insert
    void insert(LiveChannelsModel... Channels);

    @Update
    void update(LiveChannelsModel... Channels);

    @Delete
    void delete(LiveChannelsModel... Channels);

    @Query("SELECT * FROM channels WHERE id = :id ")
    LiveChannelsModel getChannelById(String id);

}
