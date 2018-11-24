package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

import java.util.Date;
import java.util.List;

public class Meeting extends Base {
    @Json(name = "_id") public Integer mId;
    @Json(name = "creator") public User mCreator;
    @Json(name = "post_id") public Integer mPostId;
    @Json(name = "comments") public List<Comment> mComments;
    @Json(name = "is_finished") public Boolean mIsFinished;
    @Json(name = "joined_people") public List<User> mJoinedPeople;
    @Json(name = "title") public String mTitle;
    @Json(name = "time") public Date mTime;
    @Json(name = "place") public String mPlace;
    @Json(name = "created_date") public Date mCreatedDate;
    @Json(name = "list_point_average") public List<MeetingPoint> mMeetingPoints;

    public Meeting(int id) {
        mId = id;
    }
}
