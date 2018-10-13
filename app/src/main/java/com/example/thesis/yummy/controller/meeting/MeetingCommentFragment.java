package com.example.thesis.yummy.controller.meeting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.utils.DateUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingCommentFragment extends Fragment {

    @BindView(R.id.commentRecyclerView) RecyclerView mCommentRecyclerView;
    @BindView(R.id.createCommentButton) FloatingActionButton mCreateCommentButton;

    private CommentAdapter mCommentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_comment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        mCommentAdapter = new CommentAdapter();

        mCommentRecyclerView.setAdapter(mCommentAdapter);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mCommentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy<0 && !mCreateCommentButton.isShown())
                    mCreateCommentButton.show();
                else if(dy>0 && mCreateCommentButton.isShown())
                    mCreateCommentButton.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


    }

    public class CommentAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> {

        public CommentAdapter() {
            super(R.layout.item_comment_layout, new ArrayList<Comment>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Comment item) {
            helper.setText(R.id.tvName, item.mCreator.mFullName);
            helper.setText(R.id.tvTime, DateUtils.getTimeAgo(mContext, item.mCreatedDate));
            helper.setText(R.id.tvContent, item.mContent);

            ImageView imgAvatar = helper.getView(R.id.imgAvatar);
            Glide.with(mContext).load(item.mCreator.mAvatar).apply(RequestOptions.circleCropTransform()).into(imgAvatar);

            helper.addOnClickListener(R.id.imgAvatar);
        }
    }

    @Override
    public String toString() {
        return "Comment";
    }
}
