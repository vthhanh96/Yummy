package com.example.thesis.yummy.restful.request;

import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.User;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRequest {

    public static void updateAddress(String address, Double latitude, Double longitude, RestCallback<User> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("address", address);

        HashMap<String, Object> coordinate = new HashMap<>();
        coordinate.put("coordinates", new Double[]{longitude, latitude});

        params.put("latlngAddress", coordinate);

        ServiceManager.getInstance().getUserService().updateAddress(params).enqueue(callback);
    }

    public static void updateProfile(String avatar, String name, int gender, Date birthday, String address, Double latitude, Double longitude, RestCallback<User> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("fullName", name);
        params.put("gender", gender);
        params.put("birthday", birthday);
        params.put("address", address);
        params.put("avatar", avatar);

        HashMap<String, Object> coordinate = new HashMap<>();
        coordinate.put("coordinates", new Double[]{longitude, latitude});

        params.put("latlngAddress", coordinate);

        ServiceManager.getInstance().getUserService().updateProfile(params).enqueue(callback);
    }

    public static void searchUsers(int page, Integer gender, int ageFrom, int ageTo, float latitude, float longitude, RestCallback<List<User>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("gender", gender);
        params.put("tuoiduoi", ageFrom);
        params.put("tuoitren", ageTo);
        params.put("latitude", latitude);
        params.put("longitude", longitude);

        ServiceManager.getInstance().getUserService().searchUser(page, params).enqueue(callback);
    }

    public static void sendRequest(int userId, String content, Double latitude, Double longitude, String place, Date time, Integer meetingID, RestCallback<Base> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("userSearch", userId);
        params.put("content", content);
        params.put("place", place);
        params.put("time", time);
        params.put("meeting", meetingID);

        HashMap<String, Object> coordinate = new HashMap<>();
        coordinate.put("coordinates", new Double[]{longitude, latitude});

        params.put("location", coordinate);

        ServiceManager.getInstance().getUserService().sendRequest(params).enqueue(callback);
    }

    public static void updateCurrentLocation(Double latitude, Double longitude, RestCallback<User> callback) {
        HashMap<String, Object> params = new HashMap<>();

        HashMap<String, Object> coordinate = new HashMap<>();
        coordinate.put("coordinates", new Double[]{longitude, latitude});

        params.put("latlngAddress", coordinate);

        ServiceManager.getInstance().getUserService().updateCurrentLocation(params).enqueue(callback);
    }

    public static void changePass(String oldPass, String newPass, RestCallback<Base> callback) {
        Map<String, Object> params = new HashMap<>();

        params.put("password", oldPass);
        params.put("newPassWord", newPass);

        ServiceManager.getInstance().getUserService().changePass(params).enqueue(callback);
    }
}
