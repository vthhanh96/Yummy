package com.example.thesis.yummy.controller.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.meeting.MeetingDetailActivity;
import com.example.thesis.yummy.eventbus.EventRating;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Rating;
import com.example.thesis.yummy.view.TopBarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ProfileReviewDetailActivity extends BaseActivity {

    private static final String ARG_KEY_MEETING_ID = "ARG_KEY_MEETING_ID";
    private static final String ARG_KEY_USER_ID = "ARG_KEY_USER_ID";

    public static void start(Context context, int meetingId, int userId) {
        Intent starter = new Intent(context, ProfileReviewDetailActivity.class);
        starter.putExtra(ARG_KEY_MEETING_ID, meetingId);
        starter.putExtra(ARG_KEY_USER_ID, userId);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.reviewRecylerView) RecyclerView mReviewRecyclerView;

    private ReviewAdapter mAdapter;
    private int mMeetingId;
    private int mUserId;

    @OnClick(R.id.meetingDetailButton)
    public void openMeetingDetail() {
        MeetingDetailActivity.start(this, mMeetingId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_review_detail_layout;
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
        initReviewRecyclerView();
        getReviewDetail();
        EventBus.getDefault().register(this);
    }

    private void getExtras() {
        mMeetingId = getIntent().getIntExtra(ARG_KEY_MEETING_ID, -1);
        mUserId = getIntent().getIntExtra(ARG_KEY_USER_ID, -1);
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.review_detail));
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

    private void initReviewRecyclerView() {
        mAdapter = new ReviewAdapter();

        mReviewRecyclerView.setAdapter(mAdapter);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getReviewDetail() {
        ServiceManager.getInstance().getMeetingService().getMeetingRating(mMeetingId, mUserId).enqueue(new RestCallback<List<Rating>>() {
            @Override
            public void onSuccess(String message, List<Rating> ratings) {
                mAdapter.setNewData(ratings);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private class ReviewAdapter extends BaseQuickAdapter<Rating, BaseViewHolder> {

        public ReviewAdapter() {
            super(R.layout.item_review_layout, new ArrayList<Rating>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Rating item) {
            MaterialRatingBar materialRatingBar = helper.getView(R.id.ratingBar);
            materialRatingBar.setRating(item.mPoint / 2f);
            helper.setText(R.id.commentTextView, item.mContent);

            if(item.mCreator != null) {
                helper.setText(R.id.nameTextView, item.mCreator.mFullName);
                ImageView avatarImageView = helper.getView(R.id.avatarImageView);
                Glide.with(mContext.getApplicationContext()).load(item.mCreator.mAvatar).apply(RequestOptions.circleCropTransform()).into(avatarImageView);
            } else {
                helper.setText(R.id.nameTextView, "");
                helper.setImageResource(R.id.avatarImageView, R.drawable.ic_default_avatar);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateRating(EventRating eventRating) {
        getReviewDetail();
    }
}
