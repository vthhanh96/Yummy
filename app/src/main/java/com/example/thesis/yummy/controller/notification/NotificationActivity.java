package com.example.thesis.yummy.controller.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Notification;
import com.example.thesis.yummy.view.TopBarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_NOTIFICATION_PAGE;

public class NotificationActivity extends DrawerActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, NotificationActivity.class);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.notificationRecyclerView) RecyclerView mNotificationRecyclerView;

    private NotificationAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notification;
    }

    @Override
    protected int getNavId() {
        return NAV_DRAWER_ID_NOTIFICATION_PAGE;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initTopBar();
        initRecyclerView();
        getNotifications();
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.notification));
        mTopBarView.setImageViewLeft(TopBarView.LEFT_MENU);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                openDrawer();
            }

            @Override
            public void onRightClick() {

            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new NotificationAdapter();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

        mNotificationRecyclerView.setAdapter(mAdapter);
        mNotificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getNotifications() {
        ServiceManager.getInstance().getNotificationService().getNotifications().enqueue(new RestCallback<List<Notification>>() {
            @Override
            public void onSuccess(String message, List<Notification> notifications) {
                mAdapter.setNewData(notifications);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(NotificationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class NotificationAdapter extends BaseQuickAdapter<Notification, BaseViewHolder>{

        public NotificationAdapter() {
            super(R.layout.item_notification_layout, new ArrayList<Notification>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Notification item) {
            if(item == null) return;

            helper.setText(R.id.contentTextView, item.mContent);

            if(item.mCreatedDate != null) {
                helper.setText(R.id.timeTextView, DateFormat.format("dd MMM yyyy", item.mCreatedDate));
            }

            if(!TextUtils.isEmpty(item.mImage)) {
                ImageView imageView = helper.getView(R.id.avatarImageView);
                Glide.with(mContext.getApplicationContext()).load(item.mImage).apply(RequestOptions.circleCropTransform()).into(imageView);
            }
        }
    }
}
