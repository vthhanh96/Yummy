package com.example.thesis.yummy.controller.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.home.HomeActivity;
import com.example.thesis.yummy.controller.login.LoginActivity;
import com.example.thesis.yummy.controller.notification.NotificationActivity;
import com.example.thesis.yummy.controller.profile.ProfileActivity;
import com.example.thesis.yummy.controller.search.SearchActivity;
import com.example.thesis.yummy.restful.auth.AuthClient;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.DrawerFooterLayout;
import com.example.thesis.yummy.view.DrawerHeaderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_HOME_PAGE;
import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_LOGOUT;
import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_NOTIFICATION_PAGE;
import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_SEARCH_PAGE;

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
        mMenuItems.add(new ItemMenu(NAV_DRAWER_ID_HOME_PAGE, getString(R.string.home), R.drawable.ic_home, getNavId() == NAV_DRAWER_ID_HOME_PAGE));
        mMenuItems.add(new ItemMenu(NAV_DRAWER_ID_NOTIFICATION_PAGE, getString(R.string.notification), R.drawable.ic_notification, getNavId() == NAV_DRAWER_ID_NOTIFICATION_PAGE));
        mMenuItems.add(new ItemMenu(NAV_DRAWER_ID_SEARCH_PAGE, getString(R.string.quick_search), R.drawable.ic_search_menu, getNavId() == NAV_DRAWER_ID_SEARCH_PAGE));
        mMenuItems.add(new ItemMenu(NAV_DRAWER_ID_LOGOUT, getString(R.string.logout), R.drawable.ic_logout, getNavId() == NAV_DRAWER_ID_LOGOUT));
    }

    private void initRecyclerView() {
        mMenuAdapter = new MenuAdapter();
        mMenuAdapter.setNewData(mMenuItems);
        mMenuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                closeDrawer();
                ItemMenu itemMenu = mMenuAdapter.getItem(position);
                if(itemMenu == null) return;
                onItemClickListener(itemMenu);
            }
        });

        DrawerHeaderLayout headerLayout = new DrawerHeaderLayout(this);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                if(DrawerActivity.this instanceof ProfileActivity) return;
                finish();
                ProfileActivity.start(DrawerActivity.this);
            }
        });
        mMenuAdapter.addHeaderView(headerLayout);

        mMenuRecyclerView.setAdapter(mMenuAdapter);
        mMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void onItemClickListener(ItemMenu itemMenu) {
        switch (itemMenu.mKey) {
            case NAV_DRAWER_ID_HOME_PAGE:
                if(this instanceof HomeActivity) return;
                finish();
                HomeActivity.start(this);
                break;
            case NAV_DRAWER_ID_NOTIFICATION_PAGE:
                if(this instanceof NotificationActivity) return;
                finish();
                NotificationActivity.start(this);
                break;
            case NAV_DRAWER_ID_SEARCH_PAGE:
                if(this instanceof SearchActivity) return;
                finish();
                SearchActivity.start(this);
                break;
            case NAV_DRAWER_ID_LOGOUT:
                finish();
                AuthClient.logout();
                LoginActivity.start(this);
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
            helper.setBackgroundColor(R.id.rootLayout, item.mIsSelected ? mContext.getResources().getColor(R.color.colorPrimary) : mContext.getResources().getColor(R.color.colorPrimaryDark));
            helper.setVisible(R.id.lineView, item.mIsSelected);
        }
    }
}
