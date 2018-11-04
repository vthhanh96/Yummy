package com.example.thesis.yummy.restful.services;


import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.Location;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.restful.response.LoginResponse;


import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);

    @GET("{user_id}")
    Call<RestResponse<User>> getUserInfo(@Path("user_id") int userId);

    @FormUrlEncoded
    @POST("register")
    Call<LoginResponse> register(@Field("email") String email, @Field("fullName") String fullName, @Field("password") String password);

    @FormUrlEncoded
    @POST("editUser")
    Call<RestResponse<User>> updateAvatar(@Field("avatar") String avatarUrl);

    @FormUrlEncoded
    @POST("editUser")
    Call<RestResponse<User>> updateGender(@Field("gender") int gender);

    @FormUrlEncoded
    @POST("editUser")
    Call<RestResponse<User>> updateBirthday(@Field("birthday") Date birthDay);

    @POST("editUser")
    Call<RestResponse<User>> updateAddress(@Body Map<String, Object> params);

    @POST("editUser")
    Call<RestResponse<User>> updateProfile(@Body Map<String, Object> params);

    @FormUrlEncoded
    @POST("listpostuser")
    Call<RestResponse<List<Post>>> getListPostOfUser(@Field("user_id") int userId, @Field("page") int page);
}
