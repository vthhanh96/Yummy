package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

import java.util.Date;
import java.util.List;

public class Post extends Base {
    @Json(name = "_id") public Integer mId;
    @Json(name = "creator") public User mCreator;
    @Json(name = "categories") public List<Category> mCategories;
    @Json(name = "is_active") public Boolean mIsActive;
    @Json(name = "content") public String mContent;
    @Json(name = "place") public String mPlace;
    @Json(name = "latlngAddress") public List<Float> mLocation;
    @Json(name = "amount") public Integer mAmount;
    @Json(name = "comments") public List<Comment> mComments;
    @Json(name = "time") public Date mTime;
    @Json(name = "interested_people") public List<User> mInterestedPeople;
    @Json(name = "image") public String mImage;
    @Json(name = "link") public String mLink;
}
