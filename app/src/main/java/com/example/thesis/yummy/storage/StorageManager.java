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
    },
    EXPIRE_IN{
        @Override
        public String toString() {
            return "EXPIRE_IN";
        }
    },
    IS_CHATTING {
        @Override
        public String toString() {
            return "IS_CHATTING";
        }
    },
    IS_COMMENT {
        @Override
        public String toString() {
            return "IS_COMMENT";
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
        mUser = user;
        Hawk.put(StorageKey.USER_DATA.toString(), user);
    }

    public static User getUser() {
        if(mUser == null) {
            mUser = Hawk.get(StorageKey.USER_DATA.toString());
        }
        return mUser;
    }

    public static void saveExpireIn(Long expireIn) {
        Hawk.put(StorageKey.EXPIRE_IN.toString(), expireIn);
    }

    public static Long getExpireIn() {
        return Hawk.get(StorageKey.EXPIRE_IN.toString(), 0L);
    }

    public static void saveIsChatting(boolean isChatting) {
        Hawk.put(StorageKey.IS_CHATTING.toString(), isChatting);
    }

    public static boolean isChatting() {
        return Hawk.get(StorageKey.IS_CHATTING.toString(), false);
    }

    public static void saveIsComment(boolean isComment) {
        Hawk.put(StorageKey.IS_COMMENT.toString(), isComment);
    }

    public static boolean isComment() {
        return Hawk.get(StorageKey.IS_COMMENT.toString(), false);
    }

    public static void deleteAll() {
        mUser = null;
        Hawk.deleteAll();
    }
}