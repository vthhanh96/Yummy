package com.example.thesis.yummy.controller.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
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
                getUserInfo();
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

    private void getUserInfo() {
        ServiceManager.getInstance().getUserService().getUserInfo().enqueue(new RestCallback<User>() {
            @Override
            public void onSuccess(String message, User user) {
                hideLoading();
                StorageManager.saveUser(user);
                Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                HomeActivity.start(LoginActivity.this);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                HomeActivity.start(LoginActivity.this);
            }
        });
    }
}
