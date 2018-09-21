package com.example.thesis.yummy.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;

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
        Glide.with(getContext().getApplicationContext()).load(user.mAvatar).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
        mTvName.setText(user.mFullName);
    }
}
