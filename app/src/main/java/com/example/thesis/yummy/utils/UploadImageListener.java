package com.example.thesis.yummy.utils;

public interface UploadImageListener {
    void uploadSuccess(String url);
    void uploadFailure(String err);
}
