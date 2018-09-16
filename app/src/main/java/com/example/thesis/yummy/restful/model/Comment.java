package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

import java.util.Date;

public class Comment extends Base{
    @Json(name = "_id") public Integer mId;
    @Json(name = "creator") public User mCreator;
    @Json(name = "content") public String mContent;
    @Json(name = "created_date") public Date mCreatedDate;
    @Json(name = "modify_date") public Date mModifiedDate;
}
