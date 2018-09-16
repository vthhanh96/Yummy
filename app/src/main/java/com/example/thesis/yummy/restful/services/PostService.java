package com.example.thesis.yummy.restful.services;

import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PostService {
    @GET("post")
    Call<RestResponse<List<Post>>> getAllPost(
            @Query("page") int pageNumber
    );
}
