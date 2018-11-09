package com.example.thesis.yummy.restful.request;

import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;

import java.util.HashMap;
import java.util.Map;

public class MeetingRequest {
    public static void createComment(int meetingID, String content, RestCallback<Base> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("content", content);

        ServiceManager.getInstance().getMeetingService().createMeetingComment(meetingID, params).enqueue(callback);
    }
}
