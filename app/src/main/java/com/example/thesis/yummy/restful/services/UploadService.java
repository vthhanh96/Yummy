package com.example.thesis.yummy.restful.services;

import com.example.thesis.yummy.restful.RestResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadService {

    @Multipart
    @POST("image/upload")
    Call<RestResponse<String>> upload(@Part MultipartBody.Part file);
}

