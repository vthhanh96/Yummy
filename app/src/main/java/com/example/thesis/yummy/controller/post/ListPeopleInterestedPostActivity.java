package com.example.thesis.yummy.controller.post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Meeting;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.TopBarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

public class ListPeopleInterestedPostActivity extends BaseActivity {

    private static final String ARG_KEY_POST_ID = "ARG_KEY_POST_ID";
    private static final String ARG_KEY_IS_CREATOR = "ARG_KEY_IS_CREATOR";

    public static void start(Context context, Integer postId, Boolean isCreator) {
        Intent starter = new Intent(context, ListPeopleInterestedPostActivity.class);
        starter.putExtra(ARG_KEY_POST_ID, postId);
        starter.putExtra(ARG_KEY_IS_CREATOR, isCreator);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.peopleRecyclerView) RecyclerView mPeopleRecyclerView;
    @BindView(R.id.createMeetingButton) FancyButton mCreateMeetingButton;

    private UserAdapter mUserAdapter;
    private int mPostID;
    private boolean mIsCreator;

    @OnClick(R.id.createMeetingButton)
    public void createMeeting() {
        List<Integer> selectedUsers = new ArrayList<>();
        for (User user : mUserAdapter.getData()) {
            if(user.mIsSelected) {
                selectedUsers.add(user.mId);
            }
        }

        if(selectedUsers.isEmpty()) {
            Toast.makeText(this, R.string.select_user, Toast.LENGTH_SHORT).show();
            return;
        }

        selectedUsers.add(StorageManager.getUser().mId);
        showLoading();
        ServiceManager.getInstance().getMeetingService().createMeeting(mPostID, selectedUsers).enqueue(new RestCallback<Meeting>() {
            @Override
            public void onSuccess(String message, Meeting meeting) {
                hideLoading();
                finish();
                Toast.makeText(ListPeopleInterestedPostActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(ListPeopleInterestedPostActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_people_interested_post;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        getExtras();
        initToolbar();
        initRecyclerView();
        initData();
        getInterestedPeople();
    }

    private void getExtras() {
        mPostID = getIntent().getIntExtra(ARG_KEY_POST_ID, -1);
        mIsCreator = getIntent().getBooleanExtra(ARG_KEY_IS_CREATOR, false);
    }

    private void initToolbar() {
        mTopBarView.setTitle(getString(R.string.create_meeting));
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
        mUserAdapter = new UserAdapter();
        mUserAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                User item = mUserAdapter.getItem(position);
                if(item == null) return;

                item.mIsSelected = !item.mIsSelected;
                mUserAdapter.notifyDataSetChanged();
            }
        });

        mPeopleRecyclerView.setAdapter(mUserAdapter);
        mPeopleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        mCreateMeetingButton.setVisibility(mIsCreator ? View.VISIBLE : View.GONE);
    }

    private void getInterestedPeople() {
        ServiceManager.getInstance().getPostService().getInterestedPeople(mPostID).enqueue(new RestCallback<List<User>>() {
            @Override
            public void onSuccess(String message, List<User> users) {
                for (User user : users) {
                    if(user.mId.equals(StorageManager.getUser().mId)) {
                        users.remove(user);
                    }
                }
                mUserAdapter.setNewData(users);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(ListPeopleInterestedPostActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class UserAdapter extends BaseQuickAdapter<User, BaseViewHolder>{

        public UserAdapter() {
            super(R.layout.item_people_interested_layout, new ArrayList<User>());
        }

        @Override
        protected void convert(BaseViewHolder helper, User item) {
            if(item == null) return;

            helper.setText(R.id.nameTextView, item.mFullName);
            if(!TextUtils.isEmpty(item.mAvatar)) {
                ImageView imageView = helper.getView(R.id.avatarImageView);
                Glide.with(mContext.getApplicationContext()).load(item.mAvatar).apply(RequestOptions.circleCropTransform()).into(imageView);
            }

            helper.setVisible(R.id.checkbox, mIsCreator);

            helper.setChecked(R.id.checkbox, item.mIsSelected);
            helper.setText(R.id.trustPointTextView, getString(R.string.trust_point_amount, item.mTrustPoint));
        }
    }
}
