package com.example.thesis.yummy.restful.adapter;

import android.support.annotation.Nullable;

import com.example.thesis.yummy.restful.ServiceGenerator;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.restful.model.User;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;

public class UserAdapter extends JsonAdapter<User> {
    @Override
    public synchronized User fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case NUMBER:
                return new User(reader.nextInt());
            case BEGIN_OBJECT:
                JsonAdapter<User> jsonAdapter = ServiceGenerator.getMoshiWithoutType(User.class).adapter(User.class);
                return jsonAdapter.fromJson(reader);
            case NULL:
                return reader.nextNull();
            default:
                throw new IOException();
        }
    }

    @Override
    public synchronized void toJson(JsonWriter writer, @Nullable User value) throws IOException {
        writer.value(value.toString());
    }
}
