package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

public class NotificationData extends Base {
    @Json(name = "type") public Integer mType;
}
