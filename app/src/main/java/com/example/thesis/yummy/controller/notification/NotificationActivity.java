package com.example.thesis.yummy.controller.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.restful.model.Notification;
import com.example.thesis.yummy.view.TopBarView;

import java.util.ArrayList;

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


        mAdapter.addData(new Notification("ABC da chap nhan yeu cau cua ban"));
        mAdapter.addData(new Notification("ABC da chap nhan yeu cau cua ban"));
        mAdapter.addData(new Notification("ABC da chap nhan yeu cau cua ban"));
        mAdapter.addData(new Notification("ABC da chap nhan yeu cau cua ban"));
        mAdapter.addData(new Notification("ABC da chap nhan yeu cau cua ban"));
    }

    private class NotificationAdapter extends BaseQuickAdapter<Notification, BaseViewHolder>{

        public NotificationAdapter() {
            super(R.layout.item_notification_layout, new ArrayList<Notification>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Notification item) {
            if(item == null) return;

            helper.setText(R.id.contentTextView, item.mContent);
        }
    }
}
