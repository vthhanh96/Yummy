package com.example.thesis.yummy.storage;

import com.example.thesis.yummy.restful.model.User;
import com.orhanobut.hawk.Hawk;

enum StorageKey {
    OAUTH2_ACCESS_TOKEN {
        @Override
        public String toString() {
            return "OAUTH2_ACCESS_TOKEN";
        }
    },
    USER_DATA{
        @Override
        public String toString() {
            return "USER_DATA";
        }
    }
}

public class StorageManager {
    private static User mUser;

    public static void saveAccessToken(String token) {
        Hawk.put(StorageKey.OAUTH2_ACCESS_TOKEN.toString(), token);
    }

    public static String getAccessToken() {
        return Hawk.get(StorageKey.OAUTH2_ACCESS_TOKEN.toString(), "");
    }

    public static void saveUser(User user) {
        Hawk.put(StorageKey.USER_DATA.toString(), user);
    }

    public static User getUser() {
        if(mUser == null) {
            mUser = Hawk.get(StorageKey.USER_DATA.toString());
        }
        return mUser;
    }

    public static void deleteAll() {
        Hawk.deleteAll();
    }
}