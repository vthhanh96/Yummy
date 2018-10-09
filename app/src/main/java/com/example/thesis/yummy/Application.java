package com.example.thesis.yummy;

import android.content.Context;

import com.cloudinary.android.MediaManager;
import com.example.thesis.yummy.restful.auth.AuthClient;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.socket.SocketManager;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.ThreadUtils;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import org.json.JSONObject;

import java.net.URISyntaxException;

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
            ThreadUtils.getInstance().runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
//                    String username;
//                    String message;
//                    try {
//                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        return;
//                    }
//
//                    // add the message to view
//                    addMessage(username, message);
                }
            });
        }
    };

    public static void initSocket() {
        User user = StorageManager.getUser();
        if(user == null) return;
        if(AuthClient.isExpireToken()) return;

        try {
            mSocket = IO.socket(SOCKET_BASE_URL);
        } catch (URISyntaxException e) {}

        User data = new User(user.mId, user.mFullName);
        mSocket.connect();
        mSocket.emit(SocketManager.EVENT_CONNECT_SERVER, new Gson().toJson(data));

        mSocket.on(SocketManager.EVENT_NOTIFICATION + user.mId, onEventNotificationListener);
    }

    public static void clearSocket() {
        if(mSocket == null) return;
        mSocket.disconnect();
        mSocket.off();
        mSocket = null;
    }
}
