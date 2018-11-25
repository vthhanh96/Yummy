package com.example.thesis.yummy.controller.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.TopBarView;
import com.example.thesis.yummy.view.dialog.SelectReviewOptionsDialogFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import mehdi.sakout.fancybuttons.FancyButton;

public class ProfileReviewActivity extends BaseActivity {

    private static final String ARG_KEY_USER_ID = "ARG_KEY_USER_ID";

    @BindView(R.id.topBar) TopBarView mTopBar;
    @BindView(R.id.reviewsRecyclerView) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.ratingBar) MaterialRatingBar mRatingBar;
    @BindView(R.id.rateButton) FancyButton mRateButton;

    private ReviewAdapter mReviewAdapter;
    private int mUserId;
    private User mUser;

    public static void start(Context context, int userId) {
        Intent starter = new Intent(context, ProfileReviewActivity.class);
        starter.putExtra(ARG_KEY_USER_ID, userId);
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
        getExtras();
        initTopBar();
        initRecyclerView();
        showLoading();
        getUserInfo();
    }

    private void getExtras() {
        mUserId = getIntent().getIntExtra(ARG_KEY_USER_ID, -1);
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
        mReviewAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                Rating rating = mReviewAdapter.getItem(position);
                if(rating == null) return false;
                showRatingActionPopup(rating);
                return false;
            }
        });

        mReviewsRecyclerView.setAdapter(mReviewAdapter);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void getUserInfo() {
        ServiceManager.getInstance().getUserService().getUserInfo(mUserId).enqueue(new RestCallback<User>() {
            @Override
            public void onSuccess(String message, User user) {
                if(user == null) return;
                mUser = user;
                getListRating();
                if(user.mCountPeopleEvaluate != 0) {
                    mRatingBar.setRating((float) (user.mMainPoint / user.mCountPeopleEvaluate) / 2);
                }
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
                mReviewAdapter.setNewData(ratings);
                checkRating();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
            }
        });
    }

    private void checkRating() {
        ServiceManager.getInstance().getRatingService().checkRatingPeople(mUserId).enqueue(new RestCallback<Base>() {
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
                getUserInfo();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(ProfileReviewActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRatingActionPopup(final Rating rating) {
        User user = StorageManager.getUser();
        if(user != null && user.mId.equals(rating.mCreator.mId)) {
            SelectReviewOptionsDialogFragment dialogFragment = new SelectReviewOptionsDialogFragment();
            dialogFragment.setReviewOptionsListener(new SelectReviewOptionsDialogFragment.SelectReviewOptionsListener() {
                @Override
                public void editReview() {
                    showEditRatingDialog(rating);
                }

                @Override
                public void deleteReview() {
                    onDeleteRating(rating);
                }
            });
            dialogFragment.show(getSupportFragmentManager(), "");
        }
    }

    private void showEditRatingDialog(final Rating currentRating) {
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

    private void onDeleteRating(Rating rating) {
        showLoading();
        ServiceManager.getInstance().getRatingService().deleteRating(rating.mId).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                getUserInfo();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(ProfileReviewActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRating(int ratingId, float point, String content) {
        showLoading();
        ServiceManager.getInstance().getRatingService().updateRatingProfile(ratingId, (int)(point * 2), content).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                getUserInfo();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
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
            materialRatingBar.setRating(item.mPoint / 2.0f);
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
