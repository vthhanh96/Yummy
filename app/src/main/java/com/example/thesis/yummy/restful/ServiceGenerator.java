package com.example.thesis.yummy.restful;

import com.example.thesis.yummy.AppConstants;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ServiceGenerator {
    private static Retrofit.Builder builder = createBuilder(AppConstants.BASE_SERVER_URL);

    private static Retrofit.Builder createBuilder(String baseUrl) {
        Moshi moshi = getMoshiWithoutType(null);

        return new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(baseUrl);
    }

    public static <T> T createService(Class<T> serviceClass) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.interceptors().add(interceptor);
        httpClient.interceptors().add(new AuthenticationInterceptor());

        builder.client(httpClient.build());
        return builder.build().create(serviceClass);
    }

    public static void changeBaseUrl(String baseUrl) {
        builder = createBuilder(baseUrl);
    }

    public static Moshi getMoshiWithoutType(Type type) {
        HashMap<Type, JsonAdapter> hashMap = new HashMap<>();

        Moshi.Builder moshiBuilder = new Moshi.Builder();

        for (Map.Entry<Type, JsonAdapter> entry : hashMap.entrySet()) {
            if(type != entry.getKey()) {
                moshiBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        return moshiBuilder.build();
    }
}
