package com.apps.hmaserv.luna2u.ui.tv.tv_Models;

import android.support.v17.leanback.widget.HeaderItem;
public class TV_IconHeaderItem extends HeaderItem {

    private int type;
    private long categoryIndex;
    private String categoryId;

    private TV_IconHeaderItem(long id, String name, int type) {
        super(id, name);
        this.type = type;
    }

    public TV_IconHeaderItem(long categoryIndex, String categoryId, String name, int type) {
        this(categoryIndex, name, type);
        this.categoryIndex = categoryIndex;
        this.categoryId=categoryId;
    }

    public int getType() {
        return type;
    }

    public long getCategoryIndex() {
        return categoryIndex;
    }

    public String getCategoryId() {
        return categoryId;
    }
}
