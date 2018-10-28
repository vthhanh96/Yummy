package com.example.thesis.yummy.restful.model;

import com.squareup.moshi.Json;

public class NotificationMeetingData extends NotificationData {
    @Json(name = "data") public Meeting mMeeting;
}
