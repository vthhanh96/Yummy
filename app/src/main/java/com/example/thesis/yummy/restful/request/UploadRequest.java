package com.example.thesis.yummy.restful.request;

import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadRequest {

    public static void uploadImage(File file, RestCallback<String> callback) {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), reqFile);

        ServiceManager.getInstance().getUploadService().upload(body).enqueue(callback);
    }
}
