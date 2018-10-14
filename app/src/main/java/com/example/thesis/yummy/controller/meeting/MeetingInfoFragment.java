package com.example.thesis.yummy.controller.meeting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thesis.yummy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingInfoFragment extends Fragment {

    @BindView(R.id.placeTextView) TextView mPlaceTextView;
    @BindView(R.id.timeTextView) TextView mTimeTextView;
    @BindView(R.id.joinedPeopleRecyclerView) RecyclerView mPeopleRecyclerView;

    private int mMeetingID;

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

    }

    @Override
    public String toString() {
        return "Information";
    }
}
