package com.example.thesis.yummy.controller.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.shared.EmptyLayout;
import com.example.thesis.yummy.eventbus.EventRating;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Meeting;
import com.example.thesis.yummy.restful.model.MeetingPoint;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.view.TopBarView;
import com.github.vipulasri.timelineview.LineType;
import com.github.vipulasri.timelineview.TimelineView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileHistoryActivity extends BaseActivity {

    private static final String ARG_KEY_USER_ID = "ARG_KEY_USER_ID";

    @BindView(R.id.topBar)
    TopBarView mTopBarView;
    @BindView(R.id.rcvHistory)
    RecyclerView mHistoryRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private TimelineAdapter mAdapter;
    private int mUserId;

    public static void start(Context context, int userId) {
        Intent starter = new Intent(context, ProfileHistoryActivity.class);
        starter.putExtra(ARG_KEY_USER_ID, userId);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_history;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        getExtras();
        initTopBar();
        initRecyclerView();
        iniRefreshLayout();
        showLoading();
        getMeetingHistory();
        EventBus.getDefault().register(this);
    }

    private void getExtras() {
        mUserId = getIntent().getIntExtra(ARG_KEY_USER_ID, -1);
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.history));
        mTopBarView.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
    }

    private void iniRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                showLoading();
                getMeetingHistory();
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new TimelineAdapter();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Meeting item = mAdapter.getItem(position);
                if (item == null) return;
                ProfileReviewDetailActivity.start(ProfileHistoryActivity.this, item.mId, mUserId);
            }
        });

        EmptyLayout emptyLayout = new EmptyLayout(this);
        emptyLayout.setEmptyImageMessage(getString(R.string.empty_meeting));
        emptyLayout.setEmptyImageResource(R.drawable.ic_empty_meeting);
        mAdapter.setEmptyView(emptyLayout);

        mHistoryRecyclerView.setAdapter(mAdapter);
        mHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getMeetingHistory() {
        ServiceManager.getInstance().getMeetingService().getMeetings(true, mUserId).enqueue(new RestCallback<List<Meeting>>() {
            @Override
            public void onSuccess(String message, List<Meeting> meetings) {
                hideLoading();
                mAdapter.setNewData(meetings);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(ProfileHistoryActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class TimelineAdapter extends BaseQuickAdapter<Meeting, BaseViewHolder> {

        public TimelineAdapter() {
            super(R.layout.item_history, new ArrayList<Meeting>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Meeting item) {
            if (item == null) return;

            helper.setText(R.id.dateTextView, DateFormat.format("dd MMM yyyy", item.mTime));
            helper.setText(R.id.placeTextView, item.mPlace);

            RatingBar ratingBar = helper.getView(R.id.ratingBar);
            ratingBar.setRating(0);
            if (item.mMeetingPoints != null) {
                for (MeetingPoint meetingPoint : item.mMeetingPoints) {
                    if (meetingPoint.mUser == null || meetingPoint.mPointSum == null || meetingPoint.mCountPeople == null || meetingPoint.mCountPeople == 0)
                        continue;
                    if (meetingPoint.mUser.mId == mUserId) {
                        ratingBar.setRating((meetingPoint.mPointSum / meetingPoint.mCountPeople) / 2f);
                    }
                }
            }

            RecyclerView recyclerView = helper.getView(R.id.rcvPeople);
            PeopleAdapter adapter = new PeopleAdapter();
            adapter.addData(item.mJoinedPeople);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private class PeopleAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

        public PeopleAdapter() {
            super(R.layout.item_people_join_in, new ArrayList<User>());
        }

        @Override
        protected void convert(BaseViewHolder helper, User item) {
            if (item != null) {
                if (TextUtils.isEmpty(item.mAvatar)) {
                    helper.setImageResource(R.id.avatarImageView, R.drawable.ic_default_avatar);
                } else {
                    ImageView avatarImageView = helper.getView(R.id.avatarImageView);
                    Glide.with(mContext).load(item.mAvatar).apply(RequestOptions.circleCropTransform()).into(avatarImageView);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateRating(EventRating eventRating) {
        getMeetingHistory();
    }
}
