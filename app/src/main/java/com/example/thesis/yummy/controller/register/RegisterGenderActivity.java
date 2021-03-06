package com.example.thesis.yummy.controller.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterGenderActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.imgMaleChecked) ImageView mImgMaleChecked;
    @BindView(R.id.imgFemaleChecked) ImageView mImgFemaleChecked;

    private boolean mIsMale;

    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterGenderActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.maleLayout)
    public void chooseMale() {
        mImgMaleChecked.setVisibility(View.VISIBLE);
        mImgFemaleChecked.setVisibility(View.INVISIBLE);
        mIsMale = true;
    }

    @OnClick(R.id.femaleLayout)
    public void chooseFemale() {
        mImgFemaleChecked.setVisibility(View.VISIBLE);
        mImgMaleChecked.setVisibility(View.INVISIBLE);
        mIsMale = false;
    }

    @OnClick(R.id.btnNext)
    public void next() {
        showLoading();
        ServiceManager.getInstance().getUserService().updateGender(mIsMale ? 1 : 0).enqueue(new RestCallback<User>() {
            @Override
            public void onSuccess(String message, User user) {
                hideLoading();
                StorageManager.saveUser(user);
                RegisterBirthdayActivity.start(RegisterGenderActivity.this);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(RegisterGenderActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_gender;
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
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
