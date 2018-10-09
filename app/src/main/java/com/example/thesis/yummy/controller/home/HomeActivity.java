package com.example.thesis.yummy.controller.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.controller.post.AddPostActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.socket.SocketManager;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.PostRecyclerView;
import com.example.thesis.yummy.view.TopBarView;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.thesis.yummy.AppConstants.NAV_DRAWER_ID_HOME_PAGE;
import static com.example.thesis.yummy.AppConstants.SOCKET_BASE_URL;

public class HomeActivity extends DrawerActivity {

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.rcvPosts) PostRecyclerView mPostRecyclerView;
    @BindView(R.id.refreshLayout) SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.btnCreatePost) FloatingActionButton mCreatePostButton;

    private int mPageNumber = 0;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(SOCKET_BASE_URL);
        } catch (URISyntaxException e) {}
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, HomeActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getNavId() {
        return NAV_DRAWER_ID_HOME_PAGE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @OnClick(R.id.btnCreatePost)
    public void createPost() {
        AddPostActivity.start(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        initSocket();
        initTopBar();
        initRecyclerView();
        initRefreshLayout();
        getPosts();
    }

    private void initSocket() {

    }

    private void initTopBar() {
        mTopBarView.setImageViewLeft(TopBarView.LEFT_MENU);
        mTopBarView.setTitle(getString(R.string.app_name));
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
        mPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy<0 && !mCreatePostButton.isShown())
                    mCreatePostButton.show();
                else if(dy>0 && mCreatePostButton.isShown())
                    mCreatePostButton.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        mPostRecyclerView.setLoadMoreEnable(new PostRecyclerView.OnPostRecyclerViewLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getPosts();
            }
        });
    }

    private void initRefreshLayout() {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageNumber = 0;
                mPostRecyclerView.setNewData(new ArrayList<Post>());
                getPosts();
            }
        });
    }

    private void getPosts() {
        ServiceManager.getInstance().getPostService().getAllPost(mPageNumber).enqueue(new RestCallback<List<Post>>() {
            @Override
            public void onSuccess(String message, List<Post> posts) {
                mRefreshLayout.setRefreshing(false);
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
                mRefreshLayout.setRefreshing(false);
                mPostRecyclerView.loadMoreFail();
            }
        });
    }
}
