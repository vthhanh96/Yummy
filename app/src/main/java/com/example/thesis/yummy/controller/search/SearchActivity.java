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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.AppConstants;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.DrawerActivity;
import com.example.thesis.yummy.controller.post.CategoryActivity;
import com.example.thesis.yummy.controller.profile.ProfileActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Base;
import com.example.thesis.yummy.restful.model.Category;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.restful.request.UserRequest;
import com.example.thesis.yummy.utils.StringUtils;
import com.example.thesis.yummy.view.SelectAgeDialog;
import com.example.thesis.yummy.view.TopBarView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.skyfishjy.library.RippleBackground;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends DrawerActivity {

    private static final String ARG_KEY_SELECTED_CATEGORIES = "ARG_KEY_SELECTED_CATEGORIES";
    private static final String ARG_KEY_IS_ADD_USER_TO_MEETING = "ARG_KEY_IS_ADD_USER_TO_MEETING";
    private static final String ARG_KEY_MEETING_ID = "ARG_KEY_MEETING_ID";

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_SELECT_CATEGORY = 2;
    private static final int GENDER_ALL = -1;
    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 0;

    public static void start(Context context) {
        Intent starter = new Intent(context, SearchActivity.class);
        context.startActivity(starter);
    }

    public static void start(Context context, boolean isAddUserToMeeting, int meetingID) {
        Intent starter = new Intent(context, SearchActivity.class);
        starter.putExtra(ARG_KEY_IS_ADD_USER_TO_MEETING, isAddUserToMeeting);
        starter.putExtra(ARG_KEY_MEETING_ID, meetingID);
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
    @BindView(R.id.placeTextView) TextView mPlaceTextView;
    @BindView(R.id.favoriteTextView) TextView mFavoriteTextView;

    private UserAdapter mUserAdapter;
    private int mGender = 1;
    private int mAgeFrom = 18;
    private int mAgeTo = 25;
    private int mPageNumber = 0;
    private Location mLocation = new Location("");;
    private List<Category> mSelectedCategories;
    private boolean mIsAddUserToMeeting = false;
    private int mMeetingID;

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

    @OnClick(R.id.placeTextView)
    public void placeTextViewClicked() {
        openSearchLocation();
    }

    @OnClick(R.id.favoriteTextView)
    public void favoriteTextViewClicked() {
        CategoryActivity.startForResult(this, mSelectedCategories, REQUEST_CODE_SELECT_CATEGORY);
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
        getExtras();
        initTopBar();
        initRecyclerView();
        updateData();
    }

    private void getExtras() {
        mIsAddUserToMeeting = getIntent().getBooleanExtra(ARG_KEY_IS_ADD_USER_TO_MEETING, false);
        mMeetingID = getIntent().getIntExtra(ARG_KEY_MEETING_ID, -1);
    }

    private void initTopBar() {
        lockDrawer();
        mTopBarView.setTitle(getString(R.string.search));
        mTopBarView.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBarView.setImageViewRight(TopBarView.DRAWABLE_SEARCH);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                mPageNumber = 0;
                mUserAdapter.setNewData(new ArrayList<User>());
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
                if(mIsAddUserToMeeting) {
                    validateInviteUser(item);
                } else {
                    SendRequestActivity.start(SearchActivity.this, item);
                }
            }
        });
        mUserAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                User item = mUserAdapter.getItem(position);
                if(item == null) return;

                if(view.getId() == R.id.avatarImageView) {
                    ProfileActivity.start(SearchActivity.this, item.mId);
                }
            }
        });

        mUserRecyclerView.setAdapter(mUserAdapter);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUserAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                searchUser();
            }
        }, mUserRecyclerView);
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
        mPlaceTextView.setText(R.string.near_me);
        mFavoriteTextView.setText(R.string.none_select_favorite);
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
        if(mPageNumber == 0) {
            showSearchAnimation();
        }
        UserRequest.searchUsers(mPageNumber, mGender < 0 ? null : mGender, mAgeFrom, mAgeTo, 0f, 0f, new RestCallback<List<User>>() {
            @Override
            public void onSuccess(String message, List<User> users) {
                hideSearchAnimation();
                if(users == null || users.isEmpty()) {
                    mUserAdapter.loadMoreEnd();
                    return;
                }

                mUserAdapter.loadMoreComplete();
                mUserAdapter.addData(users);
                mUserRecyclerView.setVisibility(View.VISIBLE);
                mPageNumber++;
            }

            @Override
            public void onFailure(String message) {
                hideSearchAnimation();
                mUserAdapter.loadMoreFail();
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

    private void openSearchLocation() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(new LatLngBounds(
                                    new LatLng(8.407168163601074, 104.1448974609375),
                                    new LatLng(10.7723923007117563, 106.6981029510498)))
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void validateInviteUser(final User user) {
        showLoading();
        ServiceManager.getInstance().getMeetingService().checkInviteUser(mMeetingID, user.mId).enqueue(new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                inviteUser(user);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inviteUser(User user) {
        UserRequest.sendRequest(user.mId, null, null, null, null, null, mMeetingID, new RestCallback<Base>() {
            @Override
            public void onSuccess(String message, Base base) {
                hideLoading();
                Toast.makeText(SearchActivity.this, "Đã gửi lời mời", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                final Place place = PlacePicker.getPlace(this, data);

                if (place != null) {
                    mPlaceTextView.setText(place.getName().toString());
                    mLocation.setLatitude(place.getLatLng().latitude);
                    mLocation.setLongitude(place.getLatLng().longitude);
                }
                break;
            case REQUEST_CODE_SELECT_CATEGORY:
                if(data == null) return;
                try {
                    String selectedCategories = data.getStringExtra(ARG_KEY_SELECTED_CATEGORIES);
                    List<Category> selected = Arrays.asList(new Gson().fromJson(selectedCategories, Category[].class));
                    mSelectedCategories = new ArrayList<>();
                    mSelectedCategories.addAll(selected);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mSelectedCategories = new ArrayList<>();
                }
                StringBuilder categories = new StringBuilder();
                for (Category category : mSelectedCategories) {
                    categories.append(category.mName).append(", ");
                }
                categories.delete(categories.length() - 2, categories.length() - 1);
                mFavoriteTextView.setText(categories);
                break;
        }
    }

    private class UserAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

        public UserAdapter() {
            super(R.layout.item_search_result_layout, new ArrayList<User>());
        }

        @Override
        protected void convert(BaseViewHolder helper, User item) {
            if(item == null) return;
            ImageView imageView = helper.getView(R.id.avatarImageView);
            Glide.with(mContext.getApplicationContext()).load(item.mAvatar).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_default_avatar)).into(imageView);

            helper.setText(R.id.nameTextView, item.mFullName);
            helper.setVisible(R.id.onlineImageView, item.mIsOnline);
            helper.setText(R.id.trustPointTextView, getString(R.string.trust_point_amount, item.mTrustPoint));
            helper.setText(R.id.distanceTextView, StringUtils.convertDistanceToString(item.mDistance));

            RatingBar ratingBar = helper.getView(R.id.ratingBar);
            if(item.mCountPeopleEvaluate != 0) {
                ratingBar.setRating((float)(item.mMainPoint / item.mCountPeopleEvaluate) / 2 );
            } else {
                ratingBar.setRating(0);
            }

            helper.addOnClickListener(R.id.avatarImageView);
        }
    }
}
