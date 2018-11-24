package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

public class MeetingPoint extends Base {
    @Json(name = "_id") public Integer mId;
    @Json(name = "user") public User mUser;
    @Json(name = "count_people") public Integer mCountPeople;
    @Json(name = "point_sum") public Integer mPointSum;

    public MeetingPoint(int id) {
        mId = id;
    }
}
