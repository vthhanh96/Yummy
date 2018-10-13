package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

import java.util.Date;
import java.util.List;

public class Meeting extends Base {
    @Json(name = "creator") public User mCreator;
    @Json(name = "joined_people") public List<User> mJoinedPeople;
    @Json(name = "title") public String mTitle;
    @Json(name = "time") public Date mTime;
    @Json(name = "place") public String mPlace;
}
