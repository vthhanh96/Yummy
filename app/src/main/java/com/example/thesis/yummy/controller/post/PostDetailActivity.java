package com.example.thesis.yummy.controller.post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.profile.ProfileActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Category;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.DateUtils;
import com.example.thesis.yummy.view.TopBarView;
import com.example.thesis.yummy.view.dialog.CustomProgressDialog;
import com.example.thesis.yummy.view.dialog.InputDialog;
import com.example.thesis.yummy.view.dialog.QuestionDialog;
import com.example.thesis.yummy.view.dialog.SelectCommentOptionsDialogFragment;
import com.example.thesis.yummy.view.dialog.SelectPostOptionsDialogFragment;
import com.example.thesis.yummy.view.dialog.listener.CustomDialogActionListener;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Hong Hanh on 5/31/2018.
 */

public class PostDetailActivity extends BaseActivity {

    private static final String ARG_KEY_POST_ID = "ARG_KEY_POST_ID";

    public static void start(Context context, Integer postId) {
        Intent starter = new Intent(context, PostDetailActivity.class);
        starter.putExtra(ARG_KEY_POST_ID, postId);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBar;
    @BindView(R.id.rcvComments) RecyclerView mCommentRecycler;
    @BindView(R.id.imgAvatar) ImageView mImgAvatar;
    @BindView(R.id.tvTime) TextView mTvTime;
    @BindView(R.id.tvTimeCreated) TextView mTvTimeCreated;
    @BindView(R.id.tvName) TextView mTvName;
    @BindView(R.id.rcvCategories) RecyclerView mCategoryRecycler;
    @BindView(R.id.tvAmount) TextView mTvAmount;
    @BindView(R.id.tvPlace) TextView mTvPlace;
    @BindView(R.id.tvContent) TextView mTvContent;
    @BindView(R.id.imgInterested) ImageView mImgInterested;
    @BindView(R.id.txtInterested) TextView mTvInterested;
    @BindView(R.id.txtComment) TextView mTvComment;

    private Post mPost;
    private String mToken;
    private User mUser;
    private Context mContext;
    private CommentAdapter mAdapter;
    private Integer mPostId;
    private CustomProgressDialog mProgressDialog;

    @OnClick(R.id.btnMenuPost)
    public void onMenuButtonClicked() {
        openOptionsPopup(mPost);
    }

    @OnClick(R.id.loInterest)
    public void onInterestedLayoutClicked() {
        interested();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mContext = this;
        init();
    }

    private void init() {
        initTopBar();
        getUser();
        getExtras();
        initRecyclerView();
    }

    private void initTopBar() {
        mTopBar.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBar.setImageViewRight(R.drawable.ic_comment);
        mTopBar.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }

            @Override
            public void onRightClick() {
                openInputDialog();
            }
        });
    }

    private void getUser() {
        mUser = StorageManager.getUser();
    }

    private void getExtras() {
        mPostId = getIntent().getIntExtra(ARG_KEY_POST_ID, -1);
        getPostInfo();
    }

    private void initRecyclerView() {
        mAdapter = new CommentAdapter();
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (mAdapter.getData().get(position) == null) return false;
                showCommentActionPopup(mAdapter.getData().get(position));
                return false;
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(view.getId() == R.id.imgAvatar) {
//                    ProfileDetailActivity.start(mContext, mAdapter.getData().get(position).getCreator().mId);
                }
            }
        });
        mCommentRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mCommentRecycler.setAdapter(mAdapter);
        mCommentRecycler.setNestedScrollingEnabled(false);
    }

    private void getPostInfo() {
        ServiceManager.getInstance().getPostService().getPostDetail(mPostId).enqueue(new RestCallback<Post>() {
            @Override
            public void onSuccess(String message, Post post) {
                if(post == null) return;
                mPost = post;
                onGetPostSuccess();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onGetPostSuccess() {
        if(mPost.mCreator != null) {
            Glide.with(mContext.getApplicationContext()).load(mPost.mCreator.mAvatar).apply(RequestOptions.circleCropTransform()).into(mImgAvatar);
            mTvName.setText(mPost.mCreator.mFullName);
        }
//        mTvTimeCreated.setText(DateUtils.getTimeAgo(mContext, mPost.getCreatedDate()));

        if(mPost.mCategories != null && !mPost.mCategories.isEmpty()) {
            FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
            flowLayoutManager.setAutoMeasureEnabled(true);

            mCategoryRecycler.setVisibility(VISIBLE);
            mCategoryRecycler.setAdapter(new CategoryAdapter(mPost.mCategories));
            mCategoryRecycler.setLayoutManager(flowLayoutManager);
        } else {
            mCategoryRecycler.setVisibility(GONE);
        }

        mTvAmount.setText(getString(R.string.post_amount, mPost.mAmount));
        mTvPlace.setText(mPost.mPlace);
        mTvContent.setText(mPost.mContent);

        mImgInterested.setImageResource(isInterested(mPost.mInterestedPeople) ? R.drawable.ic_interested_color : R.drawable.ic_interested);

        if(mPost.mInterestedPeople != null) {
            mTvInterested.setText(mContext.getString(R.string.interested, mPost.mInterestedPeople.size()));
        } else {
            mTvInterested.setText(mContext.getString(R.string.interested, 0));
        }

        if(mPost.mComments != null) {
            mTvComment.setText(mContext.getString(R.string.comment_amount, mPost.mComments.size()));
        } else {
            mTvComment.setText(mContext.getString(R.string.comment_amount, 0));
        }

        if(mPost.mTime != null) {
            mTvTime.setText(DateFormat.format("dd/MM/yyyy hh:mm", mPost.mTime));
        }

        mAdapter.setNewData(mPost.mComments);
    }

    private boolean isInterested(List<User> users) {
        if(mUser == null || users == null || users.isEmpty()) return false;
        for (User user : users) {
            if(user.mId.equals(mUser.mId)) {
                return true;
            }
        }
        return false;
    }

    private void openOptionsPopup(final Post post) {
        boolean isCreator = !(mUser == null || !mUser.mId.equals(post.mCreator.mId));
        SelectPostOptionsDialogFragment dialogFragment = SelectPostOptionsDialogFragment.getNewInstance(isCreator);
        dialogFragment.setPostOptionsListener(new SelectPostOptionsDialogFragment.SelectPostOptionsListener() {
            @Override
            public void createMeeting(boolean isCreator) {
                ListPeopleInterestedPostActivity.start(mContext, mPostId, isCreator);
            }

            @Override
            public void editPost() {
                EditPostActivity.start(PostDetailActivity.this, post);
            }

            @Override
            public void deletePost() {
                showDialogConfirmDeletePost(post);
            }

            @Override
            public void judgePost() {

            }

            @Override
            public void reportPost() {

            }
        });
        dialogFragment.show(getSupportFragmentManager(), "");
    }

    private void showDialogConfirmDeletePost(final Post post) {
        final QuestionDialog questionDialog = new QuestionDialog("Bạn có chắc chắn muốn xóa bài viết này?");
        questionDialog.setDialogActionListener(new CustomDialogActionListener() {
            @Override
            public void dialogCancel() {
                questionDialog.dismissDialog();
            }

            @Override
            public void dialogPerformAction() {
                questionDialog.dismissDialog();
                deletePost();
            }
        });
        questionDialog.show(getSupportFragmentManager(), "");
    }

    private void deletePost() {
        showLoading();
        ServiceManager.getInstance().getPostService().deletePost(mPostId).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                finish();
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void interested() {
        ServiceManager.getInstance().getPostService().interested(mPostId).enqueue(new RestCallback<Post>() {
            @Override
            public void onSuccess(String message, Post post) {
                mImgInterested.setImageResource(isInterested(post.mInterestedPeople) ? R.drawable.ic_interested_color : R.drawable.ic_interested);
                if(post.mInterestedPeople != null) {
                    mTvInterested.setText(mContext.getString(R.string.interested, post.mInterestedPeople.size()));
                } else {
                    mTvInterested.setText(mContext.getString(R.string.interested, 0));
                }
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void openInputDialog() {
        InputDialog inputDialog = new InputDialog(mContext);
        inputDialog.setListener(new InputDialog.InputDialogListener() {
            @Override
            public void onCancelClick() {

            }

            @Override
            public void onDoneClick(String content) {
                createComment(content);
            }
        });
        inputDialog.show();
    }

    private void showCommentActionPopup(final Comment comment) {
        if(mUser != null && mUser.mId == comment.mCreator.mId) {
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
            dialogFragment.show(getSupportFragmentManager(), "");
        }
    }

    private void showEditCommentDialog(final Comment comment) {
        InputDialog inputDialog = new InputDialog(mContext);
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
        inputDialog.show();
    }

    private void editComment(String content, final Comment comment) {
        showLoading();
        ServiceManager.getInstance().getPostService().editComment(mPostId, comment.mId, content).enqueue(new RestCallback<Comment>() {
            @Override
            public void onSuccess(String message, Comment comment) {
                hideLoading();
                int position = mAdapter.getData().indexOf(comment);
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
                mAdapter.remove(mAdapter.getData().indexOf(comment));
                mTvComment.setText(getString(R.string.comment_amount, mAdapter.getData().size()));
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
                mAdapter.addData(comment);
                mTvComment.setText(getString(R.string.comment_amount, mAdapter.getData().size()));
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class CategoryAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {

        public CategoryAdapter(List<Category> categories) {
            super(R.layout.layout_category_item, categories);
        }

        @Override
        protected void convert(BaseViewHolder helper, Category item) {
            if(item == null) return;
            helper.setText(R.id.txtCategoryName, item.mName);
        }
    }

    private class CommentAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> {

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