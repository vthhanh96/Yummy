package com.example.thesis.yummy.controller.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.view.dialog.CustomProgressDialog;

public abstract class BaseActivity extends AppCompatActivity {

    private CustomProgressDialog mProgressDialog;

    protected abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        init();
    }

    private void init() {
        initProgressDialog();
    }

    private void initProgressDialog() {
        mProgressDialog = new CustomProgressDialog(this, getString(R.string.loading));
    }

    public void showLoading() {
        if(mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    public void hideLoading() {
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public boolean isLoading() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }
}
