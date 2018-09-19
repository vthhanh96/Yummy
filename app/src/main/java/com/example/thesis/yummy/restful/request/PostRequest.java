package com.example.thesis.yummy.restful.request;

import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Category;
import com.example.thesis.yummy.restful.model.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PostRequest {

    public static void createPost(String content, double latitude, double longitude, String place,
                                  List<Category> categories, Date time, int amount, RestCallback<Post> callback) {
        List<Integer> categoriesId = new ArrayList<>();
        for (Category category : categories) {
            categoriesId.add(category.mId);
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("content", content);
        params.put("place", place);
        params.put("categories", categoriesId);
        params.put("time", time);
        params.put("amount", amount);

        HashMap<String, Object> coordinate = new HashMap<>();
        coordinate.put("coordinates", new Double[]{longitude, latitude});

        params.put("location", coordinate);

        ServiceManager.getInstance().getPostService().createPost(params).enqueue(callback);
    }
}
