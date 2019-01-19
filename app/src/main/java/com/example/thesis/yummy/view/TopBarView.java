package com.example.thesis.yummy.view;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thesis.yummy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopBarView extends RelativeLayout implements View.OnClickListener{

    public static final int LEFT_MENU = 0;
    public static final int LEFT_BACK = 1;

    public static final int DRAWABLE_SEARCH = R.drawable.ic_search_menu;

    @BindView(R.id.rootLayout)
    RelativeLayout mRootLayout;

    @BindView(R.id.imgLeft)
    ImageView imgLeft;

    @BindView(R.id.imgRight)
    ImageView imgRight;

    @BindView(R.id.txtTitle)
    TextView tvTitle;

    @BindView(R.id.logoImageView)
    ImageView mLogoImageView;

    private OnLeftRightClickListener leftRightClickListener;

    public void setOnLeftRightClickListener(OnLeftRightClickListener listener) {
        this.leftRightClickListener = listener;
    }

    public interface OnLeftRightClickListener {
        void onLeftClick();
        void onRightClick();
    }

    public TopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_custom_topbar,
                this,
                false
        );
        ButterKnife.bind(this, view);

        imgLeft.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        this.addView(view);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgLeft:
                leftRightClickListener.onLeftClick();
                break;
            case R.id.imgRight:
                leftRightClickListener.onRightClick();
                break;
        }
    }

    public void setImageViewLeft(int type) {
        if (type == LEFT_MENU) {
            imgLeft.setImageResource(R.drawable.ic_menu);
        } else {
            imgLeft.setImageResource(R.drawable.ic_back);
        }
        imgLeft.setVisibility(VISIBLE);
    }

    public void setImageViewRight(int one) {
        imgRight.setVisibility(VISIBLE);
        imgRight.setImageResource(one);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
        tvTitle.setVisibility(VISIBLE);
        mLogoImageView.setVisibility(View.GONE);
    }

    public void setTransparentBackground(boolean isTransparent) {
        if(isTransparent) {
            mRootLayout.setBackgroundColor(Color.TRANSPARENT);
        } else {
            mRootLayout.setBackgroundColor(this.getResources().getColor(R.color.white));
        }
    }
}
