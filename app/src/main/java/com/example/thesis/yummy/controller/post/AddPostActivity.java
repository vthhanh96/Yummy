package com.example.thesis.yummy.controller.post;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.AppConstants;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.eventbus.EventInterestedPost;
import com.example.thesis.yummy.eventbus.EventUpdatePost;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.model.Category;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.restful.request.PostRequest;
import com.example.thesis.yummy.restful.request.UploadRequest;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.FileUtils;
import com.example.thesis.yummy.utils.PermissionUtils;
import com.example.thesis.yummy.utils.UploadImageListener;
import com.example.thesis.yummy.utils.UploadImageUtils;
import com.example.thesis.yummy.view.LinkPreviewLayout;
import com.example.thesis.yummy.view.TopBarView;
import com.example.thesis.yummy.view.dialog.InputDialog;
import com.example.thesis.yummy.view.dialog.SelectModeImageDialogFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

public class AddPostActivity extends BaseActivity {

    private static final String ARG_KEY_SELECTED_CATEGORIES = "ARG_KEY_SELECTED_CATEGORIES";
    private static final String ARG_KEY_LINK_URL = "ARG_KEY_LINK_URL";

    private static final int REQUEST_CODE_SELECT_CATEGORY = 1;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private static final int REQUEST_CODE_TAKE_PICTURE = 3;
    private static final int REQUEST_CODE_GET_IMAGE = 4;
    private static final int REQUEST_CODE_CAMERA = 5;

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.imgAvatar) ImageView mImgAvatar;
    @BindView(R.id.tvName) TextView mTvName;
    @BindView(R.id.rcvCategories) RecyclerView mCategoriesRecyclerView;
    @BindView(R.id.loPlace) LinearLayout mPlaceLayout;
    @BindView(R.id.tvPlace) TextView mTvPlace;
    @BindView(R.id.loAmount) LinearLayout mAmountLayout;
    @BindView(R.id.tvAmount) TextView mTvAmount;
    @BindView(R.id.loTime) LinearLayout mTimeLayout;
    @BindView(R.id.tvTime) TextView mTvTime;
    @BindView(R.id.edtContent) EditText mEdtContent;
    @BindView(R.id.imgPost) ImageView mImgPost;
    @BindView(R.id.imageLayout) FrameLayout mImageLayout;
    @BindView(R.id.linkPreviewLayout) LinkPreviewLayout mLinkPreviewLayout;

    private User mUser;
    private List<Category> mSelectedCategories;
    private CategoryAdapter mCategoryAdapter;
    private Location mLocation = new Location("");
    private File mFile;
    private Date mTime;
    private int mAmount = 0;
    private String mImageUrl;
    private String mLinkUrl;

    public static void start(Context context) {
        Intent starter = new Intent(context, AddPostActivity.class);
        context.startActivity(starter);
    }

    public static void start(Context context, String linkUrl) {
        Intent starter = new Intent(context, AddPostActivity.class);
        starter.putExtra(ARG_KEY_LINK_URL, linkUrl);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_post;
    }

    @OnClick(R.id.btnCamera)
    public void cameraButtonClicked() {
        if (PermissionUtils.checkPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            openDialog();
        } else {
            PermissionUtils.requestPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_CAMERA);
        }
    }

    @OnClick(R.id.btnCategory)
    public void chooseCategory() {
        CategoryActivity.startForResult(this, mSelectedCategories, REQUEST_CODE_SELECT_CATEGORY);
    }

    @OnClick(R.id.btnAmount)
    public void chooseAmount() {
        showNumberPickerDialog();
    }

    @OnClick(R.id.btnLocation)
    public void chooseLocation() {
        openSearchActivity();
    }

    @OnClick(R.id.btnTime)
    public void chooseTime() {
        showDateTimePickerDialog();
    }

    @OnClick(R.id.btnDeleteImage)
    public void deleteImage() {
        mFile = null;
        mImageLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.btnAttachLink)
    public void attachLink() {
        showEnterLinkDialog();
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
        initData();
        initCategoryRecyclerView();
    }

    private void getExtras() {
        mLinkUrl = getIntent().getStringExtra(ARG_KEY_LINK_URL);
        if(mLinkUrl != null) {
            showLinkPreview(mLinkUrl);
        }
    }

    private void initData() {
        mUser = StorageManager.getUser();
        if(mUser == null) return;
        if(TextUtils.isEmpty(mUser.mAvatar)) {
            mImgAvatar.setImageResource(R.drawable.ic_default_avatar);
        } else {
            Glide.with(getApplicationContext()).load(mUser.mAvatar).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
        }
        mTvName.setText(mUser.mFullName);
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.new_post));
        mTopBarView.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBarView.setImageViewRight(R.drawable.ic_check_white);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                if(isValid()) {
                    showLoading();
                    if (mFile != null) {
                        uploadImage();
                    } else {
                        addPost();
                    }
                }
            }
        });
    }

    private void initCategoryRecyclerView() {
        mCategoryAdapter = new CategoryAdapter();

        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);

        mCategoriesRecyclerView.setAdapter(mCategoryAdapter);
        mCategoriesRecyclerView.setLayoutManager(flowLayoutManager);

    }

    private void updateCategory() {
        if(mSelectedCategories == null || mSelectedCategories.isEmpty()) {
            mCategoryAdapter.setNewData(new ArrayList<Category>());
            mCategoriesRecyclerView.setVisibility(View.GONE);
            return;
        }

        mCategoriesRecyclerView.setVisibility(View.VISIBLE);
        mCategoryAdapter.setNewData(mSelectedCategories);
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
            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", mFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
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

    private void updateImages() {
        Glide.with(getApplicationContext()).load(mFile).into(mImgPost);
        mImageLayout.setVisibility(View.VISIBLE);
    }

    private void showNumberPickerDialog() {
        final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(this)
                .minValue(1)
                .maxValue(100)
                .defaultValue(mAmount == 0 ? 2 : mAmount)
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(Color.BLACK)
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        new AlertDialog.Builder(this)
                .setTitle("Chọn số lượng người tham gia")
                .setView(numberPicker)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAmount = numberPicker.getValue();
                        mAmountLayout.setVisibility(View.VISIBLE);
                        mTvAmount.setText(String.valueOf(numberPicker.getValue()));
                    }
                })
                .show();
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
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
            String startDate = simpleDateFormat.format(Calendar.getInstance().getTime());
            Date minDate = simpleDateFormat.parse(startDate);
            dialogFragment.setMinimumDateTime(minDate);
            dialogFragment.setDefaultDateTime(mTime);
        } catch (Exception ex){
            ex.printStackTrace();
        }
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
        mTvTime.setText(DateFormat.format("dd/MM/yyyy hh:mm aa", mTime));
        mTimeLayout.setVisibility(View.VISIBLE);
    }

    private void uploadImage() {
        UploadRequest.uploadImage(mFile, new RestCallback<String>() {
            @Override
            public void onSuccess(String message, String s) {
                mImageUrl = AppConstants.BASE_SERVER_URL + s;
                addPost();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(AddPostActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEnterLinkDialog() {
        InputDialog inputDialog = new InputDialog();
        inputDialog.setTitle(getString(R.string.enter_link));
        inputDialog.setContentInput(mLinkUrl);
        inputDialog.setListener(new InputDialog.InputDialogListener() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onDoneClick(String content) {
                showLinkPreview(content);
            }
        });
        inputDialog.show(getSupportFragmentManager(), "Input Dialog");
    }

    private void showLinkPreview(String link) {
        RichPreview richPreview = new RichPreview(new ResponseListener() {
            @Override
            public void onData(MetaData metaData) {
                if(metaData == null) {
                    Toast.makeText(AddPostActivity.this, R.string.wrong_link, Toast.LENGTH_SHORT).show();
                    return;
                }

                mLinkUrl = metaData.getUrl();
                mLinkPreviewLayout.setVisibility(View.VISIBLE);
                mLinkPreviewLayout.setMetaData(metaData);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AddPostActivity.this, R.string.wrong_link, Toast.LENGTH_SHORT).show();
                mLinkUrl = null;
                mLinkPreviewLayout.setVisibility(View.GONE);
            }
        });

        if(URLUtil.isValidUrl(link)) {
            richPreview.getPreview(link);
        } else {
            Toast.makeText(AddPostActivity.this, R.string.wrong_link, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValid() {
        if(mAmountLayout.getVisibility() == View.GONE) {
            Toast.makeText(this, R.string.enter_amount_of_people, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mPlaceLayout.getVisibility() == View.GONE) {
            Toast.makeText(this, R.string.enter_place, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mTimeLayout.getVisibility() == View.GONE) {
            Toast.makeText(this, R.string.enter_time, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void addPost() {
        PostRequest.createPost(mEdtContent.getText().toString(),
                mLocation.getLatitude(),
                mLocation.getLongitude(),
                mTvPlace.getText().toString(),
                mCategoryAdapter.getData(),
                mTime, mAmount, mImageUrl, mLinkUrl, new RestCallback<Post>() {
                    @Override
                    public void onSuccess(String message, Post post) {
                        EventBus.getDefault().post(new EventUpdatePost());
                        hideLoading();
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        hideLoading();
                        Toast.makeText(AddPostActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED))
            return;
        if (requestCode == REQUEST_CODE_CAMERA) {
            openDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_CODE_SELECT_CATEGORY:
                if(data == null) return;
                try {
                    String selectedCategories = data.getStringExtra(ARG_KEY_SELECTED_CATEGORIES);
                    List<Category> selected = Arrays.asList(new Gson().fromJson(selectedCategories, Category[].class));
                    mSelectedCategories = new ArrayList<>();
                    mSelectedCategories.addAll(selected);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mSelectedCategories = new ArrayList<>();
                }
                updateCategory();
                break;
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                final Place place = PlacePicker.getPlace(this, data);

                if (place != null) {
                    mPlaceLayout.setVisibility(View.VISIBLE);
                    mTvPlace.setText(place.getName().toString());
                    mLocation = new Location("");
                    mLocation.setLatitude(place.getLatLng().latitude);
                    mLocation.setLongitude(place.getLatLng().longitude);
                }
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                if(mFile == null) return;
                updateImages();
                break;
            case REQUEST_CODE_GET_IMAGE:
                if (data == null || data.getData() == null)
                    return;
                String path = FileUtils.getPath(this, data.getData());
                if(path == null) return;
                mFile = new File(path);
                updateImages();
                break;
        }
    }

    private class CategoryAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {

        public CategoryAdapter() {
            super(R.layout.layout_category_item, new ArrayList<Category>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Category item) {
            helper.setText(R.id.txtCategoryName, item.mName);
        }
    }
}
