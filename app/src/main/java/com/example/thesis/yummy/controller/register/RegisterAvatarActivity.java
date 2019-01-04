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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thesis.yummy.AppConstants;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.restful.request.UploadRequest;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.FileUtils;
import com.example.thesis.yummy.utils.PermissionUtils;
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

    private void uploadImage() {
        if(mFile == null) {
            RegisterGenderActivity.start(this);
            return;
        }

        showLoading();
        UploadRequest.uploadImage(mFile, new RestCallback<String>() {
            @Override
            public void onSuccess(String message, String s) {
                String url = AppConstants.BASE_SERVER_URL + s;
                updateAvatar(url);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(RegisterAvatarActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAvatar(String avatarUrl) {
        ServiceManager.getInstance().getUserService().updateAvatar(avatarUrl).enqueue(new RestCallback<User>() {
            @Override
            public void onSuccess(String message, User user) {
                hideLoading();
                StorageManager.saveUser(user);
                RegisterGenderActivity.start(RegisterAvatarActivity.this);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(RegisterAvatarActivity.this, message, Toast.LENGTH_SHORT).show();
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
                Glide.with(getApplicationContext()).load(mFile).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
                break;
            case REQUEST_CODE_GET_IMAGE:
                if (data == null || data.getData() == null)
                    return;
                String path = FileUtils.getPath(this, data.getData());
                if(path == null) return;
                mFile = new File(path);
                Glide.with(getApplicationContext()).load(mFile).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}
