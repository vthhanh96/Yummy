package com.example.thesis.yummy.controller.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.PostRecyclerView;
import com.example.thesis.yummy.view.TopBarView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfilePostActivity extends BaseActivity {

    private static final String ARG_KEY_USER_ID = "ARG_KEY_USER_ID";

    @BindView(R.id.topBar) TopBarView mTopBar;
    @BindView(R.id.rcvPosts) PostRecyclerView mPostRecyclerView;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;

    private int mPageNumber = 0;
    private int mUserId;

    public static void start(Context context, int userId) {
        Intent starter = new Intent(context, ProfilePostActivity.class);
        starter.putExtra(ARG_KEY_USER_ID, userId);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_post;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        getExtras();
        initTopBar();
        iniRefreshLayout();
        initRecyclerView();
        getMyPost();
    }

    private void getExtras() {
        mUserId = getIntent().getIntExtra(ARG_KEY_USER_ID, -1);
    }

    private void initTopBar() {
        mTopBar.setTitle("Post");
        mTopBar.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBar.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
    }

    private void iniRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                mPostRecyclerView.setNewData(null);
                mPageNumber = 0;
                getMyPost();
            }
        });
    }

    private void initRecyclerView() {
        mPostRecyclerView.setLoadMoreEnable(new PostRecyclerView.OnPostRecyclerViewLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getMyPost();
            }
        });
    }

    private void getMyPost() {
        mPostRecyclerView.showShimmer();
        ServiceManager.getInstance().getUserService().getListPostOfUser(mUserId, mPageNumber).enqueue(new RestCallback<List<Post>>() {
            @Override
            public void onSuccess(String message, List<Post> posts) {
                mPostRecyclerView.hideShimmer();
                if(posts == null || posts.isEmpty()) {
                    mPostRecyclerView.loadMoreEnd();
                    return;
                }
                mPageNumber++;
                mPostRecyclerView.loadMoreComplete();
                mPostRecyclerView.addData(posts);
            }

            @Override
            public void onFailure(String message) {
                mPostRecyclerView.hideShimmer();
                mPostRecyclerView.loadMoreFail();
            }
        });
    }
}
