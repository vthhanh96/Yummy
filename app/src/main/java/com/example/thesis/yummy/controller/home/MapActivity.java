package com.example.thesis.yummy.controller.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.view.TopBarView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends BaseActivity {

    private static final String ARG_KEY_LATITUDE = "ARG_KEY_LATITUDE";
    private static final String ARG_KEY_LONGITUDE = "ARG_KEY_LONGITUDE";
    private static final String ARG_KEY_PLACE = "ARG_KEY_PLACE";

    @BindView(R.id.topBar) TopBarView mTopBarView;

    private Float mLat;
    private Float mLng;
    private String mPlace;

    public static void start(Context context, Float lat, Float lng, String place) {
        Intent starter = new Intent(context, MapActivity.class);
        starter.putExtra(ARG_KEY_LATITUDE, lat);
        starter.putExtra(ARG_KEY_LONGITUDE, lng);
        starter.putExtra(ARG_KEY_PLACE, place);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map;
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
        initMap();
    }

    private void getExtras() {
        mLat = getIntent().getFloatExtra(ARG_KEY_LATITUDE, 0);
        mLng = getIntent().getFloatExtra(ARG_KEY_LONGITUDE, 0);
        mPlace = getIntent().getStringExtra(ARG_KEY_PLACE);
    }

    private void initTopBar() {
        mTopBarView.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBarView.setTitle(getString(R.string.place));
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

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latLng = new LatLng(mLat, mLng);
                googleMap.addMarker(new MarkerOptions().position(latLng).title(mPlace));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            }
        });
    }
}
