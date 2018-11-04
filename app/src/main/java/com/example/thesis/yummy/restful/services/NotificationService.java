package com.example.thesis.yummy.restful.services;

import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NotificationService {

    @GET("notification/{user_id}")
    Call<RestResponse<List<Notification>>> getNotifications(@Path("user_id") int userId);
}
