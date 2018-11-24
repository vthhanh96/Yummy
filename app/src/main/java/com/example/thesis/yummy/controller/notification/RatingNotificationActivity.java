package com.example.thesis.yummy.controller.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.rating.RatingActivity;
import com.example.thesis.yummy.restful.model.Notification;
import com.example.thesis.yummy.restful.model.NotificationMeetingData;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.thesis.yummy.AppConstants.NOTIFICATION_MEETING;

public class RatingNotificationActivity extends BaseActivity {

    private static final String ARG_KEY_NOTIFICATION_DATA = "ARG_KEY_NOTIFICATION_DATA";

    public static void start(Context context, Notification notification) {
        Intent starter = new Intent(context, RatingNotificationActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra(ARG_KEY_NOTIFICATION_DATA, notification);
        context.startActivity(starter);
    }

    private int mMeetingID = -1;

    @OnClick(R.id.rejectButton)
    public void reject() {
        finish();
    }

    @OnClick(R.id.ratingButton)
    public void rating() {
        RatingActivity.start(this, mMeetingID);
        finish();
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
        getExtras();
    }

    private void getExtras() {
        Notification notification = (Notification) getIntent().getSerializableExtra(ARG_KEY_NOTIFICATION_DATA);
        if(notification.notificationData == null || notification.notificationData.mType == null) return;
        if(notification.notificationData.mType.equals(NOTIFICATION_MEETING)) {
            NotificationMeetingData meetingData = (NotificationMeetingData) notification.notificationData;
            if(meetingData != null && meetingData.mMeeting != null) {
                mMeetingID = meetingData.mMeeting.mId;
            }
        }
    }
}
