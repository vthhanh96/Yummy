package com.example.thesis.yummy.restful.adapter;

import android.support.annotation.Nullable;

import com.example.thesis.yummy.restful.ServiceGenerator;
import com.example.thesis.yummy.restful.model.Meeting;
import com.example.thesis.yummy.restful.model.MeetingPoint;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;

public class MeetingPointAdapter extends JsonAdapter<MeetingPoint> {
    @Override
    public synchronized MeetingPoint fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case NUMBER:
                return new MeetingPoint(reader.nextInt());
            case BEGIN_OBJECT:
                JsonAdapter<MeetingPoint> jsonAdapter = ServiceGenerator.getMoshiWithoutType(MeetingPoint.class).adapter(MeetingPoint.class);
                return jsonAdapter.fromJson(reader);
            case NULL:
                return reader.nextNull();
            default:
                throw new IOException();
        }
    }

    @Override
    public synchronized void toJson(JsonWriter writer, @Nullable MeetingPoint value) throws IOException {
        writer.value(value.toString());
    }
}
