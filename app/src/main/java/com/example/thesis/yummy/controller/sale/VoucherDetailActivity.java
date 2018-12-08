package com.example.thesis.yummy.controller.sale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.post.AddPostActivity;
import com.example.thesis.yummy.restful.model.Voucher;
import com.example.thesis.yummy.view.TopBarView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoucherDetailActivity extends BaseActivity {

    private static final String ARG_KEY_VOUCHER = "ARG_KEY_VOUCHER";

    public static void start(Context context, Voucher voucher) {
        Intent starter = new Intent(context, VoucherDetailActivity.class);
        starter.putExtra(ARG_KEY_VOUCHER, voucher);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.webView) WebView mWebView;

    private Voucher mVoucher;

    @OnClick(R.id.linkToPostButton)
    public void linkToPostButtonClicked() {
        if(mVoucher == null || mVoucher.mLink == null) return;
        AddPostActivity.start(this, mVoucher.mLink);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_voucher_detail;
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
        initWebView();
    }

    private void getExtras() {
        mVoucher = (Voucher) getIntent().getSerializableExtra(ARG_KEY_VOUCHER);
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.voucher_detail));
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

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        if(mVoucher == null || mVoucher.mLink == null) return;
        mWebView.loadUrl(mVoucher.mLink);
        mWebView.getSettings().setJavaScriptEnabled(true);
    }
}
