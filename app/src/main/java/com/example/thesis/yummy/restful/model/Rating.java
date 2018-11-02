package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

public class Rating extends Base {
    @Json(name = "_id") public Integer mId;
    @Json(name = "content") public String mContent;
    @Json(name = "creator") public User mCreator;
    @Json(name = "point") public Integer mPoint;
}
