package com.example.thesis.yummy.controller.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.Application;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.controller.shared.EmptyLayout;
import com.example.thesis.yummy.eventbus.EventNewMessage;
import com.example.thesis.yummy.eventbus.EventUpdateListChat;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Conversation;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.DateUtils;
import com.example.thesis.yummy.view.TopBarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_CHAT;

public class ChatListActivity extends DrawerActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, ChatListActivity.class);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.chatListRecyclerView) RecyclerView mChatListRecyclerView;

    private ConversationAdapter mAdapter;
    private int mPageNumber = 0;
    private User mCurrentUser;

    @Override
    protected int getNavId() {
        return NAV_DRAWER_ID_CHAT;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        getUser();
        initTopBarView();
        initRecyclerView();
        getConversationHistory();
        EventBus.getDefault().register(this);
    }

    private void getUser() {
        mCurrentUser = StorageManager.getUser();
    }

    private void initTopBarView() {
        mTopBarView.setTitle(getString(R.string.chat));
        mTopBarView.setImageViewLeft(TopBarView.LEFT_MENU);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                openDrawer();
            }

            @Override
            public void onRightClick() {

            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new ConversationAdapter();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Conversation conversation = mAdapter.getItem(position);
                if(conversation == null) return;

                User user = null;
                if(conversation.mUser != null && mCurrentUser != null) {
                    for (User userChat : conversation.mUser) {
                        if(!userChat.mId.equals(mCurrentUser.mId)) {
                            user = userChat;
                            break;
                        }
                    }
                }
                if(user == null) return;

                if(conversation.mLastMessage != null && conversation.mLastMessage.mFromUser != null && !conversation.mLastMessage.mFromUser.mId.equals(mCurrentUser.mId)) {
                    Application.emitSeenMessage(conversation.mLastMessage.mId);
                    conversation.mLastMessage.mIsSeen = true;
                    mAdapter.notifyItemChanged(position);
                }

                ChatActivity.start(ChatListActivity.this, user);
            }
        });

        EmptyLayout emptyLayout = new EmptyLayout(this);
        emptyLayout.setEmptyImageMessage(getString(R.string.empty_chat));
        emptyLayout.setEmptyImageResource(R.drawable.ic_empty_chat);
        mAdapter.setEmptyView(emptyLayout);

        mChatListRecyclerView.setAdapter(mAdapter);
        mChatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getConversationHistory();
            }
        }, mChatListRecyclerView);
    }

    private void getConversationHistory() {
        ServiceManager.getInstance().getChatService().getConversationHistory(mPageNumber).enqueue(new RestCallback<List<Conversation>>() {
            @Override
            public void onSuccess(String message, List<Conversation> conversations) {
                if(conversations == null || conversations.isEmpty()) {
                    mAdapter.loadMoreEnd();
                    return;
                }
                mAdapter.addData(conversations);
                mAdapter.loadMoreComplete();
                mPageNumber++;
            }

            @Override
            public void onFailure(String message) {
                mAdapter.loadMoreFail();
            }
        });
    }

    private class ConversationAdapter extends BaseQuickAdapter<Conversation, BaseViewHolder> {

        public ConversationAdapter() {
            super(R.layout.layout_conversation_item, new ArrayList<Conversation>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Conversation item) {
            if(item == null) return;
            ImageView imageView = helper.getView(R.id.avatarImageView);
            User user = null;
            if(item.mUser != null && mCurrentUser != null) {
                for (User userChat : item.mUser) {
                    if(!userChat.mId.equals(mCurrentUser.mId)) {
                        user = userChat;
                        break;
                    }
                }
            }
            if(user != null && !TextUtils.isEmpty(user.mAvatar)) {
                Glide.with(mContext.getApplicationContext()).load(user.mAvatar).apply(RequestOptions.circleCropTransform()).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ic_default_avatar);
            }

            helper.setText(R.id.nameTextView, user == null ? "" : user.mFullName);
            if(item.mLastMessage != null ) {
                if (item.mLastMessage.mFromUser != null && item.mLastMessage.mFromUser.mId.equals(mCurrentUser.mId)) {
                    helper.setText(R.id.lastMessageTextView, "Bạn: " + item.mLastMessage.mMessage);
                    helper.setBackgroundColor(R.id.rootLayout, ContextCompat.getColor(mContext, R.color.gray_light));
                } else  {
                    helper.setText(R.id.lastMessageTextView, item.mLastMessage.mMessage);
                    helper.setBackgroundColor(R.id.rootLayout, (item.mLastMessage != null && item.mLastMessage.mIsSeen) ? ContextCompat.getColor(mContext, R.color.gray_light) : ContextCompat.getColor(mContext, R.color.white));
                }
            } else {
                helper.setText(R.id.lastMessageTextView, "");
                helper.setBackgroundColor(R.id.rootLayout, ContextCompat.getColor(mContext, R.color.gray_light));
            }
            helper.setText(R.id.timeTextView, DateUtils.getTimeAgo(mContext, item.mLastDate));
        }
    }

    private void resetListChat() {
        mPageNumber = 0;
        mAdapter.setNewData(null);
        getConversationHistory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void newMessageEvent(EventNewMessage eventNewMessage) {
        resetListChat();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventUpdateListChat(EventUpdateListChat eventUpdateListChat) {
        resetListChat();
    }
}

