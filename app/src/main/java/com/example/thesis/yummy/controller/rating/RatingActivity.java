package com.example.thesis.yummy.controller.rating;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;

import butterknife.ButterKnife;

public class RatingActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, RatingActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rating;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }
}
