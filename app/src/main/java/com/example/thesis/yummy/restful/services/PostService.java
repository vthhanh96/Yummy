package com.example.thesis.yummy.restful.services;

import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @PUT("post/{postId}")
    Call<RestResponse<Post>> updatePost(@Path("postId") int postId, @Body Map<String, Object> params);

    @GET("post/{postId}")
    Call<RestResponse<Post>> getPostDetail(@Path("postId") int postId);

    @DELETE("post/{postId}")
    Call<RestResponse<Base>> deletePost(@Path("postId") int postId);

    @FormUrlEncoded
    @POST("post/{postId}/comment")
    Call<RestResponse<Comment>> createComment(@Path("postId") int postId, @Field("mContent") String content);

    @PUT("post/{postId}/comment/{commentId}")
    Call<RestResponse<Comment>> editComment(@Path("postId") int postId,
                                            @Path("commentId") int commentId,
                                            @Field("mContent") String content);

    @DELETE("post/{postId}/comment/{commentId}")
    Call<RestResponse<Base>> deleteComment(@Path("postId") int postId, @Path("commentId") int commentId);

    @GET("post/{postId}/interested_list")
    Call<RestResponse<List<User>>> getInterestedPeople(@Path("postId") int postId);
}
