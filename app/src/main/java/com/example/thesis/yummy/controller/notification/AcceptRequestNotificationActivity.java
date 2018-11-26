package com.example.thesis.yummy.controller.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.meeting.MeetingDetailActivity;
import com.example.thesis.yummy.restful.model.Notification;
import com.example.thesis.yummy.restful.model.NotificationMeetingData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.thesis.yummy.AppConstants.NOTIFICATION_MEETING;

public class AcceptRequestNotificationActivity extends BaseActivity {

    private static final String ARG_KEY_NOTIFICATION_DATA = "ARG_KEY_NOTIFICATION_DATA";

    public static void start(Context context, Notification notification) {
        Intent starter = new Intent(context, AcceptRequestNotificationActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra(ARG_KEY_NOTIFICATION_DATA, notification);
        context.startActivity(starter);
    }

    @BindView(R.id.messageRequestTextView) TextView mMessageRequestTextView;

    private int mMeetingID;

    @OnClick(R.id.rejectButton)
    public void reject() {
        finish();
    }

    @OnClick(R.id.acceptButton)
    public void accept() {
        finish();
        MeetingDetailActivity.start(this, mMeetingID);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_accept_request_notification;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Notification notification = (Notification) getIntent().getSerializableExtra(ARG_KEY_NOTIFICATION_DATA);
        if(notification.notificationData == null || notification.notificationData.mType == null) return;
        if(notification.notificationData.mType.equals(NOTIFICATION_MEETING)) {
            NotificationMeetingData meetingData = (NotificationMeetingData) notification.notificationData;
            if(meetingData != null && meetingData.mMeeting != null) {
                mMeetingID = meetingData.mMeeting.mId;
                mMessageRequestTextView.setText(notification.mTitle);
            }
        }
    }
}
