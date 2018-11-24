package com.example.thesis.yummy.controller.forgotpassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.login.LoginActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.view.TopBarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPasswordActivity extends BaseActivity {

    private static final String ARG_AUTH_CODE = "ARG_AUTH_CODE";
    private static final String ARG_KEY_EMAIL = "ARG_KEY_EMAIL";

    public static void start(Context context, String authCode, String email) {
        Intent starter = new Intent(context, ResetPasswordActivity.class);
        starter.putExtra(ARG_AUTH_CODE, authCode);
        starter.putExtra(ARG_KEY_EMAIL, email);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.codeEditText) EditText mCodeEditText;
    @BindView(R.id.passwordEditText) EditText mPasswordEditText;
    @BindView(R.id.confirmPasswordEditText) EditText mConfirmEditText;

    private String mAuthCode;
    private String mEmail;

    @OnClick(R.id.resetPasswordButton)
    public void onResetPasswordButton() {
        if(TextUtils.isEmpty(mCodeEditText.getText())) {
            Toast.makeText(this, getString(R.string.enter_auth_code), Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mPasswordEditText.getText())) {
            Toast.makeText(this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mConfirmEditText.getText())) {
            Toast.makeText(this, getString(R.string.enter_confirm_password), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!mCodeEditText.getText().toString().equals(mAuthCode)) {
            Toast.makeText(this, R.string.auth_code_not_match, Toast.LENGTH_SHORT).show();
            return;
        }
        if(!mPasswordEditText.getText().toString().equals(mConfirmEditText.getText().toString())) {
            Toast.makeText(this, R.string.enter_password_error, Toast.LENGTH_SHORT).show();
            return;
        }
        resetPassword();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        getExtras();
        initTopBar();
    }

    private void getExtras() {
        mAuthCode = getIntent().getStringExtra(ARG_AUTH_CODE);
        mEmail = getIntent().getStringExtra(ARG_KEY_EMAIL);
    }

    private void initTopBar() {
        mTopBarView.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
    }

    private void resetPassword() {
        showLoading();
        ServiceManager.getInstance().getUserService().changePassword(mEmail, mPasswordEditText.getText().toString()).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                Toast.makeText(ResetPasswordActivity.this, R.string.change_password_success, Toast.LENGTH_SHORT).show();
                LoginActivity.start(ResetPasswordActivity.this);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
