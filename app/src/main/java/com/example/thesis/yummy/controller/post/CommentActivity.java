package com.example.thesis.yummy.controller.post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.profile.ProfileActivity;
import com.example.thesis.yummy.controller.shared.EmptyLayout;
import com.example.thesis.yummy.eventbus.EventUpdatePost;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.DateUtils;
import com.example.thesis.yummy.view.TopBarView;
import com.example.thesis.yummy.view.dialog.InputDialog;
import com.example.thesis.yummy.view.dialog.SelectCommentOptionsDialogFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hong Hanh on 4/17/2018.
 */

public class CommentActivity extends BaseActivity {

    private static final String ARG_KEY_POST_ID = "ARG_KEY_POST_ID";

    @BindView(R.id.topBar) TopBarView mTopBar;
    @BindView(R.id.rcvComments) RecyclerView mCommentsRecyclerView;

    private Context mContext;
    private Integer mPostId;
    private CommentAdapter mAdapter;

    private String mToken;
    private User mUser;

    public static void start(Context context, Integer postId) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(ARG_KEY_POST_ID, postId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mContext = this;
        init();
    }

    private void init() {
        getExtras();
        getToken();
        initTopBar();
        initRecyclerView();
        getPostInfo();
    }

    private void getExtras() {
        mPostId = getIntent().getIntExtra(ARG_KEY_POST_ID, -1);
    }

    private void getToken() {
        mUser = StorageManager.getUser();
    }

    private void initTopBar() {
        mTopBar.setTitle(getString(R.string.comment));
        mTopBar.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBar.setImageViewRight(R.drawable.ic_add_comment);
        mTopBar.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                openInputDialog();
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new CommentAdapter();
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if(mAdapter.getData().get(position) == null) return false;
                showCommentActionPopup(view, mAdapter.getData().get(position));
                return false;
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Comment comment = mAdapter.getItem(position);
                if(comment == null || comment.mCreator == null) return;
                if(view.getId() == R.id.imgAvatar) {
                    ProfileActivity.start(mContext, comment.mCreator.mId);
                }
            }
        });

        EmptyLayout emptyLayout = new EmptyLayout(this);
        emptyLayout.setEmptyImageMessage(getString(R.string.empty_comment));
        emptyLayout.setEmptyImageResource(R.drawable.ic_empty_chat);
        mAdapter.setEmptyView(emptyLayout);

        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mCommentsRecyclerView.setAdapter(mAdapter);
    }

    private void getPostInfo() {
        showLoading();
        ServiceManager.getInstance().getPostService().getPostDetail(mPostId).enqueue(new RestCallback<Post>() {
            @Override
            public void onSuccess(String message, Post post) {
                hideLoading();
                mAdapter.setNewData(post.mComments);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
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
                createComment(content);
            }
        });
        inputDialog.show(getSupportFragmentManager(), "Input Dialog");
    }

    private void showCommentActionPopup(View view, final Comment comment) {
        if(mUser != null && mUser.mId.equals(comment.mCreator.mId)) {
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
            dialogFragment.show(getSupportFragmentManager(), CommentActivity.class.getName());
        }
    }

    private void showEditCommentDialog(final Comment comment) {
        if(mUser == null || !mUser.mId.equals(comment.mCreator.mId)) {
            Toast.makeText(mContext, "Bạn không có quyền chỉnh sửa bình luận này.", Toast.LENGTH_SHORT).show();
            return;
        }
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
        inputDialog.show(getSupportFragmentManager(), "Input Dialog");
    }

    private void editComment(String content, final Comment comment) {
        final int position = mAdapter.getData().indexOf(comment);
        showLoading();
        ServiceManager.getInstance().getPostService().editComment(mPostId, comment.mId, content).enqueue(new RestCallback<Comment>() {
            @Override
            public void onSuccess(String message, Comment comment) {
                hideLoading();
                mAdapter.remove(position);
                mAdapter.addData(position, comment);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onDeleteComment(final Comment comment) {
        showLoading();
        ServiceManager.getInstance().getPostService().deleteComment(mPostId, comment.mId).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                EventBus.getDefault().post(new EventUpdatePost());
                mAdapter.getData().remove(comment);
                mAdapter.setNewData(mAdapter.getData());
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createComment(String content) {
        showLoading();
        ServiceManager.getInstance().getPostService().createComment(mPostId, content).enqueue(new RestCallback<Comment>() {
            @Override
            public void onSuccess(String message, Comment comment) {
                hideLoading();
                EventBus.getDefault().post(new EventUpdatePost());
                mAdapter.addData(comment);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
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
}
