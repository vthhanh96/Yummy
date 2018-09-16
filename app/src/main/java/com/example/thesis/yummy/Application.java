package com.example.thesis.yummy;

import android.content.Context;

import com.orhanobut.hawk.Hawk;


public class Application extends android.app.Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        init();
    }

    private void init() {
        initStorage();
    }

    private void initStorage() {
        Hawk.init(this).build();
    }
}
