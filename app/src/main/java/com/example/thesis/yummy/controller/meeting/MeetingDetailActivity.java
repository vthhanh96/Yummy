package com.example.thesis.yummy.controller.meeting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Comment;
import com.example.thesis.yummy.restful.request.MeetingRequest;
import com.example.thesis.yummy.view.TopBarView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeetingDetailActivity extends BaseActivity implements MeetingDetailListener {

    private static final String ARG_KEY_MEETING_ID = "ARG_KEY_MEETING_ID";

    public static void start(Context context, int meetingID) {
        Intent starter = new Intent(context, MeetingDetailActivity.class);
        starter.putExtra(ARG_KEY_MEETING_ID, meetingID);
        context.startActivity(starter);
    }

    public static Intent getMeetingDetailIntent(Context context, int meetingID) {
        Intent intent = new Intent(context, MeetingDetailActivity.class);
        intent.putExtra(ARG_KEY_MEETING_ID, meetingID);
        return intent;
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.tabLayout) TabLayout mTabLayout;
    @BindView(R.id.viewPager) ViewPager mViewPager;

    private int mMeetingID;
    private MeetingCommentFragment mMeetingCommentFragment;

    @Override
    public void onCreateComment(String content) {
        showLoading();
        MeetingRequest.createComment(mMeetingID, content, new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                mMeetingCommentFragment.getMeetingComments();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(MeetingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUpdateComment(String content, Comment comment) {
        showLoading();
        ServiceManager.getInstance().getMeetingService().updateComment(comment.mId, content).enqueue(new RestCallback<Comment>() {
            @Override
            public void onSuccess(String message, Comment comment) {
                hideLoading();
                mMeetingCommentFragment.getMeetingComments();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(MeetingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteComment(Comment comment) {
        showLoading();
        ServiceManager.getInstance().getMeetingService().deleteComment(comment.mId).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                mMeetingCommentFragment.getMeetingComments();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(MeetingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meeting_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        getExtras();
        initTopBar();
        initViewPager();
        initTabLayout();
    }

    private void getExtras() {
        mMeetingID = getIntent().getIntExtra(ARG_KEY_MEETING_ID, -1);
    }

    private void initTopBar() {
        mTopBarView.setTitle(getString(R.string.meeting_detail));
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

    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(MeetingInfoFragment.newInstance(mMeetingID));

        mMeetingCommentFragment = MeetingCommentFragment.newInstance(mMeetingID);
        mMeetingCommentFragment.setMeetingDetailListener(this);
        fragments.add(mMeetingCommentFragment);

        mViewPager.setAdapter(new MeetingViewPagerAdapter(getSupportFragmentManager(), fragments));
        mViewPager.setOffscreenPageLimit(2);
    }

    private void initTabLayout() {
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private class MeetingViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public MeetingViewPagerAdapter(FragmentManager fm, @NonNull List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).toString();
        }
    }
}
