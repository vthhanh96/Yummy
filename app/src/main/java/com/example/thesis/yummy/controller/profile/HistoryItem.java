package com.example.thesis.yummy.controller.profile;

import com.example.thesis.yummy.restful.model.User;

import java.util.Date;
import java.util.List;

public class HistoryItem {
    public Date mTime;
    public List<User> mUsers;
    public String mPlace;
    public Float mRating;

    public HistoryItem(Date time, List<User> users, String place, Float rating) {
        mTime = time;
        mUsers = users;
        mPlace = place;
        mRating = rating;
    }
}
