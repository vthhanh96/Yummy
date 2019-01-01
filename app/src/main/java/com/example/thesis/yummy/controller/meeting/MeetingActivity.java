package com.example.thesis.yummy.controller.meeting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Meeting;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.MultipleImageCircleView;
import com.example.thesis.yummy.view.TopBarView;
import com.example.thesis.yummy.view.dialog.QuestionDialog;
import com.example.thesis.yummy.view.dialog.ReasonDialog;
import com.example.thesis.yummy.view.dialog.listener.CustomDialogActionListener;
import com.facebook.shimmer.ShimmerFrameLayout;

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
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.meetingRecyclerView) RecyclerView mMeetingRecyclerView;

    private MeetingAdapter mAdapter;
    private ShimmerFrameLayout mShimmerView;

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
        initSwipeRefreshLayout();
        initRecyclerView();
        showShimmer();
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

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                mAdapter.setNewData(new ArrayList<Meeting>());
                getMeetings();
            }
        });
    }

    private void initRecyclerView() {
        mShimmerView = (ShimmerFrameLayout) View.inflate(this, R.layout.shimmer_notification_layout, null);

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
        ServiceManager.getInstance().getMeetingService().getMeetings(false, StorageManager.getUser().mId).enqueue(new RestCallback<List<Meeting>>() {
            @Override
            public void onSuccess(String message, List<Meeting> meetings) {
                hideShimmer();
                hideLoading();
                mAdapter.setNewData(meetings);
            }

            @Override
            public void onFailure(String message) {
                hideShimmer();
                hideLoading();
                Toast.makeText(MeetingActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showShimmer() {
        if(mShimmerView != null) {
            mShimmerView.startShimmer();
            mAdapter.addFooterView(mShimmerView);
        }
    }

    private void hideShimmer() {
        if(mShimmerView != null) {
            mShimmerView.stopShimmer();
            mAdapter.removeFooterView(mShimmerView);
        }
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
                showReasonDialog(meeting);
            }
        });
        questionDialog.show(getSupportFragmentManager(), MeetingActivity.class.getName());
    }

    private void showReasonDialog(final Meeting meeting) {
        ReasonDialog dialog = new ReasonDialog(getString(R.string.leave_meeting_reason));
        dialog.setReasonDialogListener(new ReasonDialog.ReasonDialogListener() {
            @Override
            public void onCancelButtonClicked() {

            }

            @Override
            public void onAcceptButtonClicked(String reason) {
                leaveMeeting(meeting, reason);
            }
        });
        dialog.show(getSupportFragmentManager(), MeetingActivity.class.getName());
    }

    private void leaveMeeting(final Meeting meeting, String reason) {
        showLoading();
        ServiceManager.getInstance().getMeetingService().leaveMeeting(meeting.mId, reason).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                Toast.makeText(MeetingActivity.this, message, Toast.LENGTH_SHORT).show();
                mAdapter.getData().remove(meeting);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(MeetingActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
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
                helper.setText(R.id.timeTextView, DateFormat.format("dd/MM/yyyy hh:mm aa", item.mTime));
            }
        }
    }
}
