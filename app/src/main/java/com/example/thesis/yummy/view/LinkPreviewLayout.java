package com.example.thesis.yummy.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.thesis.yummy.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ponnamkarthik.richlinkpreview.MetaData;

public class LinkPreviewLayout extends LinearLayout {

    @BindView(R.id.linkImageView) ImageView mLinkImageView;
    @BindView(R.id.linkTitleTextView) TextView mLinkTitleTextView;
    @BindView(R.id.linkDescriptionTextView) TextView mLinkDescriptionTextView;

    private MetaData mMetaData;

    public LinkPreviewLayout(Context context) {
        this(context, null, 0);
    }

    public LinkPreviewLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinkPreviewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
        initClickListener();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.link_preview_layout, this);
        ButterKnife.bind(this, view);
    }

    public void setMetaData(@NonNull MetaData metaData) {
        mMetaData = metaData;

        Glide.with(getContext().getApplicationContext()).load(metaData.getImageurl()).into(mLinkImageView);
        mLinkTitleTextView.setText(metaData.getTitle());
        mLinkDescriptionTextView.setText(metaData.getDescription());
    }

    private void initClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMetaData == null) return;

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mMetaData.getUrl()));
                getContext().startActivity(intent);
            }
        });
    }
}
