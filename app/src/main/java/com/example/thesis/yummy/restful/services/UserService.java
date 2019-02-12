package com.example.thesis.yummy.restful.services;


import android.support.v7.widget.CardView;

import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.Base;
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

    @FormUrlEncoded
    @POST("editUser")
    Call<RestResponse<User>> updateCharacteristic(@Field("myCharacter") String characteristic);

    @FormUrlEncoded
    @POST("editUser")
    Call<RestResponse<User>> updateFavoriteFood(@Field("favoriteFood") List<Integer> mFavoriteFood);

    @POST("editUser")
    Call<RestResponse<User>> updateAddress(@Body Map<String, Object> params);

    @POST("editUser")
    Call<RestResponse<User>> updateProfile(@Body Map<String, Object> params);

    @POST("editUser")
    Call<RestResponse<User>> updateCurrentLocation(@Body Map<String, Object> params);

    @FormUrlEncoded
    @POST("listpostuser")
    Call<RestResponse<List<Post>>> getListPostOfUser(@Field("user_id") int userId, @Field("page") int page);

    @FormUrlEncoded
    @POST("forgotPassword")
    Call<RestResponse<Base>> sendCodeForgotPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("changePass")
    Call<RestResponse<Base>> changePassword(@Field("email") String email,
                                            @Field("password") String password);

    @POST("search/{page}")
    Call<RestResponse<List<User>>> searchUser(@Path("page") int pageNumber,
                                              @Body Map<String, Object> params);

    @POST("invite")
    Call<RestResponse<Base>> sendRequest(@Body Map<String, Object> params);

    @FormUrlEncoded
    @POST("acceptInvite")
    Call<RestResponse<Base>> acceptRequest(@Field("request") int requestId);

    @FormUrlEncoded
    @POST("rejectRequest")
    Call<RestResponse<Base>> rejectRequest(@Field("request") int requestId);

    @GET("list_user_near")
    Call<RestResponse<List<User>>> getUserNearMe();

    @POST("update_pass")
    Call<RestResponse<Base>> changePass(@Body Map<String, Object> params);

}
