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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.Application;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.controller.home.MapActivity;
import com.example.thesis.yummy.controller.rating.RatingActivity;
import com.example.thesis.yummy.controller.search.SearchActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Meeting;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;
import com.example.thesis.yummy.view.dialog.QuestionDialog;
import com.example.thesis.yummy.view.dialog.ReasonDialog;
import com.example.thesis.yummy.view.dialog.listener.CustomDialogActionListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

public class MeetingInfoFragment extends Fragment {

    @BindView(R.id.placeTextView) TextView mPlaceTextView;
    @BindView(R.id.timeTextView) TextView mTimeTextView;
    @BindView(R.id.joinedPeopleRecyclerView) RecyclerView mPeopleRecyclerView;
    @BindView(R.id.ratingMeetingButton) FancyButton mRatingButton;
    @BindView(R.id.inviteUserButton) ImageButton mInviteUserButton;

    private int mMeetingID;
    private Meeting mMeeting;
    private UserAdapter mUserAdapter;

    public static MeetingInfoFragment newInstance(int meetingID) {
        MeetingInfoFragment instance = new MeetingInfoFragment();
        instance.mMeetingID = meetingID;
        return instance;
    }

    @OnClick(R.id.ratingMeetingButton)
    public void openRating() {
        RatingActivity.start(getContext(), mMeetingID);
    }

    @OnClick(R.id.inviteUserButton)
    public void inviteUser() {
        SearchActivity.start(getContext(), true, mMeetingID);
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
        checkCanRating();
    }

    private void initRecyclerView() {
        mUserAdapter = new UserAdapter();
        mUserAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                User item = mUserAdapter.getItem(position);
                if(item == null) return false;
                showConfirmDialog(item);
                return false;
            }
        });

        mPeopleRecyclerView.setAdapter(mUserAdapter);
        mPeopleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getMeetingDetail() {
        ServiceManager.getInstance().getMeetingService().getMeetingDetail(mMeetingID).enqueue(new RestCallback<Meeting>() {
            @Override
            public void onSuccess(String message, Meeting meeting) {
                mMeeting = meeting;
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
            mTimeTextView.setText(DateFormat.format("dd/MM/yyyy hh:mm aa", meeting.mTime));
        }

        mUserAdapter.setNewData(meeting.mJoinedPeople);

        User currentUser = StorageManager.getUser();
        if(currentUser == null) return;

        if(currentUser.mId.equals(mMeeting.mCreator.mId) && !meeting.mIsFinished) {
            mInviteUserButton.setVisibility(View.VISIBLE);
        } else {
            mInviteUserButton.setVisibility(View.GONE);
        }
    }

    private void checkCanRating() {
        ServiceManager.getInstance().getMeetingService().canRatingMeeting(mMeetingID).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                mRatingButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String message) {
                mRatingButton.setVisibility(View.GONE);
            }
        });
    }

    private void showConfirmDialog(final User user) {
        User currentUser = StorageManager.getUser();
        if(currentUser == null) return;
        if(!currentUser.mId.equals(mMeeting.mCreator.mId)) {
            Toast.makeText(getContext(), R.string.error_kick_user_meeting, Toast.LENGTH_SHORT).show();
            return;
        }

        if(currentUser.mId.equals(user.mId)) {
            return;
        }

        final QuestionDialog questionDialog = new QuestionDialog(getString(R.string.kick_user_confirm_question, user.mFullName));
        questionDialog.setDialogActionListener(new CustomDialogActionListener() {
            @Override
            public void dialogCancel() {
                questionDialog.dismissDialog();
            }

            @Override
            public void dialogPerformAction() {
                showReasonDialog(user);
            }
        });
        questionDialog.show(getChildFragmentManager(), MeetingInfoFragment.class.getName());
    }

    private void showReasonDialog(final User user) {
        ReasonDialog dialog = new ReasonDialog(getString(R.string.leave_meeting_reason));
        dialog.setReasonDialogListener(new ReasonDialog.ReasonDialogListener() {
            @Override
            public void onCancelButtonClicked() {

            }

            @Override
            public void onAcceptButtonClicked(String reason) {
                removeUser(user, reason);
            }
        });
        dialog.show(getChildFragmentManager(), MeetingActivity.class.getName());
    }

    private void removeUser(final User user, String reason) {
        showLoading();
        ServiceManager.getInstance().getMeetingService().kickUser(mMeetingID, user.mId, reason).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                mUserAdapter.getData().remove(user);
                mUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        if(getActivity() != null) {
            ((BaseActivity)getActivity()).showLoading();
        }
    }

    private void hideLoading() {
        if(getActivity() != null) {
            ((BaseActivity)getActivity()).hideLoading();
        }
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
