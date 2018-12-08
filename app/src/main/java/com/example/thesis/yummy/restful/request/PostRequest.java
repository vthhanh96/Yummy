package com.example.thesis.yummy.restful.request;

import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Category;
import com.example.thesis.yummy.restful.model.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostRequest {

    public static void createPost(String content, double latitude, double longitude, String place,
                                  List<Category> categories, Date time, int amount, String image, String link, RestCallback<Post> callback) {
        List<Integer> categoriesId = new ArrayList<>();
        for (Category category : categories) {
            categoriesId.add(category.mId);
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("mContent", content);
        params.put("place", place);
        params.put("categories", categoriesId);
        params.put("time", time);
        params.put("amount", amount);
        params.put("image", image);
        params.put("link", link);

        HashMap<String, Object> coordinate = new HashMap<>();
        coordinate.put("coordinates", new Double[]{longitude, latitude});

        params.put("location", coordinate);

        ServiceManager.getInstance().getPostService().createPost(params).enqueue(callback);
    }

    public static void updatePost(int postId, String content, double latitude, double longitude, String place,
                                  List<Category> categories, Date time, int amount, String imageUrl, String link, RestCallback<Post> callback) {
        List<Integer> categoriesId = new ArrayList<>();
        for (Category category : categories) {
            categoriesId.add(category.mId);
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("mContent", content);
        params.put("place", place);
        params.put("categories", categoriesId);
        params.put("time", time);
        params.put("amount", amount);
        params.put("image", imageUrl);
        params.put("link", link);

        HashMap<String, Object> coordinate = new HashMap<>();
        coordinate.put("coordinates", new Double[]{longitude, latitude});

        params.put("location", coordinate);

        ServiceManager.getInstance().getPostService().updatePost(postId, params).enqueue(callback);
    }

    public static void getListPost(int pageNumber, boolean isInterested, RestCallback<List<Post>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("flag", isInterested);

        ServiceManager.getInstance().getPostService().getAllPost(pageNumber, params).enqueue(callback);
    }
}
