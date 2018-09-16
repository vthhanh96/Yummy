package com.example.thesis.yummy.restful;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RestCallback<T> implements Callback<RestResponse<T>> {

    @Override
    public void onResponse(@NonNull Call<RestResponse<T>> call, @NonNull Response<RestResponse<T>> response) {

        if(!response.isSuccessful()) {
            try {
                String errorResponse = response.errorBody().string();
                JSONObject jsonResponse = new JSONObject(errorResponse);

                String message = jsonResponse.optString("message", "Unknow error");

                onFailure(message);
            } catch (Exception e) {
                onFailure("Unknow error");
            }
            return;
        }

        RestResponse<T> restResponse = response.body();
        if(restResponse == null) {
            onFailure("Unknow error");
            return;
        }

        if (restResponse.message == null) {
            restResponse.message = "Unknow error";
        }

        if(!restResponse.success) {
            onFailure(restResponse.message);
            return;
        }

        onSuccess(restResponse.message, restResponse.data);
    }

    @Override
    public void onFailure(@NonNull final Call<RestResponse<T>> call, Throwable t) {
        onFailure("Unknow error");
    }

    public abstract void onSuccess(String message, T t);

    public abstract void onFailure(String message);
}
