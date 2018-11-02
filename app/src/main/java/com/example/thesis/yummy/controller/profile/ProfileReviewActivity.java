package com.example.thesis.yummy.controller.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.rating.RatingDialog;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Rating;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.view.TopBarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import mehdi.sakout.fancybuttons.FancyButton;

public class ProfileReviewActivity extends BaseActivity {

    @BindView(R.id.topBar) TopBarView mTopBar;
    @BindView(R.id.reviewsRecyclerView) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.ratingBar) MaterialRatingBar mRatingBar;
    @BindView(R.id.rateButton) FancyButton mRateButton;

    private ReviewAdapter mReviewAdapter;
    private User mUser;

    public static void start(Context context) {
        Intent starter = new Intent(context, ProfileReviewActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.rateButton)
    public void rateButtonClicked() {
        showRatingDialog();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_review;
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
        getUserInfo();
    }

    private void initTopBar() {
        mTopBar.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBar.setTitle(getString(R.string.review));
        mTopBar.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
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
        mReviewAdapter = new ReviewAdapter();

        mReviewsRecyclerView.setAdapter(mReviewAdapter);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void getUserInfo() {
        showLoading();
        ServiceManager.getInstance().getUserService().getUserInfo().enqueue(new RestCallback<User>() {
            @Override
            public void onSuccess(String message, User user) {
                if(user == null) return;
                mUser = user;
                getListRating();
                mRatingBar.setRating((float)(user.mMainPoint / user.mCountPeopleEvaluate) / 2 );
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
            }
        });
    }

    private void getListRating() {
        ServiceManager.getInstance().getRatingService().getListRatingProfile(mUser.mId).enqueue(new RestCallback<List<Rating>>() {
            @Override
            public void onSuccess(String message, List<Rating> ratings) {
                mReviewAdapter.addData(ratings);
                checkRating();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
            }
        });
    }

    private void checkRating() {
        ServiceManager.getInstance().getRatingService().checkRatingPeople(mUser.mId).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                mRateButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                mRateButton.setVisibility(View.GONE);
            }
        });
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

    private void rating(Float rating, String comment) {
        showLoading();
        ServiceManager.getInstance().getRatingService().createRatingProfile(comment, (int)(rating * 2), mUser.mId).enqueue(new RestCallback<Rating>() {
            @Override
            public void onSuccess(String message, Rating rating) {
                mReviewAdapter.addData(rating);
                mRateButton.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(ProfileReviewActivity.this, message, Toast.LENGTH_SHORT).show();
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
            materialRatingBar.setRating(item.mPoint);
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
