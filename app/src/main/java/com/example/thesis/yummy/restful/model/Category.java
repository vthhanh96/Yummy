package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

public class Category extends Base {
    @Json(name = "id") public Integer mId;
    @Json(name = "name") public String mName;
    public boolean mIsSelected;

    public Category(Integer id) {
        mId = id;
    }
}
