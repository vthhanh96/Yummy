package com.example.thesis.yummy.restful.auth;

import com.example.thesis.yummy.Application;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.response.LoginResponse;
import com.example.thesis.yummy.storage.StorageManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthClient {

    public interface AuthCallBack {
        void onAuthorized();
        void onUnauthorized(String message);
    }

    public static void login(String email, String password, final AuthCallBack callBack) {
        ServiceManager.getInstance().getUserService().login(email, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        if(response.body().mSuccess) {
                            StorageManager.saveAccessToken(response.body().mToken);
                            callBack.onAuthorized();
                        }else {
                            callBack.onUnauthorized(response.body().mMessage);
                        }
                    } else {
                        callBack.onUnauthorized(Application.mContext.getString(R.string.unauthorized));
                    }
                } else {
                    callBack.onUnauthorized(Application.mContext.getString(R.string.unauthorized));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callBack.onUnauthorized(t.getMessage());
            }
        });
    }
}