package com.example.thesis.yummy.storage;

import com.orhanobut.hawk.Hawk;

/**
 * Created by vuong on 2/22/18.
 */

enum StorageKey {
    OAUTH2_ACCESS_TOKEN {
        @Override
        public String toString() {
            return "OAUTH2_ACCESS_TOKEN";
        }
    }
}

public class StorageManager {
    public static void saveAccessToken(String token) {
        Hawk.put(StorageKey.OAUTH2_ACCESS_TOKEN.toString(), token);
    }

    public static String getAccessToken() {
        return Hawk.get(StorageKey.OAUTH2_ACCESS_TOKEN.toString(), "");
    }

    public static void deleteAll() {
        Hawk.deleteAll();
    }
}