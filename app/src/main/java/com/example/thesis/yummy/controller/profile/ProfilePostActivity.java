package com.example.thesis.yummy.controller.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

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

    @BindView(R.id.topBar) TopBarView mTopBar;
    @BindView(R.id.rcvPosts) PostRecyclerView mPostRecyclerView;
    private int mPageNumber = 0;

    public static void start(Context context) {
        Intent starter = new Intent(context, ProfilePostActivity.class);
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
        initTopBar();
        initRecyclerView();
        getMyPost();
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

    private void initRecyclerView() {
        mPostRecyclerView.setLoadMoreEnable(new PostRecyclerView.OnPostRecyclerViewLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getMyPost();
            }
        });

    }

    private void getMyPost() {
        User user = StorageManager.getUser();
        if(user == null) return;
        ServiceManager.getInstance().getUserService().getListPostOfUser(user.mId, mPageNumber).enqueue(new RestCallback<List<Post>>() {
            @Override
            public void onSuccess(String message, List<Post> posts) {
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
                mPostRecyclerView.loadMoreFail();
            }
        });
    }
}
