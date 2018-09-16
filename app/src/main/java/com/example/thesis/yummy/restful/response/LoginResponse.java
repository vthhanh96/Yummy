package com.example.thesis.yummy.restful.response;

import com.squareup.moshi.Json;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    @Json(name = "success") public Boolean mSuccess;
    @Json(name = "token") public String mToken;
    @Json(name = "_id") public Integer mId;
    @Json(name = "message") public String mMessage;
}
