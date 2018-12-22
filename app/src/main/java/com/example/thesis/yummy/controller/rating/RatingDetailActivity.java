package com.example.thesis.yummy.controller.rating;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.eventbus.EventRating;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Rating;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.TopBarView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import mehdi.sakout.fancybuttons.FancyButton;

public class RatingDetailActivity extends BaseActivity {

    private static final String ARG_USER_RATING = "ARG_USER_RATING";
    private static final String ARG_MEETING_ID = "ARG_MEETING_ID";

    public static void start(Context context, User user, int meetingID) {
        Intent starter = new Intent(context, RatingDetailActivity.class);
        starter.putExtra(ARG_USER_RATING, user);
        starter.putExtra(ARG_MEETING_ID, meetingID);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.ratingRecyclerView) RecyclerView mRatingRecyclerView;
    @BindView(R.id.rateButton) FancyButton mRateButton;
    @BindView(R.id.editRatingButton) FancyButton mEditRatingButton;

    private User mUser;
    private int mMeetingID;
    private ReviewAdapter mAdapter;
    private Rating mCurrentRating;

    @OnClick(R.id.rateButton)
    public void onRateButtonClicked() {
        rate();
    }

    @OnClick(R.id.editRatingButton)
    public void onEditRatingButtonClicked() {
        editRate();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rating_detail;
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
        showLoading();
        getReviewDetail();
    }

    private void getExtras() {
        mUser = (User) getIntent().getSerializableExtra(ARG_USER_RATING);
        mMeetingID = getIntent().getIntExtra(ARG_MEETING_ID, -1);
    }

    private void initTopBar() {
        String title = getString(R.string.review_detail) + (mUser == null || TextUtils.isEmpty(mUser.mFullName) ? "" : " cho " + mUser.mFullName);
        mTopBarView.setTitle(title);
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
        mAdapter = new ReviewAdapter();

        mRatingRecyclerView.setAdapter(mAdapter);
        mRatingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getReviewDetail() {
        ServiceManager.getInstance().getMeetingService().getMeetingRating(mMeetingID, mUser.mId).enqueue(new RestCallback<List<Rating>>() {
            @Override
            public void onSuccess(String message, List<Rating> ratings) {
                hideLoading();
                mAdapter.setNewData(ratings);
                showButton(ratings);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(RatingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showButton(List<Rating> ratings) {
        User currentUser = StorageManager.getUser();
        if(currentUser == null || ratings == null || mUser == null) return;
        if(mUser.mId.equals(currentUser.mId)) {
            mRateButton.setVisibility(View.GONE);
            mEditRatingButton.setVisibility(View.GONE);
            return;
        }
        for (Rating rating : ratings) {
            if(rating.mCreator == null) {
                continue;
            }

            if(currentUser.mId.equals(rating.mCreator.mId)) {
                mCurrentRating = rating;
                mRateButton.setVisibility(View.GONE);
                mEditRatingButton.setVisibility(View.VISIBLE);
                return;
            }
        }

        mRateButton.setVisibility(View.VISIBLE);
        mEditRatingButton.setVisibility(View.GONE);
    }

    private void rate() {
        showRatingDialog();
    }

    private void editRate() {
        showEditRatingDialog(mCurrentRating);
    }

    private void showRatingDialog() {
        RatingDialog ratingDialog = new RatingDialog(this, getString(R.string.rate));
        ratingDialog.setRatingDialogListener(new RatingDialog.RatingDialogListener() {
            @Override
            public void onRating(Float rating, String comment) {
                rating(rating, comment);
            }
        });
        ratingDialog.show();
    }

    private void showEditRatingDialog(final Rating currentRating) {
        if(currentRating == null) return;
        RatingDialog ratingDialog = new RatingDialog(this, getString(R.string.rate));
        ratingDialog.setRatingDialogListener(new RatingDialog.RatingDialogListener() {
            @Override
            public void onRating(Float rating, String comment) {
                updateRating(currentRating.mId, rating, comment);
            }
        });
        ratingDialog.setRating(currentRating);
        ratingDialog.show();
    }

    private void rating(Float rate, String comment) {
        if(mUser == null) return;
        showLoading();
        ServiceManager.getInstance().getRatingService().createRatingMeeting(mMeetingID, comment, (int) (rate * 2), mUser.mId).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                EventBus.getDefault().post(new EventRating());
                getReviewDetail();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(RatingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRating(int ratingID, float rate, String comment) {
        showLoading();
        ServiceManager.getInstance().getRatingService().updateRatingProfile(ratingID, (int)(rate * 2), comment).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                EventBus.getDefault().post(new EventRating());
                getReviewDetail();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(RatingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
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
}
