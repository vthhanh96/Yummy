package com.example.thesis.yummy.controller.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.controller.base.BaseActivity;
import com.example.thesis.yummy.restful.RestCallback;
import com.example.thesis.yummy.restful.ServiceManager;
import com.example.thesis.yummy.restful.model.User;
import com.example.thesis.yummy.storage.StorageManager;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterBirthdayActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.datePicker) DatePicker mDatePicker;

    private Date mBirthDay;

    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterBirthdayActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.btnNext)
    public void next() {
        showLoading();
        ServiceManager.getInstance().getUserService().updateBirthday(mBirthDay).enqueue(new RestCallback<User>() {
            @Override
            public void onSuccess(String message, User user) {
                hideLoading();
                StorageManager.saveUser(user);
                RegisterAddressActivity.start(RegisterBirthdayActivity.this);
            }

            @Override
            public void onFailure(String message) {
                hideLoading();
                Toast.makeText(RegisterBirthdayActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_birthday;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        initToolbar();
        initDatePicker();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initDatePicker() {
        Calendar calendar = Calendar.getInstance();
        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(i, i1, i2);
                mBirthDay = calendar.getTime();
            }
        });
    }
}
