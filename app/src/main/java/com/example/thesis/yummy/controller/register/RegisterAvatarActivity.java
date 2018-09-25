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
import butterknife.OnClick;

public class RegisterAvatarActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterAvatarActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.btnNext)
    public void nextButtonClicked() {
        RegisterGenderActivity.start(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_avatar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
    }

    @Override
    public void onBackPressed() {

    }
}
