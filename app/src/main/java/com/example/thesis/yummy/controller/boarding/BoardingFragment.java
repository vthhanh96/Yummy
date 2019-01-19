package com.example.thesis.yummy.controller.boarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thesis.yummy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BoardingFragment extends Fragment {

    @BindView(R.id.titleTextView) TextView mTitleTextView;
    @BindView(R.id.subTextView) TextView mSubTextView;

    private String mTitle;
    private String mSub;

    public static BoardingFragment newInstance(String title, String sub) {
        BoardingFragment fragment = new BoardingFragment();
        fragment.mTitle = title;
        fragment.mSub = sub;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boarding, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mTitleTextView.setText(mTitle);
        mSubTextView.setText(mSub);
    }
}
