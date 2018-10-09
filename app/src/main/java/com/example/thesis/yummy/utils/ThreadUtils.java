package com.example.thesis.yummy.utils;

import android.os.Handler;
import android.os.Looper;

public class ThreadUtils {
    private Handler mUIHandler;

    private static ThreadUtils instance;

    private ThreadUtils() {
        mUIHandler = new Handler(Looper.getMainLooper());
    }

    public static ThreadUtils getInstance() {
        if(instance == null) {
            synchronized (ThreadUtils.class) {
                if(instance == null) {
                    instance = new ThreadUtils();
                }
            }
        }
        return instance;
    }

    public void runOnUIThread(Runnable runnable) {
        if(Thread.currentThread() == mUIHandler.getLooper().getThread()) {
            runnable.run();
        } else {
            mUIHandler.post(runnable);
        }
    }

    public void postOnUIThread(Runnable runnable) {
        mUIHandler.post(runnable);
    }

    public void postDelayedOnUIThread(Runnable runnable, int delayInMilliseconds) {
        mUIHandler.postDelayed(runnable, delayInMilliseconds);
    }
}
