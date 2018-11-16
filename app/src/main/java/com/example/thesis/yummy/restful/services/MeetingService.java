package com.example.thesis.yummy.restful.services;

import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.restful.model.Meeting;
import com.example.thesis.yummy.restful.model.Rating;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MeetingService {

    @FormUrlEncoded
    @POST("meeting/create_meeting")
    Call<RestResponse<Meeting>> createMeeting(@Field("postId") int postId, @Field("joined_people")List<Integer> mJoinedPeople);

    @FormUrlEncoded
    @POST("meeting/list_status")
    Call<RestResponse<List<Meeting>>> getMeetings(@Field("status") boolean status);

    @GET("meeting/{meetingId}/list_comment")
    Call<RestResponse<List<Comment>>> getMeetingComments(@Path("meetingId") int meetingId);

    @Headers({"Content-Type: application/json"})
    @POST("meeting/{meetingId}/add_comment")
    Call<RestResponse<Base>> createMeetingComment(@Path("meetingId") int meetingId,
                                                  @Body Map<String, String> params);

    @GET("meeting/{meetingId}")
    Call<RestResponse<Meeting>> getMeetingDetail(@Path("meetingId") int meetingId);

    @GET("meeting/{meeting_id}/list_rating/{user_id}")
    Call<RestResponse<List<Rating>>> getMeetingRating(@Path("meeting_id") int meetingId,
                                                      @Path("user_id") int userId);

    @FormUrlEncoded
    @PUT("meeting/update_comment/{commentId}")
    Call<RestResponse<Comment>> updateComment(@Path("commentId") int commentId,
                                              @Field("content") String content);

    @DELETE("meeting/comment/{commentId}")
    Call<RestResponse<Base>> deleteComment(@Path("commentId") int commentId);
}
