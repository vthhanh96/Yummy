package com.example.thesis.yummy.controller.base;

public class ItemMenu {

    public int mKey;
    public String mText;
    public int mIcon;
    public int mIconSelected;
    public boolean mIsSelected;

    public ItemMenu(int key, String text, int icon, int iconSelected, boolean isSelected) {
        mKey = key;
        mText = text;
        mIcon = icon;
        mIconSelected = iconSelected;
        mIsSelected = isSelected;
    }
}
