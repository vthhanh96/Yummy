package com.example.thesis.yummy.utils;

import android.content.Context;

import java.util.Date;

public class DateUtils {
    public static String getTimeAgo(Context context, Date date) {
        final long SECOND_MILLIS = 1000;
        final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final long DAY_MILLIS = 24 * HOUR_MILLIS;
        final long MONTH_MILLIS = 30 * DAY_MILLIS;
        final long YEAR_MILLIS = 365 * DAY_MILLIS;

        long time = date.getTime();
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = new Date().getTime();
        if (time > now || time <= 0) {
            return "";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Mới đây";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " phút";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " giờ";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1 ngày";
        } else if (diff < 30 * DAY_MILLIS) {
            return diff / DAY_MILLIS + " ngày";
        } else if (diff < 365 * DAY_MILLIS) {
            return diff / MONTH_MILLIS + " tháng";
        } else {
            return diff / YEAR_MILLIS + " năm";
        }
    }
}
