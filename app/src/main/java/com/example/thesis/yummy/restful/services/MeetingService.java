package com.example.thesis.yummy.restful.services;

import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.restful.model.Meeting;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MeetingService {

    @FormUrlEncoded
    @POST("meeting/create_meeting")
    Call<RestResponse<Meeting>> createMeeting(@Field("postId") int postId, @Field("joined_people")List<Integer> mJoinedPeople);

    @GET("meeting")
    Call<RestResponse<List<Meeting>>> getMeetings();

    @GET("meeting/{meetingId}/list_comment")
    Call<RestResponse<List<Comment>>> getMeetingComments(@Path("meetingId") int meetingId);

    @FormUrlEncoded
    @POST("meeting/{meetingId}/add_comment")
    Call<RestResponse<Base>> createMeetingComment(@Path("meetingId") int meetingId,
                                                  @Field("content") String content);

    @GET("meeting/{meetingId}")
    Call<RestResponse<Meeting>> getMeetingDetail(@Path("meetingId") int meetingId);
}
