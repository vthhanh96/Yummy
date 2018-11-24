package com.example.thesis.yummy.controller.forgotpassword;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, ForgotPasswordActivity.class);
        context.startActivity(starter);
    }

    @BindView(R.id.emailEditText) AppCompatEditText mEmailEditText;

    @OnClick(R.id.sendCodeButton)
    public void onSendCodeButtonClicked() {
        if(TextUtils.isEmpty(mEmailEditText.getText())) {
            Toast.makeText(this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
            return;
        }
        sendCode();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    private void sendCode() {
        showLoading();
        ServiceManager.getInstance().getUserService().sendCodeForgotPassword(mEmailEditText.getText().toString()).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                ResetPasswordActivity.start(ForgotPasswordActivity.this, message, mEmailEditText.getText().toString());
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
