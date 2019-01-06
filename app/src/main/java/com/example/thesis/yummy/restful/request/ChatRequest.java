package com.example.thesis.yummy.restful.request;

import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Message;

import java.util.HashMap;
import java.util.Map;

public class ChatRequest {
    public static void createChatMessage(int userChatId, String message, RestCallback<Message> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("to", userChatId);
        params.put("message", message);

        ServiceManager.getInstance().getChatService().createChatMessage(params).enqueue(callback);
    }
}
