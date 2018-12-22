package com.example.thesis.yummy.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.eventbus.EventUpdateProfile;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrawerHeaderLayout extends LinearLayout {

    @BindView(R.id.imgAvatar) ImageView mImgAvatar;
    @BindView(R.id.txtName) TextView mTvName;

    public DrawerHeaderLayout(Context context) {
        this(context, null, 0);
    }

    public DrawerHeaderLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerHeaderLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.layout_drawer_header, this);
        ButterKnife.bind(this, view);
    }

    private void initData() {
        User user = StorageManager.getUser();
        if(user == null) return;
        if(TextUtils.isEmpty(user.mAvatar)) {
            mImgAvatar.setImageResource(R.drawable.ic_default_avatar);
        } else {
            Glide.with(getContext().getApplicationContext()).load(user.mAvatar).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
        }
        mTvName.setText(user.mFullName);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUser(EventUpdateProfile eventUpdateProfile) {
        initData();
    }
}
