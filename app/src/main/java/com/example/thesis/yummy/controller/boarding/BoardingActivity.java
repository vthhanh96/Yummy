package com.example.thesis.yummy.controller.boarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class BoardingActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, BoardingActivity.class);
        context.startActivity(starter);
    }

    @BindView(R.id.viewPager) ViewPager mViewPager;
    @BindView(R.id.indicator) CircleIndicator mCircleIndicator;

    @OnClick(R.id.skipButton)
    public void skip() {
        LoginActivity.start(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_boarding;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initViewPager();
    }

    private void initViewPager() {
        BoardingFragmentPagerAdapter adapter = new BoardingFragmentPagerAdapter(getSupportFragmentManager());
        adapter.mFragments.add(BoardingFragment.newInstance(getString(R.string.boarding_title_1), getString(R.string.boarding_sub_1)));
        adapter.mFragments.add(BoardingFragment.newInstance(getString(R.string.boarding_title_2), getString(R.string.boarding_sub_2)));
        adapter.mFragments.add(BoardingFragment.newInstance(getString(R.string.boarding_title_3), getString(R.string.boarding_sub_3)));
        adapter.mFragments.add(BoardingFragment.newInstance(getString(R.string.boarding_title_4), getString(R.string.boarding_sub_4)));
        adapter.mFragments.add(BoardingFragment.newInstance(getString(R.string.boarding_title_5), getString(R.string.boarding_sub_5)));

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(adapter.mFragments.size());
        mCircleIndicator.setViewPager(mViewPager);
    }

    private class BoardingFragmentPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> mFragments = new ArrayList<>();

        public BoardingFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
