package com.example.thesis.yummy.restful.request;

import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.User;

import java.util.Date;
import java.util.HashMap;

public class UserRequest {

    public static void updateAddress(String address, Double latitude, Double longitude, RestCallback<User> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("address", address);

        HashMap<String, Object> coordinate = new HashMap<>();
        coordinate.put("coordinates", new Double[]{longitude, latitude});

        params.put("latLngAddress", coordinate);

        ServiceManager.getInstance().getUserService().updateAddress(params).enqueue(callback);
    }

    public static void updateProfile(String name, int gender, Date birthday, String address, Double latitude, Double longitude, RestCallback<User> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("fullName", name);
        params.put("gender", gender);
        params.put("birthday", birthday);
        params.put("address", address);

        HashMap<String, Object> coordinate = new HashMap<>();
        coordinate.put("coordinates", new Double[]{longitude, latitude});

        params.put("latLngAddress", coordinate);

        ServiceManager.getInstance().getUserService().updateProfile(params).enqueue(callback);
    }
}
