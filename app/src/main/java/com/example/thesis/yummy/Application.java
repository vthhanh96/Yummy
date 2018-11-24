package com.example.thesis.yummy;

import android.content.Context;

import com.cloudinary.android.MediaManager;
import com.example.thesis.yummy.controller.notification.NotificationHandler;
import com.example.thesis.yummy.controller.notification.RatingNotificationActivity;
import com.example.thesis.yummy.restful.ServiceGenerator;
import com.example.thesis.yummy.restful.auth.AuthClient;
import com.example.thesis.yummy.restful.model.Notification;
import com.example.thesis.yummy.restful.model.NotificationMeetingData;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.socket.SocketManager;
import com.example.thesis.yummy.storage.StorageManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.squareup.moshi.JsonAdapter;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.example.thesis.yummy.AppConstants.NOTIFICATION_MEETING;
import static com.example.thesis.yummy.AppConstants.NOTIFICATION_RECONNECT_SOCKET;
import static com.example.thesis.yummy.AppConstants.NOTIFICATION_TYPE_NORMAL;
import static com.example.thesis.yummy.AppConstants.NOTIFICATION_TYPE_RATING;
import static com.example.thesis.yummy.AppConstants.SOCKET_BASE_URL;


public class Application extends android.app.Application {

    public static Context mContext;

    public static Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        clearSocket();
    }

    private void init() {
        initStorage();
        MediaManager.init(this);
        initSocket();
    }

    private void initStorage() {
        Hawk.init(this).build();
    }

    private static Emitter.Listener onEventNotificationListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            if (data == null) return;

            if (data.has(NOTIFICATION_TYPE_NORMAL)) {
                NotificationHandler.createNotification(mContext, data.optString(NOTIFICATION_TYPE_NORMAL));
            } else if(data.has(NOTIFICATION_TYPE_RATING)) {
                JsonAdapter<Notification> jsonAdapter = ServiceGenerator.getMoshiWithoutType(Notification.class).adapter(Notification.class);
                try {
                    Notification notification = jsonAdapter.fromJson(data.optString(NOTIFICATION_TYPE_RATING));
                    RatingNotificationActivity.start(mContext, notification);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else if(data.has(NOTIFICATION_RECONNECT_SOCKET)) {
                clearSocket();
                initSocket();
            }
        }
    };

    public static void initSocket() {
        User user = StorageManager.getUser();
        if (user == null) return;
        if (AuthClient.isExpireToken()) return;

        try {
            mSocket = IO.socket(SOCKET_BASE_URL);
        } catch (URISyntaxException e) {
        }

        User data = new User(user.mId, user.mFullName);
        mSocket.connect();
        mSocket.emit(SocketManager.EVENT_CONNECT_SERVER, new Gson().toJson(data));

        mSocket.on(SocketManager.EVENT_NOTIFICATION + user.mId, onEventNotificationListener);
    }

    public static void clearSocket() {
        if (mSocket == null) return;
        mSocket.disconnect();
        mSocket.off();
        mSocket = null;
    }
}
