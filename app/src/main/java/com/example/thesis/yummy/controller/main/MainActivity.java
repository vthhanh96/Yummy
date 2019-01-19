package com.example.thesis.yummy.controller.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.controller.home.HomeActivity;
import com.example.thesis.yummy.controller.post.AddPostActivity;
import com.example.thesis.yummy.controller.profile.ProfileActivity;
import com.example.thesis.yummy.controller.sale.SaleActivity;
import com.example.thesis.yummy.controller.sale.VoucherDetailActivity;
import com.example.thesis.yummy.controller.search.SearchActivity;
import com.example.thesis.yummy.eventbus.EventUpdatePost;
import com.example.thesis.yummy.eventbus.EventUpdateProfile;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.restful.model.Voucher;
import com.example.thesis.yummy.restful.request.PostRequest;
import com.example.thesis.yummy.restful.request.UserRequest;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.PermissionUtils;
import com.example.thesis.yummy.utils.StringUtils;
import com.example.thesis.yummy.view.PostRecyclerView;
import com.example.thesis.yummy.view.TopBarView;
import com.example.thesis.yummy.view.dialog.QuestionDialog;
import com.example.thesis.yummy.view.dialog.listener.CustomDialogActionListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_MAIN;
import static com.example.thesis.yummy.restful.model.Voucher.HOT_DEAL;

public class MainActivity extends DrawerActivity {

    private static final int REQUEST_CODE_GET_LOCATION = 10;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.peopleNearMeRecyclerView) RecyclerView mPeopleRecyclerView;
    @BindView(R.id.saleRecyclerView) RecyclerView mSaleRecyclerView;
    @BindView(R.id.postRecyclerView) PostRecyclerView mPostRecyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;

    private LocationCallback mLocationCallback;
    private PeopleAdapter mPeopleAdapter;
    private VoucherAdapter mVoucherAdapter;
    private User mUser;

    @OnClick(R.id.searchCardView)
    public void openSearch() {
        SearchActivity.start(this);
    }

    @OnClick(R.id.seeMoreSale)
    public void seeMoreSale() {
        SaleActivity.start(this, true);
    }

    @OnClick(R.id.seeMorePost)
    public void seeMorePost() {
        HomeActivity.start(this);
    }

    @Override
    protected int getNavId() {
        return NAV_DRAWER_ID_MAIN;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        getUser();
        initTopBar();
        initRecyclerView();
        initRefreshLayout();
        checkGetLocationPermission();
        getData();
        EventBus.getDefault().register(this);
    }

    private void getUser() {
        mUser = StorageManager.getUser();
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.app_name));
        mTopBarView.setImageViewLeft(TopBarView.LEFT_MENU);
        mTopBarView.setImageViewRight(R.drawable.ic_add);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                openDrawer();
            }

            @Override
            public void onRightClick() {
                AddPostActivity.start(MainActivity.this);
            }
        });
    }

    private void initRecyclerView() {
        initPeopleRecyclerView();
        initSaleRecyclerView();
        initPostRecyclerView();
    }

    private void initRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                getData();
            }
        });
    }

    private void getData() {
        getPeopleNearMe();
        getVouchers();
        getPostNearMe();
    }

    private void initPeopleRecyclerView() {
        mPeopleAdapter = new PeopleAdapter();
        mPeopleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                User item = mPeopleAdapter.getItem(position);
                if (item == null) return;
                ProfileActivity.start(MainActivity.this, item.mId);
            }
        });

        mPeopleRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mPeopleRecyclerView.setAdapter(mPeopleAdapter);
    }

    private void initSaleRecyclerView() {
        mVoucherAdapter = new VoucherAdapter();
        mVoucherAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Voucher item = mVoucherAdapter.getItem(position);
                if(item == null) return;
                VoucherDetailActivity.start(MainActivity.this, item);
            }
        });

        mSaleRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSaleRecyclerView.setAdapter(mVoucherAdapter);
    }

    private void initPostRecyclerView() {
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPostRecyclerView.setNestedScrollingEnabled(false);
    }

    private void checkGetLocationPermission() {
        if (PermissionUtils.checkPermission(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION})) {
            requestLocation();
        } else {
            PermissionUtils.requestPermission(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_GET_LOCATION);
        }
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(mLocationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(Integer.MAX_VALUE);
        locationRequest.setFastestInterval(Integer.MAX_VALUE);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location == null) {
                        return;
                    }
                    UserRequest.updateCurrentLocation(location.getLatitude(), location.getLongitude(), new RestCallback<User>() {
                        @Override
                        public void onSuccess(String message, User user) {
                            Log.d("HomeActivity", "Update current location success");
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.d("HomeActivity", "Failed to update current location");
                        }
                    });
                }
            }
        };
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, null);
    }

    private void getPeopleNearMe() {
        ServiceManager.getInstance().getUserService().getUserNearMe().enqueue(new RestCallback<List<User>>() {
            @Override
            public void onSuccess(String message, List<User> users) {
                mPeopleAdapter.setNewData(users);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getVouchers() {
        ServiceManager.getInstance().getVoucherService().getVouchersNearMe(0).enqueue(new RestCallback<List<Voucher>>() {
            @Override
            public void onSuccess(String message, List<Voucher> vouchers) {
                mVoucherAdapter.setNewData(vouchers);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPostNearMe() {
        ServiceManager.getInstance().getPostService().getPostsNearMe().enqueue(new RestCallback<List<Post>>() {
            @Override
            public void onSuccess(String message, List<Post> posts) {
                mPostRecyclerView.setNewData(posts);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED))
            return;
        if (requestCode == REQUEST_CODE_GET_LOCATION) {
            requestLocation();
        }
    }


    private class PeopleAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

        public PeopleAdapter() {
            super(R.layout.item_user_near_me_layout, new ArrayList<User>());
        }

        @Override
        protected void convert(BaseViewHolder helper, User item) {
            if(item == null) return;

            helper.setVisible(R.id.onlineImageView, item.mIsOnline);
            ImageView imageView = helper.getView(R.id.avatarImageView);
            Glide.with(mContext.getApplicationContext()).load(item.mAvatar).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_default_avatar)).into(imageView);
            helper.setText(R.id.nameTextView, item.mFullName);
            helper.setText(R.id.distanceTextView, StringUtils.convertDistanceToString(item.mDistance));
        }
    }

    private class VoucherAdapter extends BaseQuickAdapter<Voucher, BaseViewHolder> {

        public VoucherAdapter() {
            super(R.layout.item_sale_layout, new ArrayList<Voucher>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Voucher item) {
            if(item == null) return;
            ImageView imageView = helper.getView(R.id.voucherImageView);
            Glide.with(mContext.getApplicationContext()).load(item.mImage).into(imageView);
            if(HOT_DEAL.equals(item.mHost)) {
                helper.setImageResource(R.id.logoImageView, R.drawable.logo_hotdeal);
            } else {
                helper.setImageResource(R.id.logoImageView, R.drawable.logo_foody);
            }
            helper.setText(R.id.titleTextView, item.mTitle);
            helper.setText(R.id.placeTextView, item.mLocation);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePost(EventUpdatePost eventUpdatePost) {
        getPostNearMe();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateProfile(EventUpdateProfile eventUpdateProfile) {
        getPeopleNearMe();
        getPostNearMe();
    }

    @Override
    public void onBackPressed() {
        confirmExitApp();
    }
}
