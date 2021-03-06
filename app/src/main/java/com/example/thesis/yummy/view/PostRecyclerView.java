package com.example.thesis.yummy.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.example.thesis.yummy.controller.home.MapActivity;
import com.example.thesis.yummy.controller.post.CommentActivity;
import com.example.thesis.yummy.controller.post.EditPostActivity;
import com.example.thesis.yummy.controller.post.ListPeopleInterestedPostActivity;
import com.example.thesis.yummy.controller.post.PostDetailActivity;
import com.example.thesis.yummy.controller.profile.ProfileActivity;
import com.example.thesis.yummy.controller.shared.EmptyLayout;
import com.example.thesis.yummy.eventbus.EventInterestedPost;
import com.example.thesis.yummy.eventbus.EventUpdatePost;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Category;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.utils.DateUtils;
import com.example.thesis.yummy.view.dialog.QuestionDialog;
import com.example.thesis.yummy.view.dialog.SelectPostOptionsDialogFragment;
import com.example.thesis.yummy.view.dialog.listener.CustomDialogActionListener;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

public class PostRecyclerView extends RecyclerView {

    private PostAdapter mPostAdapter;
    private User mUser;
    private ShimmerFrameLayout mShimmerView;

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
        mShimmerView = (ShimmerFrameLayout) View.inflate(getContext(), R.layout.shimmer_post_layout, null);

        mPostAdapter = new PostAdapter();
        mPostAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Post post = (Post) adapter.getItem(position);
                if (post == null) return;
                PostDetailActivity.start(getContext(), post.mId);
            }
        });

        EmptyLayout emptyLayout = new EmptyLayout(getContext());
        emptyLayout.setEmptyImageMessage(getContext().getString(R.string.empty_post));
        emptyLayout.setEmptyImageResource(R.drawable.ic_empty_post);
//        mPostAdapter.setEmptyView(emptyLayout);

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
                        ProfileActivity.start(getContext(), post.mCreator.mId);
                        break;
                    case R.id.placeLayout:
                        if(post.mLocation == null || post.mLocation.size() < 2) return;
                        MapActivity.start(getContext(), post.mLocation.get(1), post.mLocation.get(0), post.mPlace);
                        break;
                    case R.id.registerAmountTextView:
                        boolean isCanCreateMeeting = !(mUser == null || !mUser.mId.equals(post.mCreator.mId)) && post.mIsActive;
                        ListPeopleInterestedPostActivity.start(getContext(), post.mId, isCanCreateMeeting);
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
                EventBus.getDefault().post(new EventUpdatePost());
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
            public void createMeeting(boolean isCreator) {
                ListPeopleInterestedPostActivity.start(getContext(), post.mId, isCreator);
            }

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

    public void showShimmer() {
        if(mShimmerView != null) {
            mShimmerView.startShimmer();
            mPostAdapter.addFooterView(mShimmerView);
        }
    }

    public void hideShimmer() {
        if(mShimmerView != null) {
            mShimmerView.stopShimmer();
            mPostAdapter.removeFooterView(mShimmerView);
        }
    }

    public interface OnPostRecyclerViewLoadMoreListener {
        void onLoadMore();
    }

    private class PostAdapter extends BaseQuickAdapter<Post, BaseViewHolder> {

        public PostAdapter() {
            super(R.layout.item_post_layout, new ArrayList<Post>());
        }

        @Override
        protected void convert(final BaseViewHolder helper, Post item) {
            if (item == null) return;

            if (item.mCreator != null) {
                ImageView imgAvatar = helper.getView(R.id.imgAvatar);
                if(TextUtils.isEmpty(item.mCreator.mAvatar)) {
                    imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                } else {
                    Glide.with(mContext.getApplicationContext()).load(item.mCreator.mAvatar).apply(RequestOptions.circleCropTransform()).into(imgAvatar);
                }
                helper.setText(R.id.txtName, item.mCreator.mFullName);
            }
            if(item.mTime != null) {
                if(item.mIsActive) {
                    helper.setText(R.id.tvTimeCreated, getContext().getString(R.string.time_remain, DateUtils.getTimeFuture(item.mTime)));
                } else {
                    helper.setText(R.id.tvTimeCreated, DateUtils.getTimeFuture(item.mTime));
                }
            }

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

            helper.setText(R.id.txtAmount, mContext.getString(R.string.post_amount, item.mAmount));
            helper.setText(R.id.txtPlace, item.mPlace);

            if (item.mTime != null) {
                helper.setText(R.id.txtTime, DateFormat.format("dd/MM/yyyy hh:mm aa", item.mTime));
            }

            if(TextUtils.isEmpty(item.mContent)) {
                helper.setGone(R.id.contentLayout, false);
            } else {
                helper.setGone(R.id.contentLayout, true);
                helper.setText(R.id.txtContent, item.mContent);
            }

            if (item.mInterestedPeople != null) {
                helper.setText(R.id.registerAmountTextView, mContext.getString(R.string.registered_amount, item.mInterestedPeople.size()));
            } else {
                helper.setText(R.id.registerAmountTextView, mContext.getString(R.string.registered_amount, 0));
            }
            helper.addOnClickListener(R.id.registerAmountTextView);

            if(item.mCreator.mId.equals(mUser.mId)) {
                helper.setGone(R.id.loInterest, false);
                helper.setVisible(R.id.btnMenuPost, item.mIsActive);
            } else {
                helper.setGone(R.id.loInterest, true);
                if(item.mIsActive) {
                    helper.addOnClickListener(R.id.loInterest);
                }

                if(isInterested(item.mInterestedPeople)) {
                    helper.setTextColor(R.id.txtInterested, ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    helper.setImageResource(R.id.imgInterested, R.drawable.ic_register_color);
                    helper.setText(R.id.txtInterested, R.string.registered);
                } else {
                    helper.setTextColor(R.id.txtInterested, ContextCompat.getColor(getContext(), R.color.grey));
                    helper.setImageResource(R.id.imgInterested, R.drawable.ic_register);
                    helper.setText(R.id.txtInterested, mContext.getString(R.string.register));
                }
                helper.setVisible(R.id.btnMenuPost, false);
            }

            if (item.mComments != null) {
                helper.setText(R.id.txtComment, mContext.getString(R.string.comment_amount, item.mComments.size()));
            } else {
                helper.setText(R.id.txtComment, mContext.getString(R.string.comment_amount, 0));
            }

            RichPreview richPreview = new RichPreview(new ResponseListener() {
                @Override
                public void onData(MetaData metaData) {
                    if(metaData == null) return;

                    LinkPreviewLayout linkPreviewLayout = helper.getView(R.id.linkPreviewLayout);
                    linkPreviewLayout.setVisibility(View.VISIBLE);
                    linkPreviewLayout.setMetaData(metaData);
                }

                @Override
                public void onError(Exception e) {

                }
            });
            if(!TextUtils.isEmpty(item.mLink)) {
                richPreview.getPreview(item.mLink);
            }

            if(TextUtils.isEmpty(item.mImage)) {
                helper.setGone(R.id.postImageView, false);
            } else {
                helper.setGone(R.id.postImageView, true);
                ImageView imageView = helper.getView(R.id.postImageView);
                Glide.with(mContext.getApplicationContext()).load(item.mImage).into(imageView);
            }

            helper.addOnClickListener(R.id.loComment);
            helper.addOnClickListener(R.id.btnMenuPost);
            helper.addOnClickListener(R.id.imgAvatar);
            helper.addOnClickListener(R.id.placeLayout);
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
