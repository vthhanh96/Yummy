package com.example.thesis.yummy.controller.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.controller.post.AddPostActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.restful.request.UserRequest;
import com.example.thesis.yummy.socket.SocketManager;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.PermissionUtils;
import com.example.thesis.yummy.view.PostRecyclerView;
import com.example.thesis.yummy.view.TopBarView;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_HOME_PAGE;
import static com.example.thesis.yummy.AppConstants.SOCKET_BASE_URL;

public class HomeActivity extends DrawerActivity {

    private static final int REQUEST_CODE_GET_LOCATION = 10;

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.tabLayout) TabLayout mTabLayout;
    @BindView(R.id.viewPager) ViewPager mViewPager;

    private LocationCallback mLocationCallback;

    public static void start(Context context) {
        Intent starter = new Intent(context, HomeActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    protected int getNavId() {
        return NAV_DRAWER_ID_HOME_PAGE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        initTopBar();
        initViewPager();
        initTabLayout();
        checkGetLocationPermission();
    }

    private void initTopBar() {
        mTopBarView.setImageViewLeft(TopBarView.LEFT_MENU);
        mTopBarView.setTitle(getString(R.string.app_name));
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                openDrawer();
            }

            @Override
            public void onRightClick() {

            }
        });
    }

    private void initViewPager() {
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.mFragments.add(ListPostFragment.newInstance(false));
        pagerAdapter.mFragments.add(ListPostFragment.newInstance(true));

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(pagerAdapter.mFragments.size());
    }

    private void initTabLayout() {
        mTabLayout.setupWithViewPager(mViewPager);
        String[] tabNames = new String[] {"Bài viết gần đây", "Bài viết đã đăng ký"};
        for (int i = 0; i < tabNames.length; i ++) {
            if(mTabLayout.getTabAt(i) != null) {
                mTabLayout.getTabAt(i).setText(tabNames[i]);
            }
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED))
            return;
        if (requestCode == REQUEST_CODE_GET_LOCATION) {
            requestLocation();
        }
    }

    private class PagerAdapter extends FragmentPagerAdapter{

        List<Fragment> mFragments = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
