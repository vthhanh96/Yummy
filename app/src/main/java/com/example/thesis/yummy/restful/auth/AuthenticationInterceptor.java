package com.example.thesis.yummy.restful.auth;

import android.support.annotation.NonNull;

import com.example.thesis.yummy.storage.StorageManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {
    public AuthenticationInterceptor() {
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final Request original = chain.request();
        final Request.Builder builder = original.newBuilder();
        builder.header("Authorization", StorageManager.getAccessToken());
        Request request = builder.build();
        return chain.proceed(request);
    }
}
