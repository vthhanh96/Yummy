package com.example.thesis.yummy.controller.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.search.SearchActivity;
import com.example.thesis.yummy.restful.model.Notification;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RejectRequestNotificationActivity extends BaseActivity {

    private static final String ARG_KEY_NOTIFICATION_DATA = "ARG_KEY_NOTIFICATION_DATA";

    public static void start(Context context, Notification notification) {
        Intent starter = new Intent(context, RejectRequestNotificationActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra(ARG_KEY_NOTIFICATION_DATA, notification);
        context.startActivity(starter);
    }

    @BindView(R.id.messageRequestTextView)
    TextView mMessageRequestTextView;

    @OnClick(R.id.rejectButton)
    public void reject() {
        finish();
    }

    @OnClick(R.id.acceptButton)
    public void accept() {
        finish();
        SearchActivity.start(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reject_request_notification;
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
        if(notification == null) return;
        mMessageRequestTextView.setText(notification.mTitle);
    }
}
