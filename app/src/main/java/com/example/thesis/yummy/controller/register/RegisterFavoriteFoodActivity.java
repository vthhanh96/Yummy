package com.example.thesis.yummy.controller.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.controller.home.HomeActivity;
import com.example.thesis.yummy.controller.main.MainActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.Category;
import com.example.thesis.yummy.restful.model.User;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterFavoriteFoodActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterFavoriteFoodActivity.class);
        context.startActivity(starter);
    }

    @BindView(R.id.categoryRecyclerView) RecyclerView mCategoryRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private CategoryAdapter mCategoryAdapter;
    private int mPageNumber = 0;

    @OnClick(R.id.finishButton)
    public void finished() {
        updateFavoriteFood();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_favorite_food;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initToolbar();
        initRecyclerView();
        getCategories();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initRecyclerView() {
        mCategoryAdapter = new CategoryAdapter();
        mCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Category category = mCategoryAdapter.getItem(position);
                if(category == null) return;
                category.mIsSelected = !category.mIsSelected;
                mCategoryAdapter.notifyItemChanged(position);
            }
        });

        mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCategoryRecyclerView.setAdapter(mCategoryAdapter);

        mCategoryAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getCategories();
            }
        }, mCategoryRecyclerView);
    }

    private void getCategories() {
        ServiceManager.getInstance().getCategoryService().getCategories(null, mPageNumber).enqueue(new RestCallback<List<Category>>() {
            @Override
            public void onSuccess(String message, List<Category> categories) {
                if(categories == null || categories.isEmpty()) {
                    mCategoryAdapter.loadMoreEnd();
                    return;
                }
                mCategoryAdapter.loadMoreComplete();
                mPageNumber++;
                mCategoryAdapter.addData(categories);
            }

            @Override
            public void onFailure(String message) {
                mCategoryAdapter.loadMoreFail();
                Toast.makeText(RegisterFavoriteFoodActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFavoriteFood() {
        List<Integer> categories = new ArrayList<>();
        for (Category category : mCategoryAdapter.getData()) {
            if(category.mIsSelected) {
                categories.add(category.mId);
            }
        }
        if(categories.isEmpty()) {
            MainActivity.start(this);
            return;
        }
        showLoading();
        ServiceManager.getInstance().getUserService().updateFavoriteFood(categories).enqueue(new RestCallback<User>() {
            @Override
            public void onSuccess(String message, User user) {
                hideLoading();
                MainActivity.start(RegisterFavoriteFoodActivity.this);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(RegisterFavoriteFoodActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class CategoryAdapter extends BaseQuickAdapter<Category, BaseViewHolder>{

        public CategoryAdapter() {
            super(R.layout.layout_select_category_item, new ArrayList<Category>());
        }

        @Override
        protected void convert(BaseViewHolder helper, Category item) {
            helper.setText(R.id.tvName, item.mName);
            helper.setVisible(R.id.imgChecked, item.mIsSelected);
        }
    }
}
