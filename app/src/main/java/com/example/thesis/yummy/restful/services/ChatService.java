package com.example.thesis.yummy.restful.services;

import com.example.thesis.yummy.restful.RestResponse;
import com.example.thesis.yummy.restful.model.Conversation;
import com.example.thesis.yummy.restful.model.Message;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {

    @GET("chat/{page}")
    Call<RestResponse<List<Conversation>>> getConversationHistory(@Path("page") int pageNumber);

    @GET("chat/list_chat/{user_chat}/{page}")
    Call<RestResponse<List<Message>>> getChat(@Path("user_chat") int userChatId,
                                              @Path("page") int pageNumber);

    @POST("chat/create_new_chat")
    Call<RestResponse<Message>> createChatMessage(@Body Map<String, Object> params);
}
