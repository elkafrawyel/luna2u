package com.apps.hmaserv.luna2u.ui.tv.tv_Models;

import android.support.v17.leanback.widget.HeaderItem;
public class IconHeaderItem extends HeaderItem {

    public static final String TAG = IconHeaderItem.class.getSimpleName();
    public static final int TYPE_FAVORITE = 0;
    public static final int TYPE_SETTINGS = 1;
    public static final int TYPE_CATEGORY = 2;

    private int type;
    private long categoryIndex;
    private String categoryId;

    IconHeaderItem(long id, String name, int type) {
        super(id, name);
        this.type = type;
    }

    public IconHeaderItem(long categoryIndex, String categoryId, String name, int type) {
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
