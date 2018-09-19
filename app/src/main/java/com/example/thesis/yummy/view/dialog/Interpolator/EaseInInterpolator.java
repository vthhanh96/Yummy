package com.example.thesis.yummy.view.dialog.Interpolator;

/**
 * Created by HieuMinh on 4/17/2018.
 */

public class EaseInInterpolator implements android.view.animation.Interpolator {
    public EaseInInterpolator() {
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(4, - 5 * time ) * Math.cos(3* Math.PI * time) + 1);
    }
}