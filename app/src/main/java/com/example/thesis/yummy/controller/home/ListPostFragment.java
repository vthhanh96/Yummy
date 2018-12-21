package com.example.thesis.yummy.controller.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.post.AddPostActivity;
import com.example.thesis.yummy.eventbus.EventInterestedPost;
import com.example.thesis.yummy.eventbus.EventUpdatePost;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.restful.request.PostRequest;
import com.example.thesis.yummy.view.PostRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListPostFragment extends Fragment {

    public static ListPostFragment newInstance(boolean isInterested) {
        ListPostFragment fragment = new ListPostFragment();
        fragment.mIsInterested = isInterested;
        return fragment;
    }

    @BindView(R.id.rcvPosts) PostRecyclerView mPostRecyclerView;
    @BindView(R.id.btnCreatePost) FloatingActionButton mCreatePostButton;
    @BindView(R.id.refreshLayout) SwipeRefreshLayout mRefreshLayout;

    private int mPageNumber = 0;
    private boolean mIsInterested = false;
    private boolean mIsLoaded = false;

    @OnClick(R.id.btnCreatePost)
    public void createPost() {
        AddPostActivity.start(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_post, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && !mIsLoaded && mIsInterested) {
            mIsLoaded = true;
            getPosts();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        initRecyclerView();
        initRefreshLayout();
        EventBus.getDefault().register(this);
        if(!mIsInterested) getPosts();
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

    private void getPosts() {
        mPostRecyclerView.showShimmer();
        PostRequest.getListPost(mPageNumber, mIsInterested, new RestCallback<List<Post>>() {
            @Override
            public void onSuccess(String message, List<Post> posts) {
                mPostRecyclerView.hideShimmer();
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
                mPostRecyclerView.hideShimmer();
                mRefreshLayout.setRefreshing(false);
                mPostRecyclerView.loadMoreFail();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResetListPost(EventInterestedPost eventInterestedPost) {
        if(eventInterestedPost.isInterested == mIsInterested) {
            mPageNumber = 0;
            mPostRecyclerView.setNewData(new ArrayList<Post>());
            getPosts();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventUpdatePost(EventUpdatePost eventUpdatePost) {
        mPageNumber = 0;
        mPostRecyclerView.setNewData(new ArrayList<Post>());
        getPosts();
    }
}
