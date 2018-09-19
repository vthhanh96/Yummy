package com.example.thesis.yummy.restful.services;

import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.Post;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostService {
    @GET("post")
    Call<RestResponse<List<Post>>> getAllPost(
            @Query("page") int pageNumber
    );

    @POST("post/{postId}/interested")
    Call<RestResponse<Post>> interested(@Path("postId") int postId);


    @POST("post")
    Call<RestResponse<Post>> createPost(@Body Map<String, Object> params);

    @GET("post/{postId}")
    Call<RestResponse<Post>> getPostDetail(@Path("postId") int postId);
}
