package com.example.thesis.yummy.controller.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.eventbus.EventUpdateProfile;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.restful.request.UserRequest;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.FileUtils;
import com.example.thesis.yummy.utils.PermissionUtils;
import com.example.thesis.yummy.utils.UploadImageListener;
import com.example.thesis.yummy.utils.UploadImageUtils;
import com.example.thesis.yummy.view.TopBarView;
import com.example.thesis.yummy.view.dialog.SelectModeImageDialogFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends BaseActivity {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_TAKE_PICTURE = 3;
    private static final int REQUEST_CODE_GET_IMAGE = 4;
    private static final int REQUEST_CODE_CAMERA = 5;

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.imgAvatar) ImageView mImgAvatar;
    @BindView(R.id.edtEmail) EditText mEdtEmail;
    @BindView(R.id.edtName) EditText mEdtName;
    @BindView(R.id.edtDateOfBirth) EditText mEdtDateOfBirth;
    @BindView(R.id.edtAddress) EditText mEdtAddress;
    @BindView(R.id.genderSpinner) Spinner mGenderSpinner;
    @BindView(R.id.edtPassword_edit) EditText edtPassword;
    @BindView(R.id.btnEditPassWord) ImageView btnEditPass;
    @BindView(R.id.edtNewPass_edit) EditText edtNewPass;
    @BindView(R.id.edtConfirmNewPass_edit) EditText edtConfirmNewPass;
    @BindView(R.id.lnNewPass) LinearLayout lnNewPass;
    @BindView(R.id.scrollView) ScrollView mScrollView;

    private ArrayAdapter<GenderItem> mGenderAdapter;
    private Date mBirthday;
    private Location mLocation = new Location("");
    private String mImageUrl;
    private File mFile;
    private Uri mImageUri;

    public static void start(Context context) {
        Intent starter = new Intent(context, EditProfileActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.avatarImageButton)
    public void cameraButtonClicked() {
        if (PermissionUtils.checkPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            openDialog();
        } else {
            PermissionUtils.requestPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_CAMERA);
        }
    }

    @OnClick(R.id.btnBirthday)
    public void chooseDateOfBirth() {
        showDatePickerDialog();
    }

    @OnClick(R.id.btnAddress)
    public void chooseAddress() {
        openSearchLocation();
    }

    @OnClick(R.id.btnEditPassWord)
    public void editPassWord(){
        if(lnNewPass.getVisibility() == View.GONE){
            lnNewPass.setVisibility(View.VISIBLE);
        }else {
            lnNewPass.setVisibility(View.GONE);
            edtPassword.setText("");
            edtNewPass.setText("");
            edtConfirmNewPass.setText("");
        }
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initTopBar();
        initSpinner();
        initData();
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.edit_profile));
        mTopBarView.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBarView.setImageViewRight(R.drawable.ic_check_white);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                showLoading();
                if(mImageUri != null) {
                    uploadImage();
                } else {
                    updateProfile();
                }
            }
        });
    }

    private void initSpinner() {
        mGenderAdapter = new ArrayAdapter<GenderItem>(this, android.R.layout.simple_spinner_dropdown_item);
        mGenderAdapter.add(new GenderItem(0, getString(R.string.female)));
        mGenderAdapter.add(new GenderItem(1, getString(R.string.male)));

        mGenderSpinner.setAdapter(mGenderAdapter);
    }

    private void initData() {
        User user = StorageManager.getUser();
        if(user == null) return;

        mImageUrl = user.mAvatar;
        Glide.with(getApplicationContext()).load(user.mAvatar).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
        mEdtEmail.setText(user.mEmail);
        mEdtName.setText(user.mFullName);

        if(user.mBirthDay != null) {
            mBirthday = user.mBirthDay;
            mEdtDateOfBirth.setText(DateFormat.format("dd-MM-yyyy", user.mBirthDay));
        }
        mEdtAddress.setText(user.mAddress);

        if(user.mLatLngAddress != null && user.mLatLngAddress.mCoordinates != null) {
            mLocation.setLatitude(user.mLatLngAddress.mCoordinates.get(0));
            mLocation.setLongitude(user.mLatLngAddress.mCoordinates.get(1));
        }

        if(user.mGender != null) {
            mGenderSpinner.setSelection(user.mGender);
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        if(mBirthday != null) {
            calendar.setTime(mBirthday);
        }


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar date = Calendar.getInstance();
                        date.set(year, monthOfYear, dayOfMonth);
                        mBirthday = date.getTime();
                        mEdtDateOfBirth.setText(DateFormat.format("dd-MM-yyyy", mBirthday));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
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

    private void updateProfile() {
        boolean isChangePass = false;

        if(TextUtils.isEmpty(mEdtName.getText())) {
            Toast.makeText(this, getString(R.string.enter_full_name), Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mEdtDateOfBirth.getText())) {
            Toast.makeText(this, R.string.choose_date_of_birth, Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mEdtAddress.getText())) {
            Toast.makeText(this, R.string.enter_address, Toast.LENGTH_SHORT).show();
            return;
        }

        if(lnNewPass.getVisibility() == View.VISIBLE) {
            if(TextUtils.isEmpty(edtPassword.getText())) {
                Toast.makeText(this, R.string.enter_current_password, Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(edtNewPass.getText())) {
                Toast.makeText(this, R.string.enter_new_password, Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(edtConfirmNewPass.getText())) {
                Toast.makeText(this, R.string.enter_confirm_password, Toast.LENGTH_SHORT).show();
                return;
            }
            if(!edtNewPass.getText().toString().equals(edtConfirmNewPass.getText().toString())) {
                Toast.makeText(this, R.string.wrong_confirm_password, Toast.LENGTH_SHORT).show();
                return;
            }
            isChangePass = true;
        }


        showLoading();
        if(isChangePass) {
            changePass();
        } else {
            updateUser();
        }
    }

    private void openDialog() {
        SelectModeImageDialogFragment selectModeImageDialogFragment = new SelectModeImageDialogFragment();
        selectModeImageDialogFragment.setPostArticleEditListener(new SelectModeImageDialogFragment.SelectModeImageListener() {
            @Override
            public void callCamera() {
                openCamera();
            }

            @Override
            public void callGallery() {
                openLibrary();
            }
        });
        selectModeImageDialogFragment.show(getSupportFragmentManager(), this.getClass().getName());
    }

    private void openCamera() {
        try {
            mFile = FileUtils.createImageFile();
            mImageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", mFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void openLibrary() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_GET_IMAGE);
    }

    private void uploadImage() {
        UploadImageUtils.uploadImage(mImageUri, new UploadImageListener() {
            @Override
            public void uploadSuccess(String url) {
                mImageUrl = url;
                updateProfile();
            }

            @Override
            public void uploadFailure(String err) {
                hideLoading();
                Toast.makeText(EditProfileActivity.this, err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePass() {
        UserRequest.changePass(edtPassword.getText().toString(), edtNewPass.getText().toString(), new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                updateUser();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser() {
        int gender = ((GenderItem) mGenderSpinner.getSelectedItem()).mKey;
        UserRequest.updateProfile(mImageUrl, mEdtName.getText().toString(),
                gender, mBirthday, mEdtAddress.getText().toString(),
                mLocation.getLatitude(), mLocation.getLongitude(), new RestCallback<User>() {
                    @Override
                    public void onSuccess(String message, User user) {
                        hideLoading();
                        StorageManager.saveUser(user);
                        EventBus.getDefault().post(new EventUpdateProfile());
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        hideLoading();
                        Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
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
                    mEdtAddress.setText(place.getName().toString());
                    mLocation = new Location("");
                    mLocation.setLatitude(place.getLatLng().latitude);
                    mLocation.setLongitude(place.getLatLng().longitude);
                }
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                if(mFile == null) return;
                Glide.with(getApplicationContext()).load(mImageUri).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
                break;
            case REQUEST_CODE_GET_IMAGE:
                if (data == null || data.getData() == null)
                    return;
                String path = FileUtils.getPath(this, data.getData());
                if(path == null) return;
                mFile = new File(path);
                mImageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", mFile);
                Glide.with(getApplicationContext()).load(mImageUri).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
                break;
        }
    }
}
