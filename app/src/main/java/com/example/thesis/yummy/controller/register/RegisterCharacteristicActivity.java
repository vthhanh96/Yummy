package com.example.thesis.yummy.controller.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterCharacteristicActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;

    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterCharacteristicActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_characteristic;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initToolbar();
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
