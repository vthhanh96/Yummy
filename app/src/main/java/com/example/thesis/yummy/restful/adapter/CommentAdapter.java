package com.example.thesis.yummy.restful.adapter;

import android.support.annotation.Nullable;

import com.example.thesis.yummy.restful.ServiceGenerator;
import com.example.thesis.yummy.restful.model.Comment;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;

public class CommentAdapter extends JsonAdapter<Comment> {
    @Override
    public synchronized Comment fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case NUMBER:
                return new Comment(reader.nextInt());
            case BEGIN_OBJECT:
                JsonAdapter<Comment> jsonAdapter = ServiceGenerator.getMoshiWithoutType(Comment.class).adapter(Comment.class);
                return jsonAdapter.fromJson(reader);
            case NULL:
                return reader.nextNull();
            default:
                throw new IOException();
        }
    }

    @Override
    public synchronized void toJson(JsonWriter writer, @Nullable Comment value) throws IOException {
        writer.value(value.toString());
    }
}
