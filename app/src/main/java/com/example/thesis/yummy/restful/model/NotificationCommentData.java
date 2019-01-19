package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

public class NotificationCommentData extends NotificationData {
    @Json(name = "data") public Comment mComment;
}
