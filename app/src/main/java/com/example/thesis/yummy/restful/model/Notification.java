package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

import java.util.Date;

public class Notification extends Base {

    @Json(name = "_id") public Integer mId;
    @Json(name = "user_id") public Integer mUserId;
    @Json(name = "title") public String mTitle;
    @Json(name = "created_date") public Date mCreatedDate;
    @Json(name = "image") public String mImage;
    @Json(name = "content") public NotificationData notificationData;

}
