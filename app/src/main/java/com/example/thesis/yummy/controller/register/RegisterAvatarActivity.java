package com.example.thesis.yummy.controller.register;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.FileUtils;
import com.example.thesis.yummy.utils.PermissionUtils;
import com.example.thesis.yummy.utils.UploadImageListener;
import com.example.thesis.yummy.utils.UploadImageUtils;
import com.example.thesis.yummy.view.dialog.SelectModeImageDialogFragment;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterAvatarActivity extends BaseActivity {

    private static final int REQUEST_CODE_TAKE_PICTURE = 1;
    private static final int REQUEST_CODE_GET_IMAGE = 2;
    private static final int REQUEST_CODE_CAMERA = 3;

    @BindView(R.id.imgAvatar) ImageView mImgAvatar;

    private File mFile;
    private Uri mImageUri;

    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterAvatarActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.btnAvatar)
    public void avatarButtonClicked() {
        if (PermissionUtils.checkPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            openDialog();
        } else {
            PermissionUtils.requestPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_CAMERA);
        }
    }

    @OnClick(R.id.btnNext)
    public void nextButtonClicked() {
        uploadImage();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_avatar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
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
        if(mImageUri == null) {
            RegisterGenderActivity.start(this);
            return;
        }

        showLoading();
        UploadImageUtils.uploadImage(mImageUri, new UploadImageListener() {
            @Override
            public void uploadSuccess(String url) {
                hideLoading();
                User user = StorageManager.getUser();
                if(user == null) return;
                user.mAvatar = url;
                StorageManager.saveUser(user);
                RegisterGenderActivity.start(RegisterAvatarActivity.this);
            }

            @Override
            public void uploadFailure(String err) {
                hideLoading();
                Toast.makeText(RegisterAvatarActivity.this, err, Toast.LENGTH_SHORT).show();
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
            case REQUEST_CODE_TAKE_PICTURE:
                if(mFile == null) return;
                Glide.with(getApplicationContext()).load(mImageUri).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
                break;
            case REQUEST_CODE_GET_IMAGE:
                if (data == null || data.getData() == null)
                    return;
                mFile = new File(FileUtils.getPath(this, data.getData()));
                mImageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", mFile);
                Glide.with(getApplicationContext()).load(mImageUri).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}
