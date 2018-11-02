package com.example.thesis.yummy.restful.services;

import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Rating;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RatingService {

    @GET("user/{userId}/list_rating")
    Call<RestResponse<List<Rating>>> getListRatingProfile(@Path("userId") int id);

    @FormUrlEncoded
    @POST("user/is_had_rating")
    Call<RestResponse<Base>> checkRatingPeople(@Field("people_evaluate") int id);

    @FormUrlEncoded
    @POST("rate/rating_people")
    Call<RestResponse<Rating>> createRatingProfile(@Field("content") String content,
                                                   @Field("point") Integer point,
                                                   @Field("people_evaluate") Integer peopleEvaluate);
}
