package com.example.thesis.yummy.controller.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.TopBarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_PROFILE_PAGE;

public class ProfileActivity extends DrawerActivity {

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.imgAvatar) ImageView mImgAvatar;
    @BindView(R.id.txtName) TextView mTxtName;
    @BindView(R.id.txtBirthday) TextView mTxtBirthday;

    private User mUser;

    public static void start(Context context) {
        Intent starter = new Intent(context, ProfileActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.postLayout)
    public void openMyPost() {
        ProfilePostActivity.start(this);
    }

    @OnClick(R.id.historyLayout)
    public void openHistory() {
        ProfileHistoryActivity.start(this);
    }

    @OnClick(R.id.reviewLayout)
    public void openReview() {
        ProfileReviewActivity.start(this);
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

    private void init() {
        initTopBar();
        initData();
    }

    private void initTopBar() {
        mTopBarView.setImageViewLeft(TopBarView.LEFT_MENU);
        mTopBarView.setImageViewRight(R.drawable.ic_edit);
        mTopBarView.setTitle(getString(R.string.profile));
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                openDrawer();
            }

            @Override
            public void onRightClick() {
                EditProfileActivity.start(ProfileActivity.this);
            }
        });
    }

    private void initData() {
        mUser = StorageManager.getUser();
        if (mUser == null) return;
        if (TextUtils.isEmpty(mUser.mAvatar)) {
            mImgAvatar.setImageResource(R.drawable.ic_default_avatar);
        } else {
            Glide.with(getApplicationContext()).load(mUser.mAvatar).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
        }
        mTxtName.setText(mUser.mFullName);
        if(mUser.mBirthDay != null) {
            mTxtBirthday.setText(DateFormat.format("dd MMMM yyyy", mUser.mBirthDay));
        }
    }
}
