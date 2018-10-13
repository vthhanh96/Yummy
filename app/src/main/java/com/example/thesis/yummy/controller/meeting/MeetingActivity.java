package com.example.thesis.yummy.controller.meeting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.restful.model.Meeting;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.view.MultipleImageCircleView;
import com.example.thesis.yummy.view.TopBarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_MEETING;

public class MeetingActivity extends DrawerActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, MeetingActivity.class);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.meetingRecyclerView) RecyclerView mMeetingRecyclerView;

    private MeetingAdapter mAdapter;

    @Override
    protected int getNavId() {
        return NAV_DRAWER_ID_MEETING;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activiy_meeting;
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
        mTopBarView.setTitle(getString(R.string.meeting));
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
        mAdapter = new MeetingAdapter();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

        mMeetingRecyclerView.setAdapter(mAdapter);
        mMeetingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class MeetingAdapter extends BaseQuickAdapter<Meeting, BaseViewHolder> {

        public MeetingAdapter() {
            super(R.layout.item_meeting_layout, new ArrayList<Meeting>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Meeting item) {
            if(item == null) return;
            MultipleImageCircleView imageCircleView = helper.getView(R.id.multipleImageCircleView);
            List<String> images = new ArrayList<>();

            if(item.mJoinedPeople != null) {
                for (User user : item.mJoinedPeople) {
                    images.add(user.mAvatar);
                }
            }

            imageCircleView.setImages((String[]) images.toArray());

            helper.setText(R.id.placeTextView, item.mPlace);

            if(item.mTime != null) {
                helper.setText(R.id.timeTextView, DateFormat.format("dd/MM/yyyy hh:mm", item.mTime));
            }
        }
    }
}
