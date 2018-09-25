package com.example.thesis.yummy.controller.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterAddressActivity extends BaseActivity {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.txtAddress) TextView mTxtAddress;

    private Location mLocation = new Location("");

    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterAddressActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.addressLayout)
    public void searchAddress() {
        openSearchActivity();
    }

    @OnClick(R.id.btnNext)
    public void nextButtonClicked() {
        RegisterCharacteristicActivity.start(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_address;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initToolbar();
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void openSearchActivity() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(new LatLngBounds(
                                    new LatLng(8.407168163601074, 104.1448974609375),
                                    new LatLng(10.7723923007117563, 106.6981029510498)))
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                final Place place = PlacePicker.getPlace(data, this);

                if (place != null) {
                    mTxtAddress.setText(place.getName().toString());
                    mLocation = new Location("");
                    mLocation.setLatitude(place.getLatLng().latitude);
                    mLocation.setLongitude(place.getLatLng().longitude);
                }
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
