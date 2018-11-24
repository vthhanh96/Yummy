package com.example.thesis.yummy.controller.meeting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Meeting;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.view.MultipleImageCircleView;
import com.example.thesis.yummy.view.TopBarView;
import com.example.thesis.yummy.view.dialog.QuestionDialog;
import com.example.thesis.yummy.view.dialog.listener.CustomDialogActionListener;

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
        getMeetings();
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
                Meeting item = mAdapter.getItem(position);
                if(item == null) return;

                MeetingDetailActivity.start(MeetingActivity.this, item.mId);
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                Meeting item = mAdapter.getItem(position);
                if(item == null) return false;

                showConfirmDialog(item);
                return false;
            }
        });

        mMeetingRecyclerView.setAdapter(mAdapter);
        mMeetingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getMeetings() {
        ServiceManager.getInstance().getMeetingService().getMeetings(false).enqueue(new RestCallback<List<Meeting>>() {
            @Override
            public void onSuccess(String message, List<Meeting> meetings) {
                mAdapter.setNewData(meetings);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MeetingActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmDialog(final Meeting meeting) {
        final QuestionDialog questionDialog = new QuestionDialog(getString(R.string.leave_meeting_confirm_question));
        questionDialog.setDialogActionListener(new CustomDialogActionListener() {
            @Override
            public void dialogCancel() {
                questionDialog.dismissDialog();
            }

            @Override
            public void dialogPerformAction() {
                leaveMeeting(meeting);
            }
        });
        questionDialog.show(getSupportFragmentManager(), MeetingActivity.class.getName());
    }

    private void leaveMeeting(Meeting meeting) {

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

            imageCircleView.setImages(images);

            helper.setText(R.id.placeTextView, item.mPlace);

            if(item.mTime != null) {
                helper.setText(R.id.timeTextView, DateFormat.format("dd/MM/yyyy hh:mm", item.mTime));
            }
        }
    }
}
