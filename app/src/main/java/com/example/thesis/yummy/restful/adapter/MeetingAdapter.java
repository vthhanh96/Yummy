package com.example.thesis.yummy.restful.adapter;

import android.support.annotation.Nullable;

import com.example.thesis.yummy.restful.ServiceGenerator;
import com.example.thesis.yummy.restful.model.Meeting;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;

public class MeetingAdapter extends JsonAdapter<Meeting> {
    @Override
    public synchronized Meeting fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case NUMBER:
                return new Meeting(reader.nextInt());
            case BEGIN_OBJECT:
                JsonAdapter<Meeting> jsonAdapter = ServiceGenerator.getMoshiWithoutType(Meeting.class).adapter(Meeting.class);
                return jsonAdapter.fromJson(reader);
            case NULL:
                return reader.nextNull();
            default:
                throw new IOException();
        }
    }

    @Override
    public synchronized void toJson(JsonWriter writer, @Nullable Meeting value) throws IOException {
        writer.value(value.toString());
    }
}
