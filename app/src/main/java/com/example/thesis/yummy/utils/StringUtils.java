package com.example.thesis.yummy.utils;

import android.annotation.SuppressLint;

public class StringUtils {

    @SuppressLint("DefaultLocale")
    public static String convertDistanceToString(Double distance) {
        if(distance == null) return "";

        if(distance < 1) {
            return String.format("%.2f m", distance * 1000);
        }

        return String.format("%.2f km", distance);
    }
}
