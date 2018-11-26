package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

public class NotificationRequestData extends NotificationData {
    @Json(name = "data") public Request mRequest;
}
