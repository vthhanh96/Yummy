package com.example.thesis.yummy.controller.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thesis.yummy.Application;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.profile.ProfileActivity;
import com.example.thesis.yummy.eventbus.EventNewMessage;
import com.example.thesis.yummy.eventbus.EventUpdateListChat;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Message;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.restful.request.ChatRequest;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.TopBarView;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends BaseActivity implements MessageInput.InputListener,
        MessageInput.TypingListener {

    private static final String ARG_KEY_USER_CHAT = "ARG_KEY_USER_CHAT";

    public static void start(Context context, User userChat) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(ARG_KEY_USER_CHAT, userChat);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.messagesList) MessagesList mMessagesList;
    @BindView(R.id.input) MessageInput mMessageInput;

    private ImageLoader mImageLoader;
    private MessagesListAdapter<Message> mMessagesListAdapter;
    private User mUserChat;
    private int mSenderId;
    private int mPageNumber = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        Message message = new Message(-1, input.toString(), mUserChat, StorageManager.getUser(), Calendar.getInstance().getTime());
        mMessagesListAdapter.addToStart(message, true);
        createChatMessage(input.toString());
        return true;
    }

    @Override
    public void onStartTyping() {

    }

    @Override
    public void onStopTyping() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        getExtras();
        initTopBarView();
        initImageLoader();
        initAdapter();
        initMessageInput();
        getMessages();
        EventBus.getDefault().register(this);
    }

    private void getExtras() {
        mUserChat = (User) getIntent().getSerializableExtra(ARG_KEY_USER_CHAT);
        User user = StorageManager.getUser();
        if(user != null) {
            mSenderId = user.mId;
        } else {
            mSenderId = mUserChat.mId;
        }
    }

    private void initTopBarView() {
        mTopBarView.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBarView.setTitle(mUserChat == null ? "" : mUserChat.mFullName);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
    }

    private void initImageLoader() {
        mImageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Glide.with(ChatActivity.this.getApplicationContext()).load(url).apply(RequestOptions.circleCropTransform()).into(imageView);
            }
        };
    }

    private void initAdapter() {
        mMessagesListAdapter = new MessagesListAdapter<>(String.valueOf(mSenderId), mImageLoader);
        mMessagesListAdapter.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

            }
        });
        mMessagesListAdapter.registerViewClickListener(R.id.messageUserAvatar, new MessagesListAdapter.OnMessageViewClickListener<Message>() {
            @Override
            public void onMessageViewClick(View view, Message message) {
                try {
                    ProfileActivity.start(ChatActivity.this, Integer.parseInt(message.getUser().getId()));
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        mMessagesList.setAdapter(mMessagesListAdapter);
    }

    private void initMessageInput() {
        mMessageInput.setInputListener(this);
        mMessageInput.setTypingListener(this);
    }

    private void getMessages() {
        if(mUserChat == null) return;
        ServiceManager.getInstance().getChatService().getChat(mUserChat.mId, mPageNumber).enqueue(new RestCallback<List<Message>>() {
            @Override
            public void onSuccess(String message, List<Message> messages) {
                mMessagesListAdapter.addToEnd(messages, true);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createChatMessage(String message) {
        if(mUserChat == null) return;
        ChatRequest.createChatMessage(mUserChat.mId, message, new RestCallback<Message>() {
            @Override
            public void onSuccess(String message, Message message2) {
                EventBus.getDefault().post(new EventUpdateListChat());
            }

            @Override
            public void onFailure(String message) {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void newMessageEvent(EventNewMessage eventNewMessage) {
        mMessagesListAdapter.addToStart(eventNewMessage.mMessage, true);
        Application.emitSeenMessage(eventNewMessage.mMessage.mId);
    }
}
