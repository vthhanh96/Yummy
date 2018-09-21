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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileHistoryFragment extends Fragment {

    @BindView(R.id.rcvHistory) RecyclerView mHistoryRecyclerView;

    private TimelineAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_history, container, false);
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
        mAdapter = new TimelineAdapter();
        mAdapter.addData(new HistoryItem());
        mAdapter.addData(new HistoryItem());
        mAdapter.addData(new HistoryItem());
        mAdapter.addData(new HistoryItem());
        mAdapter.addData(new HistoryItem());
        mAdapter.addData(new HistoryItem());
        mAdapter.addData(new HistoryItem());

        mHistoryRecyclerView.setAdapter(mAdapter);
        mHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private class TimelineAdapter extends BaseQuickAdapter<HistoryItem, BaseViewHolder>{

        public TimelineAdapter() {
            super(R.layout.item_history, new ArrayList<HistoryItem>());
        }

        @Override
        protected void convert(BaseViewHolder helper, HistoryItem item) {

        }
    }
}
