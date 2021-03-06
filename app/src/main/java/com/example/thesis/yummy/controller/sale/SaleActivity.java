package com.example.thesis.yummy.controller.sale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Voucher;
import com.example.thesis.yummy.view.TopBarView;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_SALE;
import static com.example.thesis.yummy.restful.model.Voucher.HOT_DEAL;

public class SaleActivity extends DrawerActivity {

    private static final String ARG_KEY_HAS_BACK_BUTTON = "ARG_KEY_HAS_BACK_BUTTON";

    public static void start(Context context, boolean hasBackButton) {
        Intent starter = new Intent(context, SaleActivity.class);
        starter.putExtra(ARG_KEY_HAS_BACK_BUTTON, hasBackButton);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.saleRecyclerView) RecyclerView mSaleRecyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;

    private SaleAdapter mSaleAdapter;
    private int mPageNumber = 0;
    private ShimmerFrameLayout mShimmerView;
    private boolean mHasBackButton;

    @Override
    protected int getNavId() {
        return NAV_DRAWER_ID_SALE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sale;
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
        initRefreshLayout();
        initRecyclerView();
        getVouchers();
    }

    private void getExtras() {
        mHasBackButton = getIntent().getBooleanExtra(ARG_KEY_HAS_BACK_BUTTON, false);
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.sale));
        mTopBarView.setImageViewLeft(mHasBackButton ? TopBarView.LEFT_BACK : TopBarView.LEFT_MENU);
        if(mHasBackButton) {
            lockDrawer();
        }
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                if(mHasBackButton) {
                    finish();
                } else {
                    openDrawer();
                }
            }

            @Override
            public void onRightClick() {

            }
        });
    }

    private void initRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                mSaleAdapter.setNewData(null);
                mPageNumber = 0;
                getVouchers();
            }
        });
    }

    private void initRecyclerView() {
        mShimmerView = (ShimmerFrameLayout) View.inflate(this, R.layout.shimmer_voucher_layout, null);

        mSaleAdapter = new SaleAdapter();
        mSaleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Voucher item = mSaleAdapter.getItem(position);
                if(item == null) return;
                VoucherDetailActivity.start(SaleActivity.this, item);
            }
        });

        mSaleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSaleRecyclerView.setAdapter(mSaleAdapter);

        mSaleAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getVouchers();
            }
        }, mSaleRecyclerView);
    }

    private void getVouchers() {
        showShimmer();
        ServiceManager.getInstance().getVoucherService().getVouchersNearMe(mPageNumber).enqueue(new RestCallback<List<Voucher>>() {
            @Override
            public void onSuccess(String message, List<Voucher> vouchers) {
                hideShimmer();
                if(vouchers == null || vouchers.isEmpty()) {
                    mSaleAdapter.loadMoreEnd();
                    return;
                }
                mSaleAdapter.loadMoreComplete();
                mSaleAdapter.addData(vouchers);
                mPageNumber++;
            }

            @Override
            public void onFailure(String message) {
                hideShimmer();
                mSaleAdapter.loadMoreFail();
                Toast.makeText(SaleActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showShimmer() {
        if(mShimmerView != null) {
            mShimmerView.startShimmer();
            mSaleAdapter.addFooterView(mShimmerView);
        }
    }

    private void hideShimmer() {
        if(mShimmerView != null) {
            mShimmerView.stopShimmer();
            mSaleAdapter.removeFooterView(mShimmerView);
        }
    }

    private class SaleAdapter extends BaseQuickAdapter<Voucher, BaseViewHolder> {

        public SaleAdapter() {
            super(R.layout.item_sale_layout, new ArrayList<Voucher>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Voucher item) {
            ImageView imageView = helper.getView(R.id.voucherImageView);
            Glide.with(mContext.getApplicationContext()).load(item.mImage).into(imageView);
            if(item.mHost.equals(HOT_DEAL)) {
                helper.setImageResource(R.id.logoImageView, R.drawable.logo_hotdeal);
            } else {
                helper.setImageResource(R.id.logoImageView, R.drawable.logo_foody);
            }
            helper.setText(R.id.titleTextView, item.mTitle);
            helper.setText(R.id.placeTextView, item.mLocation);
        }
    }
}
