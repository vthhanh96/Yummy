package com.example.thesis.yummy.controller.post;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Category;
import com.example.thesis.yummy.view.TopBarView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryActivity extends BaseActivity {

    private static final String ARG_KEY_SELECTED_CATEGORIES = "ARG_KEY_SELECTED_CATEGORIES";

    @BindView(R.id.topBar) TopBarView mTopBarView;
    @BindView(R.id.rcvCategories) RecyclerView mCategoriesRecyclerView;
    @BindView(R.id.edtSearch) AppCompatEditText mEdtSearch;

    private int mPageNumber = 0;
    private CategoryAdapter mCategoryAdapter;
    private String mQuery = "";
    private List<Category> mSelectedCategories;

    public static void startForResult(Activity activity, List<Category> selectedCategories, int requestCode) {
        Intent starter = new Intent(activity, CategoryActivity.class);
        String data = new Gson().toJson(selectedCategories);
        starter.putExtra(ARG_KEY_SELECTED_CATEGORIES, data);
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_category;
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
        initEditText();
        initRecyclerView();
        getCategories(mQuery);
    }

    private void getExtras() {
        try {
            String data = getIntent().getStringExtra(ARG_KEY_SELECTED_CATEGORIES);
            List<Category> selected = Arrays.asList(new Gson().fromJson(data, Category[].class));
            mSelectedCategories = new ArrayList<>();
            mSelectedCategories.addAll(selected);
        } catch (Exception ex) {
            ex.printStackTrace();
            mSelectedCategories = new ArrayList<>();
        }
    }

    private void initTopBar() {
        mTopBarView.setImageViewLeft(TopBarView.LEFT_BACK);
        mTopBarView.setImageViewRight(R.drawable.ic_check_white);
        mTopBarView.setOnLeftRightClickListener(new TopBarView.OnLeftRightClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                Intent data = getIntent();
                data.putExtra(ARG_KEY_SELECTED_CATEGORIES, new Gson().toJson(mSelectedCategories));
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }

    private void initEditText() {
        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mQuery = editable.toString();
                mPageNumber = 0;
                mCategoryAdapter.setEnableLoadMore(true);
                getCategories(mQuery);
            }
        });
    }

    private void initRecyclerView() {
        mCategoryAdapter = new CategoryAdapter();
        mCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Category category = (Category) adapter.getItem(position);
                if(category == null) return;

                category.mIsSelected = !category.mIsSelected;
                mCategoryAdapter.notifyDataSetChanged();
                updateSelectedList(category);
            }
        });

        mCategoriesRecyclerView.setAdapter(mCategoryAdapter);
        mCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mCategoryAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getCategories(mQuery);
            }
        }, mCategoriesRecyclerView);
    }

    private void getCategories(String query) {
        ServiceManager.getInstance().getCategoryService().getCategories(query, mPageNumber).enqueue(new RestCallback<List<Category>>() {
            @Override
            public void onSuccess(String message, List<Category> categories) {
                if(categories == null || categories.isEmpty()) {
                    mCategoryAdapter.loadMoreEnd();
                    return;
                }
                mCategoryAdapter.loadMoreComplete();
                mPageNumber++;

                for (Category category : categories) {
                    for (Category selected : mSelectedCategories) {
                        if(selected.mId.equals(category.mId)) {
                            category.mIsSelected = true;
                        }
                    }
                }

                mCategoryAdapter.addData(categories);
            }

            @Override
            public void onFailure(String message) {
                mCategoryAdapter.loadMoreEnd();
            }
        });
    }

    private void updateSelectedList(Category category) {
        if(category.mIsSelected) {
            mSelectedCategories.add(category);
        } else {
            for(Category cate : mSelectedCategories) {
                if(cate.mId.equals(category.mId)) {
                    mSelectedCategories.remove(cate);
                    break;
                }
            }
        }
    }

    private class CategoryAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {

        public CategoryAdapter() {
            super(R.layout.layout_select_category_item, new ArrayList<Category>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Category item) {
            if(item == null) return;
            helper.setText(R.id.tvName, item.mName);
            helper.setVisible(R.id.imgChecked, item.mIsSelected);
        }
    }
}
