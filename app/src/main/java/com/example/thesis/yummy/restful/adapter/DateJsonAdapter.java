package com.example.thesis.yummy.restful.adapter;

import android.annotation.SuppressLint;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateJsonAdapter extends JsonAdapter<Date> {

    @Override
    public synchronized Date fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case STRING:
                String string = reader.nextString();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    return simpleDateFormat.parse(string);
                } catch (ParseException ignored) { }

                return IsoUtils.parse(string);
            case NULL:
                return reader.nextNull();
            default:
                throw new IOException();
        }

    }

    @Override
    public synchronized void toJson(JsonWriter writer, Date value) throws IOException {
        String string = IsoUtils.format(value);
        writer.value(string);
    }
}
