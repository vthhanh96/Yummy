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
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Post;
import com.example.thesis.yummy.view.PostRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListPostFragment extends Fragment {

    @BindView(R.id.rcvPosts) PostRecyclerView mPostRecyclerView;
    @BindView(R.id.btnCreatePost) FloatingActionButton mCreatePostButton;
    @BindView(R.id.refreshLayout) SwipeRefreshLayout mRefreshLayout;

    private int mPageNumber = 0;


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

    private void init() {
        initRecyclerView();
        initRefreshLayout();
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
