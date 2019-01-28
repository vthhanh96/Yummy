package com.example.thesis.yummy.controller.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.auth.AuthClient;
import com.example.thesis.yummy.restful.model.Base;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterEnterCodeActivity extends BaseActivity {

    private static final String ARG_KEY_NAME = "ARG_KEY_NAME";
    private static final String ARG_KEY_EMAIL = "ARG_KEY_EMAIL";
    private static final String ARG_KEY_PASSWORD = "ARG_KEY_PASSWORD";
    private static final String ARG_KEY_CODE = "ARG_KEY_CODE";

    public static void start(Context context, String name, String email, String password, String code) {
        Intent starter = new Intent(context, RegisterEnterCodeActivity.class);
        starter.putExtra(ARG_KEY_NAME, name);
        starter.putExtra(ARG_KEY_EMAIL, email);
        starter.putExtra(ARG_KEY_PASSWORD, password);
        starter.putExtra(ARG_KEY_CODE, code);
        context.startActivity(starter);
    }

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.codeEditText) EditText mCodeEditText;

    private String mName;
    private String mEmail;
    private String mPassword;
    private String mCode;

    @OnClick(R.id.resendButton)
    public void resendCode() {
        showLoading();
        ServiceManager.getInstance().getUserService().sendCodeForgotPassword(mEmail).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                mCode = message;
                Toast.makeText(RegisterEnterCodeActivity.this, R.string.resend_validate_code_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(RegisterEnterCodeActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.validateButton)
    public void validate() {
        if(!mCodeEditText.getText().toString().equals(mCode)) {
            Toast.makeText(this, R.string.auth_code_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading();
        AuthClient.register(mEmail, mName, mPassword, new AuthClient.AuthCallBack() {
            @Override
            public void onAuthorized() {
                hideLoading();
                RegisterAvatarActivity.start(RegisterEnterCodeActivity.this);
            }

            @Override
            public void onUnauthorized(String message) {
                hideLoading();
                Toast.makeText(RegisterEnterCodeActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.register_enter_code_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initToolbar();
        getExtras();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void getExtras() {
        mName = getIntent().getStringExtra(ARG_KEY_NAME);
        mEmail = getIntent().getStringExtra(ARG_KEY_EMAIL);
        mPassword = getIntent().getStringExtra(ARG_KEY_PASSWORD);
        mCode = getIntent().getStringExtra(ARG_KEY_CODE);
    }
}
