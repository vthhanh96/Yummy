package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

import java.util.Date;

public class Request extends Base {
    @Json(name = "_id") public Integer mId;
    @Json(name = "place") public String mPlace;
    @Json(name = "content") public String mContent;
    @Json(name = "userSearch") public User mUserReceiveId;
    @Json(name = "creator") public User mCreator;
    @Json(name = "time") public Date mTime;
    @Json(name = "location") public Location mLocation;
}
