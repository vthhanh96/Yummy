package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

public class User extends Base{
    @Json(name = "id") public Integer mId;
    @Json(name = "phone") public String mPhone;
    @Json(name = "email") public String mEmail;
    @Json(name = "fullName") public String mFullName;
    @Json(name = "password") public String mPassword;
    @Json(name = "avatar") public String mAvatar;
    @Json(name = "birthday") public String mBirthDay;
    @Json(name = "gender") public String mGender;
    @Json(name = "address") public String mAddress;
    @Json(name = "latLngAdress") public String mLatLngAddress;
    @Json(name = "myCharacter") public String mCharacter;
    @Json(name = "myStyle") public String mStyle;
    @Json(name = "targetCharacter") public String mTargetCharacter;
    @Json(name = "targetStyle") public String mTargetStyle;
    @Json(name = "targetFood") public String mTargetFood;

    public User(Integer id) {
        mId = id;
    }
}
