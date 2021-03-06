package com.example.thesis.yummy.controller.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.example.thesis.yummy.controller.chat.ChatListActivity;
import com.example.thesis.yummy.controller.home.HomeActivity;
import com.example.thesis.yummy.controller.login.LoginActivity;
import com.example.thesis.yummy.controller.main.MainActivity;
import com.example.thesis.yummy.controller.meeting.MeetingActivity;
import com.example.thesis.yummy.controller.notification.NotificationActivity;
import com.example.thesis.yummy.controller.profile.ProfileActivity;
import com.example.thesis.yummy.controller.sale.SaleActivity;
import com.example.thesis.yummy.controller.search.SearchActivity;
import com.example.thesis.yummy.restful.auth.AuthClient;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.DrawerFooterLayout;
import com.example.thesis.yummy.view.DrawerHeaderLayout;
import com.example.thesis.yummy.view.dialog.QuestionDialog;
import com.example.thesis.yummy.view.dialog.listener.CustomDialogActionListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_CHAT;
import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_HOME_PAGE;
import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_LOGOUT;
import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_MAIN;
import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_MEETING;
import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_NOTIFICATION_PAGE;
import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_SALE;
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
        mMenuItems.add(new ItemMenu(NAV_DRAWER_ID_MAIN, getString(R.string.home), R.drawable.ic_home_black, R.drawable.ic_home_green, getNavId() == NAV_DRAWER_ID_MAIN));
        mMenuItems.add(new ItemMenu(NAV_DRAWER_ID_NOTIFICATION_PAGE, getString(R.string.notification), R.drawable.ic_notification_black, R.drawable.ic_notification_green, getNavId() == NAV_DRAWER_ID_NOTIFICATION_PAGE));
        mMenuItems.add(new ItemMenu(NAV_DRAWER_ID_SALE, getString(R.string.sale), R.drawable.ic_sale_black, R.drawable.ic_sale_green, getNavId() == NAV_DRAWER_ID_SALE));
        mMenuItems.add(new ItemMenu(NAV_DRAWER_ID_MEETING, getString(R.string.meeting), R.drawable.ic_meeting_black, R.drawable.ic_meeting_green, getNavId() == NAV_DRAWER_ID_MEETING));
        mMenuItems.add(new ItemMenu(NAV_DRAWER_ID_CHAT, getString(R.string.chat), R.drawable.ic_chat, R.drawable.ic_chat_selected, getNavId() == NAV_DRAWER_ID_CHAT));
        mMenuItems.add(new ItemMenu(NAV_DRAWER_ID_LOGOUT, getString(R.string.logout), R.drawable.ic_logout_black, R.drawable.ic_logout_black,getNavId() == NAV_DRAWER_ID_LOGOUT));
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
            case NAV_DRAWER_ID_MAIN:
                if(this instanceof MainActivity) return;
                finish();
                MainActivity.start(this);
                break;
            case NAV_DRAWER_ID_NOTIFICATION_PAGE:
                if(this instanceof NotificationActivity) return;
                if((!(this instanceof HomeActivity))) {
                    finish();
                }
                NotificationActivity.start(this);
                break;
            case NAV_DRAWER_ID_LOGOUT:
                showConfirmLogoutDialog();
                break;
            case NAV_DRAWER_ID_MEETING:
                if(this instanceof MeetingActivity) return;
                if((!(this instanceof HomeActivity))) {
                    finish();
                }
                MeetingActivity.start(this);
                break;
            case NAV_DRAWER_ID_SALE:
                if(this instanceof SaleActivity) return;
                if((!(this instanceof HomeActivity))) {
                    finish();
                }
                SaleActivity.start(this, false);
                break;
            case NAV_DRAWER_ID_CHAT:
                if(this instanceof ChatListActivity) return;
                if((!(this instanceof HomeActivity))) {
                    finish();
                }
                ChatListActivity.start(this);
                break;
        }
    }

    private void confirmExitApp() {
        final QuestionDialog questionDialog = new QuestionDialog(getString(R.string.confirm_exit_app));
        questionDialog.setDialogActionListener(new CustomDialogActionListener() {
            @Override
            public void dialogCancel() {
                questionDialog.dismissDialog();
            }

            @Override
            public void dialogPerformAction() {
                finish();
            }
        });
        questionDialog.show(getSupportFragmentManager(), DrawerActivity.class.getName());
    }


    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            closeDrawer();
        } else {
            super.onBackPressed();
//            if(isLockDrawer()) {
//                super.onBackPressed();
//            } else {
//                confirmExitApp();
//            }
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

    protected boolean isLockDrawer() {
        return mDrawerLayout != null && mDrawerLayout.getDrawerLockMode(mDrawerLayout) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
    }

    private void showConfirmLogoutDialog() {
        final QuestionDialog questionDialog = new QuestionDialog(getString(R.string.confirm_log_out_message));
        questionDialog.setDialogActionListener(new CustomDialogActionListener() {
            @Override
            public void dialogCancel() {
                questionDialog.dismissDialog();
            }

            @Override
            public void dialogPerformAction() {
                logout();
            }
        });
        questionDialog.show(getSupportFragmentManager(), DrawerActivity.class.getName());

    }

    private void logout() {
        finish();
        AuthClient.logout();
        LoginActivity.start(this);
    }

    private class MenuAdapter extends BaseQuickAdapter<ItemMenu, BaseViewHolder> {

        MenuAdapter() {
            super(R.layout.item_menu_layout, new ArrayList<ItemMenu>());
        }

        @Override
        protected void convert(BaseViewHolder helper, ItemMenu item) {
            helper.setText(R.id.txtText, item.mText);
            if(item.mIsSelected) {
                helper.setBackgroundRes(R.id.rootLayout, R.drawable.bg_corner_solid_green_light);
                helper.setTextColor(R.id.txtText, ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
                helper.setImageResource(R.id.imgIcon, item.mIconSelected);
            } else {
                helper.setImageResource(R.id.imgIcon, item.mIcon);
                helper.setBackgroundColor(R.id.rootLayout, ContextCompat.getColor(mContext, R.color.white));
                helper.setTextColor(R.id.txtText, ContextCompat.getColor(mContext, R.color.black));
            }
        }
    }
}
