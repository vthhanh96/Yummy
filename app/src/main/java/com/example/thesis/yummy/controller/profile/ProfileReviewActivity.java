package com.example.thesis.yummy.controller.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.view.TopBarView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ProfileReviewActivity extends BaseActivity {

    @BindView(R.id.topBar) TopBarView mTopBar;
    @BindView(R.id.rcvCharacters) RecyclerView mCharacterRecyclerView;
    @BindView(R.id.rcvComments) RecyclerView mCommentRecyclerView;

    private CharacteristicAdapter mCharacterAdapter;
    private CommentAdapter mCommentAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, ProfileReviewActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_review;
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
        mTopBar.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBar.setTitle(getString(R.string.review));
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
        mCharacterAdapter = new CharacteristicAdapter();
        mCharacterAdapter.addData(new CharacterItem("Vui vẻ", 4.5f));
        mCharacterAdapter.addData(new CharacterItem("Hòa đồng", 3.5f));
        mCharacterAdapter.addData(new CharacterItem("Thân thiện", 3.0f));

        mCharacterRecyclerView.setAdapter(mCharacterAdapter);
        mCharacterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCharacterRecyclerView.setNestedScrollingEnabled(false);

        mCommentAdapter = new CommentAdapter();
        mCommentAdapter.addData(new Comment());
        mCommentAdapter.addData(new Comment());
        mCommentAdapter.addData(new Comment());
        mCommentAdapter.addData(new Comment());

        mCommentRecyclerView.setAdapter(mCommentAdapter);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCommentRecyclerView.setNestedScrollingEnabled(false);
    }

    private class CharacteristicAdapter extends BaseQuickAdapter<CharacterItem, BaseViewHolder> {

        public CharacteristicAdapter() {
            super(R.layout.layout_character_item, new ArrayList<CharacterItem>());
        }

        @Override
        protected void convert(BaseViewHolder helper, CharacterItem item) {
            helper.setText(R.id.txtCharacterName, item.mName);
            MaterialRatingBar materialRatingBar = helper.getView(R.id.ratingBar);
            materialRatingBar.setRating(item.mRating);
        }
    }

    private class CommentAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> {

        public CommentAdapter() {
            super(R.layout.item_comment_layout, new ArrayList<Comment>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Comment item) {

        }
    }
}
