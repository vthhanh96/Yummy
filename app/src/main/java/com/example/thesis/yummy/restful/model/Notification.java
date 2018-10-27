package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

import java.util.Date;

public class Notification extends Base {

    @Json(name = "_id") public Integer mId;
    @Json(name = "user_id") public Integer mUserId;
    @Json(name = "content") public String mContent;
    @Json(name = "created_date") public Date mCreatedDate;
    @Json(name = "type") public Integer mType;
    @Json(name = "image") public String mImage;

    public Notification(String content) {
        mContent = content;
    }
}
