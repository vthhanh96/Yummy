package com.example.thesis.yummy.controller.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.forgotpassword.ForgotPasswordActivity;
import com.example.thesis.yummy.controller.home.HomeActivity;
import com.example.thesis.yummy.controller.register.RegisterActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.auth.AuthClient;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edtEmail) AppCompatEditText mEdtEmail;
    @BindView(R.id.edtPassword) AppCompatEditText mEdtPassword;

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    public static void start(Activity activity, View view) {
        Intent starter = new Intent(activity, LoginActivity.class);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, "logoImageView");
        activity.startActivity(starter, optionsCompat.toBundle());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @OnClick(R.id.btnForgotPassword)
    public void forgotPasswordButtonClicked() {
        forgotPassword();
    }

    @OnClick(R.id.btnLogin)
    public void loginButtonClicked() {
        login();
    }

    @OnClick(R.id.btnRegister)
    public void registerButtonClicked() {
        register();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    private void forgotPassword() {
        ForgotPasswordActivity.start(this);
    }

    private void login() {
        if(TextUtils.isEmpty(mEdtEmail.getText())) {
            Toast.makeText(this, R.string.enter_email, Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mEdtPassword.getText())) {
            Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        AuthClient.login(mEdtEmail.getText().toString(), mEdtPassword.getText().toString(), new AuthClient.AuthCallBack() {
            @Override
            public void onAuthorized() {
                hideLoading();
                Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                finish();
                HomeActivity.start(LoginActivity.this);
            }

            @Override
            public void onUnauthorized(String message) {
                hideLoading();
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register() {
        RegisterActivity.start(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
