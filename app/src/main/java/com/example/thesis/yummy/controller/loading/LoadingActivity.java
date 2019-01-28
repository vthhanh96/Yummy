package com.example.thesis.yummy.controller.loading;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.boarding.BoardingActivity;
import com.example.thesis.yummy.controller.home.HomeActivity;
import com.example.thesis.yummy.controller.login.LoginActivity;
import com.example.thesis.yummy.controller.main.MainActivity;
import com.example.thesis.yummy.restful.auth.AuthClient;
import com.example.thesis.yummy.storage.StorageManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingActivity extends BaseActivity {

    @BindView(R.id.imageView) ImageView mLogoImageView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_loading;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 1000);
    }

    private void init() {
//        if(StorageManager.isFirstTime()) {
//            StorageManager.saveIsFirstTime(false);
//            BoardingActivity.start(this);
//            return;
//        }
        if(AuthClient.isExpireToken()) {
            LoginActivity.start(this, mLogoImageView);
        } else {
            MainActivity.start(this);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }
}
