package com.example.thesis.yummy.controller.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.controller.chat.ChatActivity;
import com.example.thesis.yummy.eventbus.EventUpdateProfile;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.TopBarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_PROFILE_PAGE;

public class ProfileActivity extends DrawerActivity {

    private static final String ARG_KEY_USER_ID = "ARG_KEY_USER_ID";
    private static final String ARG_KEY_LEFT_BACK = "ARG_KEY_LEFT_BACK";

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.imgAvatar) ImageView mImgAvatar;
    @BindView(R.id.txtName) TextView mTxtName;
    @BindView(R.id.txtBirthday) TextView mTxtBirthday;
    @BindView(R.id.meetingAmountTextView) TextView mMeetingAmountTextView;
    @BindView(R.id.trustPointTextView) TextView mTrustPointTextView;
    @BindView(R.id.postAmountTextView) TextView mPostAmountTextView;
    @BindView(R.id.historyAmountTextView) TextView mHistoryAmountTextView;
    @BindView(R.id.reviewAmountTextView) TextView mReviewAmountTextView;
    @BindView(R.id.reviewPointTextView) TextView mReviewPointTextView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;

    private boolean mIsMyProfile = false;
    private boolean mIsLeftBack = false;
    private int mUserId;
    private User mUser;

    public static void start(Context context) {
        Intent starter = new Intent(context, ProfileActivity.class);
        context.startActivity(starter);
    }

    public static void start(Context context, int id) {
        Intent starter = new Intent(context, ProfileActivity.class);
        starter.putExtra(ARG_KEY_USER_ID, id);
        starter.putExtra(ARG_KEY_LEFT_BACK, true);
        context.startActivity(starter);
    }

    @OnClick(R.id.postLayout)
    public void openMyPost() {
        ProfilePostActivity.start(this, mUserId);
    }

    @OnClick(R.id.historyLayout)
    public void openHistory() {
        ProfileHistoryActivity.start(this, mUserId);
    }

    @OnClick(R.id.reviewLayout)
    public void openReview() {
        ProfileReviewActivity.start(this, mUserId);
    }

    @Override
    protected int getNavId() {
        return NAV_DRAWER_ID_PROFILE_PAGE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile;
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
        initRefreshLayout();
        initData();
        EventBus.getDefault().register(this);
    }

    private void getExtras() {
        mUserId = getIntent().getIntExtra(ARG_KEY_USER_ID, StorageManager.getUser().mId);
        if(mUserId == StorageManager.getUser().mId) {
            mIsMyProfile = true;
        }
        mIsLeftBack = getIntent().getBooleanExtra(ARG_KEY_LEFT_BACK, false);
    }

    private void initTopBar() {
        mTopBarView.setImageViewLeft(mIsLeftBack ? TopBarView.LEFT_BACK : TopBarView.LEFT_MENU);
        mTopBarView.setTitle(getString(R.string.profile));
        if(mIsMyProfile) {
            mTopBarView.setImageViewRight(R.drawable.ic_edit);
        } else {
            mTopBarView.setImageViewRight(R.drawable.ic_chat_white);
        }
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                if(mIsLeftBack) {
                    finish();
                } else {
                    openDrawer();
                }
            }

            @Override
            public void onRightClick() {
                if(mIsMyProfile) {
                    EditProfileActivity.start(ProfileActivity.this);
                } else {
                    ChatActivity.start(ProfileActivity.this, mUser);
                }
            }
        });
    }

    private void initRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                showLoading();
                getUserInfo();
            }
        });
    }

    private void initData() {
        showLoading();
        getUserInfo();
    }

    private void getUserInfo() {
        ServiceManager.getInstance().getUserService().getUserInfo(mUserId).enqueue(new RestCallback<User>() {
            @Override
            public void onSuccess(String message, User user) {
                hideLoading();
                mUser = user;
                fillData(user);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillData(User user) {
        if (user == null) return;
        if (TextUtils.isEmpty(user.mAvatar)) {
            mImgAvatar.setImageResource(R.drawable.ic_default_avatar);
        } else {
            Glide.with(getApplicationContext()).load(user.mAvatar).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
        }
        mTxtName.setText(user.mFullName);
        if(user.mBirthDay != null) {
            mTxtBirthday.setText(DateFormat.format("dd MMMM yyyy", user.mBirthDay));
        }

        mMeetingAmountTextView.setText(String.valueOf(user.mMeetingAmount));
        mTrustPointTextView.setText(String.valueOf(user.mTrustPoint));
        mPostAmountTextView.setText(String.valueOf(user.mPostAmount));
        mHistoryAmountTextView.setText(String.valueOf(user.mMeetingAmount));
        mReviewAmountTextView.setText(String.valueOf(user.mCountPeopleEvaluate));
        if(user.mCountPeopleEvaluate != 0) {
            mReviewPointTextView.setText(String.format(Locale.getDefault(), "%.2f", (user.mMainPoint / user.mCountPeopleEvaluate) / 2f));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUser(EventUpdateProfile eventUpdateProfile) {
        getUserInfo();
    }
}
