package com.example.thesis.yummy.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.post.CommentActivity;
import com.example.thesis.yummy.controller.post.EditPostActivity;
import com.example.thesis.yummy.controller.post.PostDetailActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.RestError;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Category;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.dialog.QuestionDialog;
import com.example.thesis.yummy.view.dialog.SelectPostOptionsDialogFragment;
import com.example.thesis.yummy.view.dialog.listener.CustomDialogActionListener;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class PostRecyclerView extends RecyclerView {

    private PostAdapter mPostAdapter;
    private User mUser;

    public PostRecyclerView(Context context) {
        this(context, null, 0);
    }

    public PostRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PostRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        getData();
        initRecyclerView();
    }

    private void getData() {
        mUser = StorageManager.getUser();
    }

    private void initRecyclerView() {
        mPostAdapter = new PostAdapter();
        mPostAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Post post = (Post) adapter.getItem(position);
                if (post == null) return;
                PostDetailActivity.start(getContext(), post.mId);
            }
        });

        mPostAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Post post = (Post) adapter.getItem(position);
                if (post == null) return;

                switch (view.getId()) {
                    case R.id.loInterest:
                        interested(post, position);
                        break;
                    case R.id.loComment:
                        CommentActivity.start(getContext(), post.mId);
                        break;
                    case R.id.btnMenuPost:
                        openOptionsPopup(post, view);
                        break;
                    case R.id.imgAvatar:
                        break;
                }
            }
        });

        this.setAdapter(mPostAdapter);
        this.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void interested(Post post, final int position) {
        ServiceManager.getInstance().getPostService().interested(post.mId).enqueue(new RestCallback<Post>() {
            @Override
            public void onSuccess(String message, Post post) {
                mPostAdapter.getData().get(position).mInterestedPeople = post.mInterestedPeople;
                mPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    public void setLoadMoreEnable(final OnPostRecyclerViewLoadMoreListener listener) {
        mPostAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (listener != null) {
                    listener.onLoadMore();
                }
            }
        }, this);
    }

    public void setNewData(List<Post> posts) {
        mPostAdapter.setNewData(posts);
    }

    public void addData(List<Post> posts) {
        mPostAdapter.addData(posts);
    }

    public void loadMoreFail() {
        mPostAdapter.loadMoreFail();
    }

    public void loadMoreComplete() {
        mPostAdapter.loadMoreComplete();
    }

    public void loadMoreEnd() {
        mPostAdapter.loadMoreEnd();
    }

    private boolean isInterested(List<User> users) {
        if (mUser == null || users == null || users.isEmpty()) return false;
        for (User user : users) {
            if (user.mId.equals(mUser.mId)) {
                return true;
            }
        }
        return false;
    }

    private void openOptionsPopup(final Post post, View view) {
        boolean isCreator = !(mUser == null || !mUser.mId.equals(post.mCreator.mId));
        SelectPostOptionsDialogFragment dialogFragment = SelectPostOptionsDialogFragment.getNewInstance(isCreator);
        dialogFragment.setPostOptionsListener(new SelectPostOptionsDialogFragment.SelectPostOptionsListener() {
            @Override
            public void editPost() {
                EditPostActivity.start(getContext(), post);
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
        dialogFragment.show(((FragmentActivity)getContext()).getSupportFragmentManager(), "");
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
                deletePost(post);
            }
        });
        questionDialog.show(((FragmentActivity)getContext()).getSupportFragmentManager(), "");
    }

    private void deletePost(final Post post) {
        ServiceManager.getInstance().getPostService().deletePost(post.mId).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                mPostAdapter.getData().remove(post);
                mPostAdapter.setNewData(mPostAdapter.getData());
                Toast.makeText(getContext(), R.string.delete_post_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnPostRecyclerViewLoadMoreListener {
        void onLoadMore();
    }

    private class PostAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {

        public PostAdapter() {
            super(R.layout.item_post_layout, new ArrayList<Post>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Post item) {
            if (item == null) return;

            if (item.mCreator != null) {
                ImageView imgAvatar = helper.getView(R.id.imgAvatar);
                Glide.with(mContext.getApplicationContext()).load(item.mCreator.mAvatar).apply(RequestOptions.circleCropTransform()).into(imgAvatar);
                helper.setText(R.id.txtName, item.mCreator.mFullName);
            }
//            helper.setText(R.id.tvTimeCreated, DateUtils.getTimeAgo(mContext, item.getCreatedDate()));

            RecyclerView rcvCategories = helper.getView(R.id.rcvCategories);
            if (item.mCategories != null && !item.mCategories.isEmpty()) {
                FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
                flowLayoutManager.setAutoMeasureEnabled(true);

                rcvCategories.setVisibility(VISIBLE);
                rcvCategories.setAdapter(new CategoryAdapter(item.mCategories));
                rcvCategories.setLayoutManager(flowLayoutManager);
            } else {
                rcvCategories.setVisibility(GONE);
            }

            helper.setText(R.id.txtAmount, String.valueOf(item.mAmount));
            helper.setText(R.id.txtPlace, item.mPlace);

            if (item.mTime != null) {
                helper.setText(R.id.txtTime, DateFormat.format("dd/MM/yyyy hh:mm", item.mTime));
            }

            helper.setText(R.id.txtContent, item.mContent);

            helper.setImageResource(R.id.imgInterested, isInterested(item.mInterestedPeople) ? R.drawable.ic_interested_color : R.drawable.ic_interested);

            if (item.mInterestedPeople != null) {
                helper.setText(R.id.txtInterested, mContext.getString(R.string.interested, item.mInterestedPeople.size()));
            } else {
                helper.setText(R.id.txtInterested, mContext.getString(R.string.interested, 0));
            }

            if (item.mComments != null) {
                helper.setText(R.id.txtComment, mContext.getString(R.string.comment_amount, item.mComments.size()));
            } else {
                helper.setText(R.id.txtComment, mContext.getString(R.string.comment_amount, 0));
            }

            helper.addOnClickListener(R.id.loInterest);
            helper.addOnClickListener(R.id.loComment);
            helper.addOnClickListener(R.id.btnMenuPost);
            helper.addOnClickListener(R.id.imgAvatar);
        }
    }

    private class CategoryAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {

        public CategoryAdapter(List<Category> categories) {
            super(R.layout.item_category_layout, categories);
        }

        @Override
        protected void convert(BaseViewHolder helper, Category item) {
            helper.setText(R.id.txtCategoryName, item.mName);
        }
    }
}
