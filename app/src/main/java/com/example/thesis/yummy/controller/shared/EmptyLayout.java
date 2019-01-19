package com.example.thesis.yummy.controller.shared;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thesis.yummy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmptyLayout extends LinearLayout {

    @BindView(R.id.emptyImageView) ImageView mEmptyImageView;
    @BindView(R.id.emptyTextView) TextView mEmptyTextView;

    public EmptyLayout(Context context) {
        this(context, null, 0);
    }

    public EmptyLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.layout_empty, this);
        ButterKnife.bind(this, view);
    }

    public void setEmptyImageResource(int resource) {
        mEmptyImageView.setImageResource(resource);
    }

    public void setEmptyImageMessage(String message) {
        mEmptyTextView.setText(message);
    }
}
