package com.example.thesis.yummy.view.dialog.Interpolator;

import android.view.animation.Interpolator;

/**
 * Created by HieuMinh on 4/17/2018.
 */

public class EaseOutInterpolator implements Interpolator {
    public EaseOutInterpolator() {
    }

    @Override
    public float getInterpolation(float time) {
        return (float) (time / 2 * Math.pow(2,  time ) * Math.cos(Math.PI * 2 * time) + Math.pow(time, 3) + Math.pow(time, 2)/2);
    }
}