package com.example.thesis.yummy.restful.adapter;

import android.support.annotation.Nullable;

import com.example.thesis.yummy.restful.ServiceGenerator;
import com.example.thesis.yummy.restful.model.Category;
import com.example.thesis.yummy.restful.model.Comment;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;

public class CategoryAdapter extends JsonAdapter<Category> {
    @Override
    public synchronized Category fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case NUMBER:
                return new Category(reader.nextInt());
            case BEGIN_OBJECT:
                JsonAdapter<Category> jsonAdapter = ServiceGenerator.getMoshiWithoutType(Category.class).adapter(Category.class);
                return jsonAdapter.fromJson(reader);
            case NULL:
                return reader.nextNull();
            default:
                throw new IOException();
        }
    }

    @Override
    public synchronized void toJson(JsonWriter writer, @Nullable Category value) throws IOException {
        writer.value(value.toString());
    }
}
