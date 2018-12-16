package com.example.thesis.yummy.restful;

import com.example.thesis.yummy.AppConstants;
import com.example.thesis.yummy.restful.adapter.CategoryAdapter;
import com.example.thesis.yummy.restful.adapter.CommentAdapter;
import com.example.thesis.yummy.restful.adapter.DateJsonAdapter;
import com.example.thesis.yummy.restful.adapter.LocationAdapter;
import com.example.thesis.yummy.restful.adapter.MeetingAdapter;
import com.example.thesis.yummy.restful.adapter.MeetingPointAdapter;
import com.example.thesis.yummy.restful.adapter.NotificationDataAdapter;
import com.example.thesis.yummy.restful.adapter.UserAdapter;
import com.example.thesis.yummy.restful.auth.AuthenticationInterceptor;
import com.example.thesis.yummy.restful.model.Category;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.restful.model.Location;
import com.example.thesis.yummy.restful.model.Meeting;
import com.example.thesis.yummy.restful.model.MeetingPoint;
import com.example.thesis.yummy.restful.model.NotificationData;
import com.example.thesis.yummy.restful.model.User;
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
        hashMap.put(Date.class, new DateJsonAdapter());
        hashMap.put(Comment.class, new CommentAdapter());
        hashMap.put(User.class, new UserAdapter());
        hashMap.put(Category.class, new CategoryAdapter());
        hashMap.put(NotificationData.class, new NotificationDataAdapter());
        hashMap.put(Meeting.class, new MeetingAdapter());
        hashMap.put(MeetingPoint.class, new MeetingPointAdapter());
        hashMap.put(Location.class, new LocationAdapter());

        for (Map.Entry<Type, JsonAdapter> entry : hashMap.entrySet()) {
            if(type != entry.getKey()) {
                moshiBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        return moshiBuilder.build();
    }
}
