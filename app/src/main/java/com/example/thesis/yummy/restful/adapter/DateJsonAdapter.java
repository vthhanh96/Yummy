package com.example.thesis.yummy.restful.adapter;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DateJsonAdapter extends JsonAdapter<Date> {

    @Override public synchronized Date fromJson(JsonReader reader) throws IOException {
        switch (reader.peek()) {
            case STRING:
                String string = reader.nextString();
                List<SimpleDateFormat> knownPatterns = new ArrayList<>();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                knownPatterns.add(simpleDateFormat);

                for (SimpleDateFormat pattern : knownPatterns) {
                    try {
                        return new Date(pattern.parse(string).getTime());
                    } catch (ParseException ignored) {
                    }
                }

                return IsoUtils.parse(string);
            case NULL:
                return reader.nextNull();
            default:
                throw new IOException();
        }

    }

    @Override public synchronized void toJson(JsonWriter writer, Date value) throws IOException {
        String string = IsoUtils.format(value);
        writer.value(string);
    }
}
