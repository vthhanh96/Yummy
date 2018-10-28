package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

public class NotificationPostData extends NotificationData {
    @Json(name = "data") public Post mPost;
}
