package com.example.thesis.yummy.controller.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.restful.model.Comment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ProfileCharacteristicFragment extends Fragment {

    @BindView(R.id.rcvCharacteristics) RecyclerView mCharacteristicsRecyclerView;
    @BindView(R.id.rcvComments) RecyclerView mCommentRecyclerView;

    private CharacteristicAdapter mCharacterAdapter;
    private CommentAdapter mCommentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_characteristic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        mCharacterAdapter = new CharacteristicAdapter();
        mCharacterAdapter.addData(new CharacterItem("Vui vẻ", 4.5f));
        mCharacterAdapter.addData(new CharacterItem("Hòa đồng", 3.5f));
        mCharacterAdapter.addData(new CharacterItem("Thân thiện", 3.0f));

        mCharacteristicsRecyclerView.setAdapter(mCharacterAdapter);
        mCharacteristicsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mCharacteristicsRecyclerView.setNestedScrollingEnabled(false);

        mCommentAdapter = new CommentAdapter();
        mCommentAdapter.addData(new Comment());
        mCommentAdapter.addData(new Comment());
        mCommentAdapter.addData(new Comment());
        mCommentAdapter.addData(new Comment());

        mCommentRecyclerView.setAdapter(mCommentAdapter);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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
