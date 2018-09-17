package com.example.thesis.yummy.controller.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_HOME_PAGE;

public abstract class DrawerActivity extends BaseActivity {

    @BindView(R.id.drawerLayout) DrawerLayout mDrawerLayout;
    @BindView(R.id.rcvMenu) RecyclerView mMenuRecyclerView;

    private MenuAdapter mMenuAdapter;
    private List<ItemMenu> mMenuItems;

    protected abstract int getNavId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setUpListDrawer();
        initRecyclerView();
    }

    private void setUpListDrawer() {
        mMenuItems = new ArrayList<>();
        mMenuItems.add(new ItemMenu(NAV_DRAWER_ID_HOME_PAGE, "Home", R.drawable.ic_home, getNavId() == NAV_DRAWER_ID_HOME_PAGE));
    }

    private void initRecyclerView() {
        mMenuAdapter = new MenuAdapter();
        mMenuAdapter.setNewData(mMenuItems);
        mMenuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ItemMenu itemMenu = mMenuAdapter.getItem(position);
                if(itemMenu == null) return;
                onItemClickListener(itemMenu);
            }
        });

        mMenuRecyclerView.setAdapter(mMenuAdapter);
        mMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void onItemClickListener(ItemMenu itemMenu) {
        switch (itemMenu.mKey) {
            case NAV_DRAWER_ID_HOME_PAGE:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START);
    }

    protected void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }

    protected void openDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(Gravity.START);
        }
    }

    protected void lockDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    protected void unLockDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    private class MenuAdapter extends BaseQuickAdapter<ItemMenu, BaseViewHolder> {

        MenuAdapter() {
            super(R.layout.item_menu_layout, new ArrayList<ItemMenu>());
        }

        @Override
        protected void convert(BaseViewHolder helper, ItemMenu item) {
            helper.setImageResource(R.id.imgIcon, item.mIcon);
            helper.setText(R.id.txtText, item.mText);
            helper.setBackgroundColor(R.id.rootLayout, item.mIsSelected ? mContext.getResources().getColor(R.color.colorPrimaryDark) : mContext.getResources().getColor(R.color.colorPrimary));
            helper.setVisible(R.id.lineView, item.mIsSelected);
        }
    }
}
