package com.example.thesis.yummy.restful.adapter;

import android.support.annotation.Nullable;

import com.example.thesis.yummy.restful.ServiceGenerator;
import com.example.thesis.yummy.restful.model.Location;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;

public class LocationAdapter extends JsonAdapter<Location> {

    @Nullable
    @Override
    public Location fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case NUMBER:
                return new Location();
            case STRING:
                return new Location();
            case NULL:
                return reader.nextNull();
            case BEGIN_OBJECT:
                JsonAdapter<Location> jsonAdapter = ServiceGenerator.getMoshiWithoutType(Location.class).adapter(Location.class);
                return jsonAdapter.fromJson(reader);
            default:
                throw new IOException();
        }
    }

    @Override
    public void toJson(JsonWriter writer, @Nullable Location value) throws IOException {
        writer.value(value.toString());
    }
}
