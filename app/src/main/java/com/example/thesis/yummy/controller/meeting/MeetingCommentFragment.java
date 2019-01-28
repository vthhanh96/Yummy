package com.example.thesis.yummy.controller.meeting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.Application;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.notification.NotificationHandler;
import com.example.thesis.yummy.controller.post.CommentActivity;
import com.example.thesis.yummy.controller.profile.ProfileActivity;
import com.example.thesis.yummy.eventbus.EventNewComment;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.restful.model.Message;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.restful.request.MeetingRequest;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.DateUtils;
import com.example.thesis.yummy.view.dialog.InputDialog;
import com.example.thesis.yummy.view.dialog.SelectCommentOptionsDialogFragment;
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
import butterknife.OnClick;

public class MeetingCommentFragment extends Fragment implements MessageInput.InputListener,
        MessageInput.TypingListener {

    @BindView(R.id.messagesList) MessagesList mMessagesList;
    @BindView(R.id.input) MessageInput mMessageInput;

    private int mMeetingID;
    private ImageLoader mImageLoader;
    private MessagesListAdapter<Comment> mMessagesListAdapter;
    private boolean mIsActive = true;

    public static MeetingCommentFragment newInstance(int meetingID) {
        MeetingCommentFragment instance = new MeetingCommentFragment();
        instance.mMeetingID = meetingID;
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_comment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        mIsActive = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StorageManager.saveIsComment(false);
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        initImageLoader();
        initAdapter();
        initMessageInput();
        getMeetingComments();
        StorageManager.saveIsComment(true);
        EventBus.getDefault().register(this);
    }

    private void initImageLoader() {
        mImageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                if(getContext() == null) return;
                Glide.with(getContext().getApplicationContext()).load(url).apply(RequestOptions.circleCropTransform()).into(imageView);
            }
        };
    }

    private void initAdapter() {
        mMessagesListAdapter = new MessagesListAdapter<>(String.valueOf(StorageManager.getUser().getId()), mImageLoader);
        mMessagesListAdapter.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

            }
        });

        mMessagesListAdapter.registerViewClickListener(R.id.messageUserAvatar, new MessagesListAdapter.OnMessageViewClickListener<Comment>() {
            @Override
            public void onMessageViewClick(View view, Comment message) {
                try {
                    ProfileActivity.start(getContext(), Integer.parseInt(message.getUser().getId()));
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

    public void getMeetingComments() {
        ServiceManager.getInstance().getMeetingService().getMeetingComments(mMeetingID).enqueue(new RestCallback<List<Comment>>() {
            @Override
            public void onSuccess(String message, List<Comment> comments) {
                mMessagesListAdapter.addToEnd(comments, true);
            }

            @Override
            public void onFailure(String message) {
                if(getContext() == null) return;
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openInputDialog() {
        InputDialog inputDialog = new InputDialog();
        inputDialog.setTitle(getString(R.string.input_comment));
        inputDialog.setListener(new InputDialog.InputDialogListener() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onDoneClick(String content) {
                createMeetingComment(content);
            }
        });
        inputDialog.show(getChildFragmentManager(), "Input Dialog");
    }

    private void createMeetingComment(String content) {
        MeetingRequest.createComment(mMeetingID, content, new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
            }

            @Override
            public void onFailure(String message) {
            }
        });
    }

    private void showCommentActionPopup(final Comment comment) {
        User user = StorageManager.getUser();
        if(user != null && user.mId.equals(comment.mCreator.mId)) {
            SelectCommentOptionsDialogFragment dialogFragment = new SelectCommentOptionsDialogFragment();
            dialogFragment.setCommentOptionsListener(new SelectCommentOptionsDialogFragment.SelectCommentOptionsListener() {
                @Override
                public void editComment() {
                    showEditCommentDialog(comment);
                }

                @Override
                public void deleteComment() {
                    onDeleteComment(comment);
                }
            });
            dialogFragment.show(getChildFragmentManager(), "");
        }
    }

    private void showEditCommentDialog(final Comment comment) {
        if(getContext() == null) return;

        InputDialog inputDialog = new InputDialog();
        inputDialog.setTitle(getString(R.string.input_comment));
        inputDialog.setListener(new InputDialog.InputDialogListener() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onDoneClick(String content) {
                editComment(content, comment);
            }
        });
        inputDialog.setContentInput(comment.mContent);
        inputDialog.show(getChildFragmentManager(), "Input Dialog");
    }

    private void editComment(String content, Comment comment) {

    }

    private void onDeleteComment(Comment comment) {

    }

    @Override
    public String toString() {
        return Application.mContext.getString(R.string.chat);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        Comment comment = new Comment(-1, StorageManager.getUser(), input.toString(), Calendar.getInstance().getTime());
        mMessagesListAdapter.addToStart(comment, true);
        createMeetingComment(input.toString());
        return true;
    }

    @Override
    public void onStartTyping() {

    }

    @Override
    public void onStopTyping() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewComment(EventNewComment eventNewComment) {
        if(eventNewComment == null || eventNewComment.mComment == null || !mIsActive) return;
        if(eventNewComment.mComment.mParentID == mMeetingID) {
            mMessagesListAdapter.addToStart(eventNewComment.mComment, true);
        } else {
            NotificationHandler.createNotification(getContext(), eventNewComment.mDataNotification);
        }
    }
}
