package com.example.thesis.yummy.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.thesis.yummy.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MultipleImageCircleView extends FrameLayout {

    @BindView(R.id.leftLayout) LinearLayout mLeftLayout;
    @BindView(R.id.topLeftImageView) ImageView mTopLeftImageView;
    @BindView(R.id.bottomLeftImageView) ImageView mBottomLeftImageView;
    @BindView(R.id.leftSeparateView) View mLeftSeparateView;
    @BindView(R.id.verticalSeparateView) View mVerticalSeparateView;
    @BindView(R.id.rightLayout) LinearLayout mRightLayout;
    @BindView(R.id.topRightImageView) ImageView mTopRightImageView;
    @BindView(R.id.bottomRightImageView) ImageView mBottomRightImage;
    @BindView(R.id.rightSeparateView) View mRightSeparateView;

    private List<String> mImages = new ArrayList<>();

    public MultipleImageCircleView(@NonNull Context context) {
        this(context, null, 0);
    }

    public MultipleImageCircleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleImageCircleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.layout_multiple_image_circle_view, this);
        ButterKnife.bind(this, view);
    }

    public void setImages(String... images) {
        mImages.clear();
        mImages.addAll(Arrays.asList(images));
        showImage();
    }

    private void showImage() {
        for (int i = 0; i < mImages.size(); i++) {
            switch (i) {
                case 0:
                    showTopLeftImage(mImages.get(i));
                    break;
                case 1:
                    showTopRightImage(mImages.get(i));
                    break;
                case 2:
                    showBottomRightImage(mImages.get(i));
                    break;
                case 3:
                    showBottomLeftImageView(mImages.get(i));
                    break;
            }
            if(i == 3) break;
        }

    }

    private void showTopLeftImage(String image) {
        Glide.with(getContext().getApplicationContext()).load(image).into(mTopLeftImageView);
    }

    private void showTopRightImage(String image) {
        mRightLayout.setVisibility(VISIBLE);
        mVerticalSeparateView.setVisibility(VISIBLE);
        Glide.with(getContext().getApplicationContext()).load(image).into(mTopRightImageView);
    }

    private void showBottomRightImage(String image) {
        mBottomRightImage.setVisibility(VISIBLE);
        mRightSeparateView.setVisibility(VISIBLE);
        Glide.with(getContext().getApplicationContext()).load(image).into(mBottomRightImage);
    }

    private void showBottomLeftImageView(String image) {
        mBottomLeftImageView.setVisibility(VISIBLE);
        mLeftSeparateView.setVisibility(VISIBLE);
        Glide.with(getContext().getApplicationContext()).load(image).into(mBottomLeftImageView);
    }
}
