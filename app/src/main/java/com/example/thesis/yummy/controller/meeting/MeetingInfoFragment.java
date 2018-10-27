package com.example.thesis.yummy.controller.meeting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.Application;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Meeting;
import com.example.thesis.yummy.restful.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingInfoFragment extends Fragment {

    @BindView(R.id.placeTextView) TextView mPlaceTextView;
    @BindView(R.id.timeTextView) TextView mTimeTextView;
    @BindView(R.id.joinedPeopleRecyclerView) RecyclerView mPeopleRecyclerView;

    private int mMeetingID;
    private UserAdapter mUserAdapter;

    public static MeetingInfoFragment newInstance(int meetingID) {
        MeetingInfoFragment instance = new MeetingInfoFragment();
        instance.mMeetingID = meetingID;
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_info, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        initRecyclerView();
        getMeetingDetail();
    }

    private void initRecyclerView() {
        mUserAdapter = new UserAdapter();

        mPeopleRecyclerView.setAdapter(mUserAdapter);
        mPeopleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getMeetingDetail() {
        ServiceManager.getInstance().getMeetingService().getMeetingDetail(mMeetingID).enqueue(new RestCallback<Meeting>() {
            @Override
            public void onSuccess(String message, Meeting meeting) {
                onGetDataSuccess(meeting);
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }

    private void onGetDataSuccess(Meeting meeting) {
        if(meeting == null) return;

        mPlaceTextView.setText(meeting.mPlace);
        if(meeting.mTime != null) {
            mTimeTextView.setText(DateFormat.format("dd/MM/yyyy hh:mm", meeting.mTime));
        }

        mUserAdapter.setNewData(meeting.mJoinedPeople);
    }

    @Override
    public String toString() {
        return Application.mContext.getString(R.string.information);
    }

    private class UserAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

        public UserAdapter() {
            super(R.layout.item_joined_people_meeting_layout, new ArrayList<User>());
        }

        @Override
        protected void convert(BaseViewHolder helper, User item) {
            if(item == null) return;

            helper.setText(R.id.nameTextView, item.mFullName);
            if(item.mAvatar != null) {
                ImageView imageView = helper.getView(R.id.avatarImageView);
                Glide.with(mContext.getApplicationContext()).load(item.mAvatar).apply(RequestOptions.circleCropTransform()).into(imageView);
            }
        }
    }
}
