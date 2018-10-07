package com.example.thesis.yummy.controller.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.TopBarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileHistoryActivity extends BaseActivity {

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.rcvHistory) RecyclerView mHistoryRecyclerView;

    private TimelineAdapter mAdapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, ProfileHistoryActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_history;
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
        mTopBarView.setTitle(getString(R.string.history));
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
        mAdapter = new TimelineAdapter();

        List<User> users = new ArrayList<>();
        User user = StorageManager.getUser();
        users.add(user);
        users.add(user);
        users.add(user);

        Calendar calendar = Calendar.getInstance();

        mAdapter.addData(new HistoryItem(calendar.getTime(), users, "Nha Hang Huong Cau", 4.0f));
        mAdapter.addData(new HistoryItem(calendar.getTime(), users, "Nha Hang Huong Cau", 4.0f));
        mAdapter.addData(new HistoryItem(calendar.getTime(), users, "Nha Hang Huong Cau", 4.0f));
        mAdapter.addData(new HistoryItem(calendar.getTime(), users, "Nha Hang Huong Cau", 4.0f));
        mAdapter.addData(new HistoryItem(calendar.getTime(), users, "Nha Hang Huong Cau", 4.0f));
        mAdapter.addData(new HistoryItem(calendar.getTime(), users, "Nha Hang Huong Cau", 4.0f));
        mAdapter.addData(new HistoryItem(calendar.getTime(), users, "Nha Hang Huong Cau", 4.0f));

        mHistoryRecyclerView.setAdapter(mAdapter);
        mHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private class TimelineAdapter extends BaseQuickAdapter<HistoryItem, BaseViewHolder> {

        public TimelineAdapter() {
            super(R.layout.item_history, new ArrayList<HistoryItem>());
        }

        @Override
        protected void convert(BaseViewHolder helper, HistoryItem item) {
            if(item == null) return;
            helper.setText(R.id.dateTextView, DateFormat.format("dd MMM yyyy", item.mTime));
            helper.setText(R.id.placeTextView, item.mPlace);

            RatingBar ratingBar = helper.getView(R.id.ratingBar);
            ratingBar.setRating(item.mRating);

            RecyclerView recyclerView = helper.getView(R.id.rcvPeople);
            PeopleAdapter adapter = new PeopleAdapter();
            adapter.addData(item.mUsers);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private class PeopleAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

        public PeopleAdapter() {
            super(R.layout.item_people_join_in, new ArrayList<User>());
        }

        @Override
        protected void convert(BaseViewHolder helper, User item) {
            if(item != null) {
                if(TextUtils.isEmpty(item.mAvatar)) {
                    helper.setImageResource(R.id.avatarImageView, R.drawable.ic_default_avatar);
                } else {
                    ImageView avatarImageView = helper.getView(R.id.avatarImageView);
                    Glide.with(mContext).load(item.mAvatar).apply(RequestOptions.circleCropTransform()).into(avatarImageView);
                }
            }
        }
    }
}
