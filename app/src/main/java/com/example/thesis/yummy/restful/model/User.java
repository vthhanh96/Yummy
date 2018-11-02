package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

import java.util.Date;

public class User extends Base{
    @Json(name = "_id") public Integer mId;
    @Json(name = "phone") public String mPhone;
    @Json(name = "email") public String mEmail;
    @Json(name = "fullName") public String mFullName;
    @Json(name = "password") public String mPassword;
    @Json(name = "avatar") public String mAvatar;
    @Json(name = "birthday") public Date mBirthDay;
    @Json(name = "gender") public Integer mGender;
    @Json(name = "address") public String mAddress;
    @Json(name = "latLngAdress") public Location mLatLngAddress;
    @Json(name = "count_people_evaluate") public Integer mCountPeopleEvaluate;
    @Json(name = "main_point") public Double mMainPoint;
    @Json(name = "trust_point") public Long mTrustPoint;
//    @Json(name = "myCharacter") public String mCharacter;
//    @Json(name = "myStyle") public String mStyle;
//    @Json(name = "targetCharacter") public String mTargetCharacter;
//    @Json(name = "targetStyle") public String mTargetStyle;
//    @Json(name = "targetFood") public String mTargetFood;

    public boolean mIsSelected;

    public User(Integer id) {
        mId = id;
    }

    public User(Integer id, String fullName) {
        mId = id;
        mFullName = fullName;
    }
}
