package com.example.thesis.yummy.restful.services;


import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.restful.response.LoginResponse;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);

    @GET("me")
    Call<RestResponse<User>> getUserInfo();
}
