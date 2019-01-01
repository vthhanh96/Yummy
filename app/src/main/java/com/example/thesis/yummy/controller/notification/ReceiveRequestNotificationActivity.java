package com.example.thesis.yummy.controller.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Notification;
import com.example.thesis.yummy.restful.model.NotificationRequestData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.http.Body;

import static com.example.thesis.yummy.AppConstants.NOTIFICATION_REQUEST;

public class ReceiveRequestNotificationActivity extends BaseActivity {


    private static final String ARG_KEY_NOTIFICATION = "ARG_KEY_NOTIFICATION";

    public static void start(Context context, Notification notification) {
        Intent starter = new Intent(context, ReceiveRequestNotificationActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra(ARG_KEY_NOTIFICATION, notification);
        context.startActivity(starter);
    }

    @BindView(R.id.messageRequestTextView) TextView mMessageRequestTextView;
    @BindView(R.id.placeTextView) TextView mPlaceTextView;
    @BindView(R.id.timeTextView) TextView mTimeTextView;
    @BindView(R.id.contentTextView) TextView mContentTextView;

    private Notification mNotification;
    private int mRequestID;

    @OnClick(R.id.rejectButton)
    public void reject() {
        showLoading();
        ServiceManager.getInstance().getUserService().rejectRequest(mRequestID).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                Toast.makeText(ReceiveRequestNotificationActivity.this, R.string.reject_request_message, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(ReceiveRequestNotificationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.acceptButton)
    public void accept() {
        showLoading();
        ServiceManager.getInstance().getUserService().acceptRequest(mRequestID).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                Toast.makeText(ReceiveRequestNotificationActivity.this, R.string.accept_request_message, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(ReceiveRequestNotificationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_receive_request_notification;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        getExtras();
        initData();
    }

    private void getExtras() {
        mNotification = (Notification) getIntent().getSerializableExtra(ARG_KEY_NOTIFICATION);
    }

    private void initData() {
        if(mNotification.notificationData == null || mNotification.notificationData.mType == null) return;
        if(mNotification.notificationData.mType.equals(NOTIFICATION_REQUEST)) {
            NotificationRequestData requestData = (NotificationRequestData) mNotification.notificationData;
            if(requestData != null && requestData.mRequest != null) {
                mRequestID = requestData.mRequest.mId;
                mPlaceTextView.setText(requestData.mRequest.mPlace);
                mTimeTextView.setText(DateFormat.format("dd/MM/yyyy hh:mm aa", requestData.mRequest.mTime));
                mContentTextView.setText(requestData.mRequest.mContent);
                mMessageRequestTextView.setText(getString(R.string.send_request_title, requestData.mRequest.mCreator.mFullName));
            }
        }
    }
}
