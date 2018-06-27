package com.apps.hmaserv.luna2u.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Objects;

@Entity(tableName = "channels")
public class LiveChannelsModel {

    @NonNull
    @PrimaryKey()
    private String id;
    private String name;
    private String group;
    private String url;
    private boolean is_favorite;

    public LiveChannelsModel(@NonNull String id, String name, String group,
                             String url, boolean is_favorite) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.url = url;
        this.is_favorite = is_favorite;
    }

    public boolean isIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveChannelsModel model = (LiveChannelsModel) o;
        return Objects.equals(id, model.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

}
