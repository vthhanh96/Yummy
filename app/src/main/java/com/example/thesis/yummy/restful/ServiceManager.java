package com.example.thesis.yummy.restful;

import com.example.thesis.yummy.AppConstants;
import com.example.thesis.yummy.restful.services.CategoryService;
import com.example.thesis.yummy.restful.services.PostService;
import com.example.thesis.yummy.restful.services.UserService;

public class ServiceManager {

    private static ServiceManager mInstance = null;

    private static PostService mPostService;
    private static UserService mUserService;
    private static CategoryService mCategoryService;

    private ServiceManager() {}

    public static synchronized ServiceManager getInstance() {
        if(mInstance == null) {
            mInstance = new ServiceManager();
        }

        return mInstance;
    }

    public PostService getPostService() {
        ServiceGenerator.changeBaseUrl(AppConstants.BASE_SERVER_URL);
        if(mPostService == null) {
            mPostService = ServiceGenerator.createService(PostService.class);
        }
        return mPostService;
    }

    public UserService getUserService(){
        ServiceGenerator.changeBaseUrl(AppConstants.BASE_USER_URL);
        if(mUserService == null) {
            mUserService = ServiceGenerator.createService(UserService.class);
        }
        return mUserService;
    }

    public CategoryService getCategoryService() {
        ServiceGenerator.changeBaseUrl(AppConstants.BASE_SERVER_URL);
        if(mCategoryService == null) {
            mCategoryService = ServiceGenerator.createService(CategoryService.class);
        }

        return mCategoryService;
    }
}
