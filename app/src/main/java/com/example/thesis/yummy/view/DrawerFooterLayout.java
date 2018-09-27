package com.example.thesis.yummy.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.login.LoginActivity;
import com.example.thesis.yummy.storage.StorageManager;

public class DrawerFooterLayout extends LinearLayout{
    public DrawerFooterLayout(Context context) {
        this(context, null, 0);
    }

    public DrawerFooterLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerFooterLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
        initClickListener();
    }

    private void initView() {
        inflate(getContext(), R.layout.layout_drawer_footer, this);
    }

    private void initClickListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageManager.deleteAll();
                LoginActivity.start(getContext());
            }
        });
    }
}