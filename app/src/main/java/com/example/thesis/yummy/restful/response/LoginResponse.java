package com.example.thesis.yummy.restful.response;

import com.example.thesis.yummy.restful.model.User;
import com.squareup.moshi.Json;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    @Json(name = "success") public Boolean mSuccess;
    @Json(name = "token") public String mToken;
    @Json(name = "message") public String mMessage;
    @Json(name = "expiresIn") public long mExpiresIn;
    @Json(name = "data") public User mUser;
}
