package com.example.thesis.yummy.controller.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.restful.request.UserRequest;
import com.example.thesis.yummy.view.TopBarView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendRequestActivity extends BaseActivity {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final String ARG_KEY_USER = "ARG_KEY_USER";

    public static void start(Context context, User user) {
        Intent starter = new Intent(context, SendRequestActivity.class);
        starter.putExtra(ARG_KEY_USER, user);
        context.startActivity(starter);
    }

    @BindView(R.id.avatarImageView) ImageView mAvatarImageView;
    @BindView(R.id.nameTextView) TextView mNameTextView;
    @BindView(R.id.placeEditText) EditText mPlaceEditText;
    @BindView(R.id.timeEditText) EditText mTimeEditText;
    @BindView(R.id.contentEditText) EditText mContentEditText;
    @BindView(R.id.topBar) TopBarView mTopBarView;

    private Date mTime;
    private User mUser;
    private Location mLocation = new Location("");

    @OnClick(R.id.placeButton)
    public void placeButtonClicked() {
        openSearchLocation();
    }

    @OnClick(R.id.timeButton)
    public void timeButtonClicked() {
        showDateTimePickerDialog();
    }

    @OnClick(R.id.sendRequestButton)
    public void sendRequestButtonClicked() {
        if(TextUtils.isEmpty(mPlaceEditText.getText())){
            Toast.makeText(this, R.string.choose_place, Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mTimeEditText.getText())) {
            Toast.makeText(this, R.string.choose_time, Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mContentEditText.getText())) {
            Toast.makeText(this, R.string.enter_content, Toast.LENGTH_SHORT).show();
            return;
        }
        sendRequest();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.send_request_activity;
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
    }

    private void getExtras() {
        mUser = (User) getIntent().getSerializableExtra(ARG_KEY_USER);
        if(mUser == null) return;

        Glide.with(getApplicationContext()).load(mUser.mAvatar).apply(RequestOptions.circleCropTransform()).into(mAvatarImageView);
        mNameTextView.setText(mUser.mFullName);
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.send_request));
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

    private void openSearchLocation() {
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

    private void showDateTimePickerDialog() {
        if(mTime == null) {
            mTime = Calendar.getInstance().getTime();
        }
        SwitchDateTimeDialogFragment dialogFragment = SwitchDateTimeDialogFragment.newInstance(
                "Chọn thời gian",
                "OK",
                "Hủy"
        );

        dialogFragment.startAtTimeView();
        dialogFragment.setHighlightAMPMSelection(true);
        dialogFragment.setDefaultDateTime(mTime);
        dialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                setTime(date);
            }

            @Override
            public void onNegativeButtonClick(Date date) {

            }
        });

        dialogFragment.show(getSupportFragmentManager(), "dateTimePicker");
    }

    private void setTime(Date date) {
        mTime = date;
        mTimeEditText.setText(DateFormat.format("dd/MM/yyyy hh:mm", mTime));
    }

    private void sendRequest() {
        showLoading();
        UserRequest.sendRequest(mUser.mId, mContentEditText.getText().toString(), mLocation.getLatitude(), mLocation.getLongitude(),
                mPlaceEditText.getText().toString(), mTime, new RestCallback<Base>() {
                    @Override
                    public void onSuccess(String message, Base base) {
                        hideLoading();
                        Toast.makeText(SendRequestActivity.this, R.string.send_request_success, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        hideLoading();
                        Toast.makeText(SendRequestActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                final Place place = PlacePicker.getPlace(this, data);

                if (place != null) {
                    mPlaceEditText.setText(place.getName().toString());
                    mLocation = new Location("");
                    mLocation.setLatitude(place.getLatLng().latitude);
                    mLocation.setLongitude(place.getLatLng().longitude);
                }
                break;
        }
    }
}
