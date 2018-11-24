package com.example.thesis.yummy.controller.rating;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Meeting;
import com.example.thesis.yummy.restful.model.MeetingPoint;
import com.example.thesis.yummy.restful.model.Rating;
import com.example.thesis.yummy.view.TopBarView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RatingActivity extends BaseActivity {

    private static final String ARG_KEY_MEETING_ID = "ARG_KEY_MEETING_ID";

    public static void start(Context context, int meetingID) {
        Intent starter = new Intent(context, RatingActivity.class);
        starter.putExtra(ARG_KEY_MEETING_ID, meetingID);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.ratingRecyclerView) RecyclerView mRatingRecyclerView;

    private RatingAdapter mRatingAdapter;
    private int mMeetingID;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rating;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        getExtras();
        initTopBar();
        initRecyclerView();
        getMeetingDetail();
    }

    private void getExtras() {
        mMeetingID = getIntent().getIntExtra(ARG_KEY_MEETING_ID, -1);
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.review));
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

    private void initRecyclerView() {
        mRatingAdapter = new RatingAdapter();
        mRatingAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MeetingPoint item = mRatingAdapter.getItem(position);
                if(item == null || item.mUser == null) return;

                RatingDetailActivity.start(RatingActivity.this, item.mUser.mId, mMeetingID);
            }
        });

        mRatingRecyclerView.setAdapter(mRatingAdapter);
        mRatingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getMeetingDetail() {
        showLoading();
        ServiceManager.getInstance().getMeetingService().getMeetingDetail(mMeetingID).enqueue(new RestCallback<Meeting>() {
            @Override
            public void onSuccess(String message, Meeting meeting) {
                hideLoading();
                if(meeting == null) return;
                mRatingAdapter.setNewData(meeting.mMeetingPoints);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(RatingActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class RatingAdapter extends BaseQuickAdapter<MeetingPoint, BaseViewHolder> {

        public RatingAdapter() {
            super(R.layout.item_notification_rating_layout, new ArrayList<MeetingPoint>());
        }

        @Override
        protected void convert(BaseViewHolder helper, MeetingPoint item) {
            if(item == null) return;

            ImageView avatarView = helper.getView(R.id.avatarImageView);
            if(item.mUser == null) {
                avatarView.setImageResource(R.drawable.ic_default_avatar);
            } else {
                RequestOptions options = new RequestOptions()
                        .circleCrop()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar);
                Glide.with(mContext.getApplicationContext()).load(item.mUser.mAvatar).apply(options).into(avatarView);
                helper.setText(R.id.nameTextView, item.mUser.mFullName);
            }

            RatingBar ratingBar = helper.getView(R.id.ratingBar);
            if(item.mPointSum == null || item.mCountPeople == null ||item.mCountPeople == 0) {
                ratingBar.setRating(0);
            } else {
                ratingBar.setRating((item.mPointSum / item.mCountPeople) / 2f);
            }
        }
    }
}
