package com.example.thesis.yummy.restful;

import com.example.thesis.yummy.AppConstants;
import com.example.thesis.yummy.restful.services.CategoryService;
import com.example.thesis.yummy.restful.services.ChatService;
import com.example.thesis.yummy.restful.services.MeetingService;
import com.example.thesis.yummy.restful.services.NotificationService;
import com.example.thesis.yummy.restful.services.PostService;
import com.example.thesis.yummy.restful.services.RatingService;
import com.example.thesis.yummy.restful.services.UploadService;
import com.example.thesis.yummy.restful.services.UserService;
import com.example.thesis.yummy.restful.services.VoucherService;

public class ServiceManager {

    private static ServiceManager mInstance = null;

    private static PostService mPostService;
    private static UserService mUserService;
    private static CategoryService mCategoryService;
    private static MeetingService mMeetingService;
    private static NotificationService mNotificationService;
    private static RatingService mRatingService;
    private static VoucherService mVoucherService;
    private static UploadService mUploadService;
    private static ChatService mChatService;

    private ServiceManager() {}

    public static synchronized ServiceManager getInstance() {
        if(mInstance == null) {
            mInstance = new ServiceManager();
        }

        return mInstance;
    }

    public PostService getPostService() {
        ServiceGenerator.changeBaseUrl(AppConstants.BASE_SERVER_API_URL);
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
        ServiceGenerator.changeBaseUrl(AppConstants.BASE_SERVER_API_URL);
        if(mCategoryService == null) {
            mCategoryService = ServiceGenerator.createService(CategoryService.class);
        }

        return mCategoryService;
    }

    public MeetingService getMeetingService() {
        ServiceGenerator.changeBaseUrl(AppConstants.BASE_SERVER_API_URL);
        if(mMeetingService == null) {
            mMeetingService = ServiceGenerator.createService(MeetingService.class);
        }

        return mMeetingService;
    }

    public NotificationService getNotificationService() {
        ServiceGenerator.changeBaseUrl(AppConstants.BASE_SERVER_API_URL);
        if(mNotificationService == null) {
            mNotificationService = ServiceGenerator.createService(NotificationService.class);
        }

        return mNotificationService;
    }

    public RatingService getRatingService() {
        ServiceGenerator.changeBaseUrl(AppConstants.BASE_SERVER_API_URL);
        if(mRatingService == null) {
            mRatingService = ServiceGenerator.createService(RatingService.class);
        }
        return mRatingService;
    }

    public VoucherService getVoucherService() {
        ServiceGenerator.changeBaseUrl(AppConstants.BASE_SERVER_API_URL);
        if(mVoucherService == null) {
            mVoucherService = ServiceGenerator.createService(VoucherService.class);
        }

        return mVoucherService;
    }

    public UploadService getUploadService() {
        ServiceGenerator.changeBaseUrl(AppConstants.BASE_SERVER_API_URL);
        if(mUploadService == null) {
            mUploadService = ServiceGenerator.createService(UploadService.class);
        }
        return mUploadService;
    }

    public ChatService getChatService() {
        ServiceGenerator.changeBaseUrl(AppConstants.BASE_SERVER_API_URL);
        if(mChatService == null) {
            mChatService = ServiceGenerator.createService(ChatService.class);
        }
        return mChatService;
    }
}
