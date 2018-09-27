package com.example.thesis.yummy.controller.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.auth.AuthClient;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.edtEmail) EditText mEdtEmail;
    @BindView(R.id.edtName) EditText mEdtName;
    @BindView(R.id.edtPassword) EditText mEdtPassword;
    @BindView(R.id.edtConfirmPassword) EditText mEdtConfirmPassword;

    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.btnRegister)
    public void register() {
        if (TextUtils.isEmpty(mEdtName.getText())) {
            Toast.makeText(this, R.string.enter_full_name, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mEdtEmail.getText())) {
            Toast.makeText(this, R.string.enter_email, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mEdtPassword.getText())) {
            Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mEdtConfirmPassword.getText())) {
            Toast.makeText(this, R.string.enter_confirm_password, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mEdtPassword.getText().toString().trim().equals(mEdtConfirmPassword.getText().toString().trim())) {
            Toast.makeText(this, R.string.enter_password_error, Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading();
        AuthClient.register(mEdtEmail.getText().toString(),
                mEdtName.getText().toString(),
                mEdtPassword.getText().toString(), new AuthClient.AuthCallBack() {
                    @Override
                    public void onAuthorized() {
                        hideLoading();
                        RegisterAvatarActivity.start(RegisterActivity.this);
                    }

                    @Override
                    public void onUnauthorized(String message) {
                        hideLoading();
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

}
