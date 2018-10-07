package com.example.thesis.yummy.controller.profile;

public class GenderItem {

    public int mKey;
    public String mValue;

    @Override
    public String toString() {
        return mValue;
    }

    public GenderItem(int mKey, String mValue) {
        this.mKey = mKey;
        this.mValue = mValue;
    }
}
