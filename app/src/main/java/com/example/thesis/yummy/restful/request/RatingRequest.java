package com.example.thesis.yummy.restful.request;

import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;

import java.util.HashMap;
import java.util.Map;

public class RatingRequest {

    public static void checkRatingPeople(int id, RestCallback<Base> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("people_evaluate", id);
        ServiceManager.getInstance().getRatingService().checkRatingPeople(params).enqueue(callback);
    }
}
