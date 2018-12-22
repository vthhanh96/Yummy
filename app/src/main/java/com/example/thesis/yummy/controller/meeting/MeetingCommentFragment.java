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
import com.example.thesis.yummy.controller.post.CommentActivity;
import com.example.thesis.yummy.controller.profile.ProfileActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.DateUtils;
import com.example.thesis.yummy.view.dialog.InputDialog;
import com.example.thesis.yummy.view.dialog.SelectCommentOptionsDialogFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeetingCommentFragment extends Fragment {

    @BindView(R.id.commentRecyclerView) RecyclerView mCommentRecyclerView;
    @BindView(R.id.createCommentButton) FloatingActionButton mCreateCommentButton;

    private MeetingDetailListener mListener;
    private CommentAdapter mCommentAdapter;
    private int mMeetingID;

    public static MeetingCommentFragment newInstance(int meetingID) {
        MeetingCommentFragment instance = new MeetingCommentFragment();
        instance.mMeetingID = meetingID;
        return instance;
    }

    @OnClick(R.id.createCommentButton)
    public void createComment() {
        openInputDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_comment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    public void setMeetingDetailListener(MeetingDetailListener listener) {
        mListener = listener;
    }

    private void init() {
        initRecyclerView();
        getMeetingComments();
    }

    private void initRecyclerView() {
        mCommentAdapter = new CommentAdapter();
        mCommentAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                Comment comment = mCommentAdapter.getItem(position);
                if(comment == null) return false;
                showCommentActionPopup(comment);
                return false;
            }
        });

        mCommentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Comment comment = mCommentAdapter.getItem(position);
                if(comment == null) return;
                switch (view.getId()) {
                    case R.id.imgAvatar:
                        if(comment.mCreator == null) return;
                        ProfileActivity.start(getContext(), comment.mCreator.mId);
                        break;
                }
            }
        });

        mCommentRecyclerView.setAdapter(mCommentAdapter);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mCommentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy<0 && !mCreateCommentButton.isShown())
                    mCreateCommentButton.show();
                else if(dy>0 && mCreateCommentButton.isShown())
                    mCreateCommentButton.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    public void getMeetingComments() {
        ServiceManager.getInstance().getMeetingService().getMeetingComments(mMeetingID).enqueue(new RestCallback<List<Comment>>() {
            @Override
            public void onSuccess(String message, List<Comment> comments) {
                mCommentAdapter.setNewData(comments);
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
        if(mListener != null) {
            mListener.onCreateComment(content);
        }
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
        if(mListener != null) {
            mListener.onUpdateComment(content, comment);
        }
    }

    private void onDeleteComment(Comment comment) {
        if(mListener != null) {
            mListener.onDeleteComment(comment);
        }
    }

    public class CommentAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> {

        public CommentAdapter() {
            super(R.layout.item_comment_layout, new ArrayList<Comment>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Comment item) {
            helper.setText(R.id.tvName, item.mCreator.mFullName);
            helper.setText(R.id.tvTime, DateUtils.getTimeAgo(mContext, item.mCreatedDate));
            helper.setText(R.id.tvContent, item.mContent);

            ImageView imgAvatar = helper.getView(R.id.imgAvatar);
            Glide.with(mContext).load(item.mCreator.mAvatar).apply(RequestOptions.circleCropTransform()).into(imgAvatar);

            helper.addOnClickListener(R.id.imgAvatar);
        }
    }

    @Override
    public String toString() {
        return Application.mContext.getString(R.string.comment);
    }
}
