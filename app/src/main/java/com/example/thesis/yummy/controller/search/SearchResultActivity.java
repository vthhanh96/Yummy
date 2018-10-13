package com.example.thesis.yummy.controller.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.view.TopBarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchResultActivity extends BaseActivity {

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.resultRecyclerView) RecyclerView mResultRecyclerView;

    private SearchResultAdapter mAdapter;
    private List<User> mSelectedUsers = new ArrayList<>();

    @OnClick(R.id.sendRequestButton)
    public void sendRequest() {
        showConfirmDialog();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_result;
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
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.suggestions));
        mTopBarView.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
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
        mAdapter = new SearchResultAdapter();

        mResultRecyclerView.setAdapter(mAdapter);
        mResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showConfirmDialog() {

    }

    private class SearchResultAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

        public SearchResultAdapter() {
            super(R.layout.item_search_result_layout, new ArrayList<User>());
        }

        @Override
        protected void convert(BaseViewHolder helper, User item) {
            if(item == null) return;
            ImageView imageView = helper.getView(R.id.avatarImageView);
            Glide.with(mContext.getApplicationContext()).load(item.mAvatar).into(imageView);

            helper.setText(R.id.nameTextView, item.mFullName);
            helper.setVisible(R.id.checkedImageView, item.mIsSelected);
        }
    }
}
