package com.example.thesis.yummy.controller.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.rating.RatingActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RatingNotificationActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, RatingNotificationActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.rejectButton)
    public void reject() {

    }

    @OnClick(R.id.ratingButton)
    public void rating() {
        RatingActivity.start(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rating_notification;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

    }
}
