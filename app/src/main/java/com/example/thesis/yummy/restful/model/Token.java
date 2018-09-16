package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

public class Token extends Base {
    @Json(name = "token") public String mToken;
}
