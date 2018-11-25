package com.example.thesis.yummy.controller.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.AppConstants;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.view.SelectAgeDialog;
import com.example.thesis.yummy.view.TopBarView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.skyfishjy.library.RippleBackground;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends DrawerActivity {

    private static final int GENDER_ALL = -1;
    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 0;

    public static void start(Context context) {
        Intent starter = new Intent(context, SearchActivity.class);
        context.startActivity(starter);
    }

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.rippleBackground) RippleBackground mRippleBackground;
    @BindView(R.id.filterExpandableLayout) ExpandableLayout mFilterExpandableLayout;
    @BindView(R.id.arrowImageView) ImageView mArrowImageView;
    @BindView(R.id.userRecyclerView) RecyclerView mUserRecyclerView;
    @BindView(R.id.filterTextView) TextView mFilterTextView;
    @BindView(R.id.allGenderButton) Button mAllGenderButton;
    @BindView(R.id.maleButton) Button mMaleButton;
    @BindView(R.id.femaleButton) Button mFemaleButton;
    @BindView(R.id.ageRangeTextView) TextView mAgeRangeTextView;

    private UserAdapter mUserAdapter;
    private int mGender = 1;
    private int mAgeFrom = 18;
    private int mAgeTo = 25;
    private int mPageNumber = 1;

    @OnClick(R.id.searchImageButton)
    public void onSearchButtonClicked() {
        expandLayout(false);
        mRippleBackground.startRippleAnimation();
    }

    @OnClick(R.id.filterLayout)
    public void onFilterLayoutClicked() {
        expandLayout(!mFilterExpandableLayout.isExpanded());
    }

    @OnClick(R.id.allGenderButton)
    public void onAllGenderButtonClick() {
        mGender = -1;
        updateData();
    }

    @OnClick(R.id.maleButton)
    public void onMaleButtonClicked() {
        mGender = 1;
        updateData();
    }

    @OnClick(R.id.femaleButton)
    public void onFemaleButtonClicked() {
        mGender = 0;
        updateData();
    }

    @OnClick(R.id.ageRangeTextView)
    public void onAgeRangeTextViewClicked() {
        showSelectAgeDialog();
    }

    @Override
    protected int getNavId() {
        return AppConstants.NAV_DRAWER_ID_SEARCH_PAGE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
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
        updateData();
    }

    private void initTopBar() {
        mTopBarView.setTitle("Search");
        mTopBarView.setImageViewLeft(TopBarView.LEFT_MENU);
        mTopBarView.setImageViewRight(TopBarView.DRAWABLE_SEARCH);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                openDrawer();
            }

            @Override
            public void onRightClick() {
                searchUser();
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

        mUserRecyclerView.setAdapter(mUserAdapter);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateData() {
        String gender = "";
        switch (mGender) {
            case GENDER_ALL:
                selectedButton(mAllGenderButton, true);
                selectedButton(mMaleButton, false);
                selectedButton(mFemaleButton, false);
                gender = getString(R.string.all);
                break;
            case GENDER_MALE:
                selectedButton(mAllGenderButton, false);
                selectedButton(mMaleButton, true);
                selectedButton(mFemaleButton, false);
                gender = getString(R.string.male);
                break;
            case GENDER_FEMALE:
                selectedButton(mAllGenderButton, false);
                selectedButton(mMaleButton, false);
                selectedButton(mFemaleButton, true);
                gender = getString(R.string.female);
                break;
        }

        mAgeRangeTextView.setText(String.format(Locale.getDefault(), "%d - %d", mAgeFrom, mAgeTo));
        mFilterTextView.setText(getString(R.string.search_condition, gender, mAgeFrom, mAgeTo));
    }

    private void selectedButton(Button button, boolean isSelected) {
        if(isSelected) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
            button.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            button.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    private void expandLayout(boolean isExpand) {
        mFilterExpandableLayout.setExpanded(isExpand);
        mArrowImageView.setImageResource(isExpand ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
    }

    private void searchUser() {
        showSearchAnimation();
        ServiceManager.getInstance().getUserService().searchUser(mPageNumber, mGender, mAgeFrom, mAgeTo).enqueue(new RestCallback<List<User>>() {
            @Override
            public void onSuccess(String message, List<User> users) {
                hideSearchAnimation();
                mUserAdapter.addData(users);
            }

            @Override
            public void onFailure(String message) {
                hideSearchAnimation();
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSearchAnimation() {
        expandLayout(false);
        mRippleBackground.setVisibility(View.VISIBLE);
        mRippleBackground.startRippleAnimation();
    }

    private void hideSearchAnimation() {
        mRippleBackground.setVisibility(View.GONE);
        mRippleBackground.stopRippleAnimation();
    }

    private void showSelectAgeDialog() {
        SelectAgeDialog dialog = new SelectAgeDialog(this, mAgeTo, mAgeFrom);
        dialog.setSelectAgeDialogListener(new SelectAgeDialog.SelectAgeDialogListener() {
            @Override
            public void onCancelButtonClicked() {

            }

            @Override
            public void onOKButtonClicked(int ageFrom, int ageTo) {
                mAgeFrom = ageFrom;
                mAgeTo = ageTo;
                updateData();
            }
        });
        dialog.show();
    }

    private class UserAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

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

            helper.setChecked(R.id.checkbox, item.mIsSelected);
        }
    }
}
