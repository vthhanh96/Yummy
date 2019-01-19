package com.example.thesis.yummy.restful.adapter;

import android.support.annotation.Nullable;

import com.example.thesis.yummy.restful.ServiceGenerator;
import com.example.thesis.yummy.restful.model.NotificationCommentData;
import com.example.thesis.yummy.restful.model.NotificationData;
import com.example.thesis.yummy.restful.model.NotificationMeetingData;
import com.example.thesis.yummy.restful.model.NotificationPostData;
import com.example.thesis.yummy.restful.model.NotificationRequestData;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;
import java.util.Map;

import static com.example.thesis.yummy.AppConstants.NOTIFICATION_COMMENT;
import static com.example.thesis.yummy.AppConstants.NOTIFICATION_MEETING;
import static com.example.thesis.yummy.AppConstants.NOTIFICATION_POST;
import static com.example.thesis.yummy.AppConstants.NOTIFICATION_REQUEST;

public class NotificationDataAdapter extends JsonAdapter<NotificationData> {
    @Override
    public NotificationData fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case NUMBER:
                return new NotificationData();
            case NULL:
                return reader.nextNull();
            case BEGIN_OBJECT:
                Map<String, Object> map = (Map<String, Object>) reader.readJsonValue();
                Double type = (Double) map.get("type");

                switch (type.intValue()) {
                    case NOTIFICATION_POST:
                        JsonAdapter<NotificationPostData> postDataJsonAdapter = ServiceGenerator.getMoshiWithoutType(NotificationData.class).adapter(NotificationPostData.class);
                        return postDataJsonAdapter.fromJsonValue(map);
                    case NOTIFICATION_MEETING:
                        JsonAdapter<NotificationMeetingData> meetingDataJsonAdapter = ServiceGenerator.getMoshiWithoutType(NotificationData.class).adapter(NotificationMeetingData.class);
                        return meetingDataJsonAdapter.fromJsonValue(map);
                    case NOTIFICATION_REQUEST:
                        JsonAdapter<NotificationRequestData> requestDataJsonAdapter = ServiceGenerator.getMoshiWithoutType(NotificationData.class).adapter(NotificationRequestData.class);
                        return requestDataJsonAdapter.fromJsonValue(map);
                    case NOTIFICATION_COMMENT:
                        JsonAdapter<NotificationCommentData> commentDataJsonAdapter = ServiceGenerator.getMoshiWithoutType(NotificationData.class).adapter(NotificationCommentData.class);
                        return commentDataJsonAdapter.fromJsonValue(map);
                }

                JsonAdapter<NotificationData> notificationDataJsonAdapter = ServiceGenerator.getMoshiWithoutType(NotificationData.class).adapter(NotificationData.class);
                return notificationDataJsonAdapter.fromJsonValue(map);

            default:
                throw new IOException();

        }
    }

    @Override
    public void toJson(JsonWriter writer, @Nullable NotificationData value) throws IOException {

    }
}
