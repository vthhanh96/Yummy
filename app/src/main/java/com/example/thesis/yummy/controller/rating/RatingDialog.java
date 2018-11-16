package com.example.thesis.yummy.controller.rating;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.restful.model.Rating;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RatingDialog extends Dialog {

    @BindView(R.id.titleTextView) TextView mTitleTextView;
    @BindView(R.id.ratingBar) MaterialRatingBar mRatingBar;
    @BindView(R.id.commentTextView) TextView mCommentTextView;

    private RatingDialogListener mListener;
    private Context mContext;

    public interface RatingDialogListener {
        void onRating(Float rating, String comment);
    }

    private String mTitle;
    private Float mRating = 0f;
    private String mComment = "";

    @OnClick(R.id.cancelButton)
    public void cancel() {
        dismiss();
    }

    @OnClick(R.id.okButton)
    public void rating() {
        if(mRatingBar.getRating() == 0) {
            Toast.makeText(mContext, R.string.rating_message, Toast.LENGTH_SHORT).show();
            return;
        }

        dismiss();
        if(mListener == null) return;
        mListener.onRating(mRatingBar.getRating(), mCommentTextView.getText().toString());
    }

    public RatingDialog(@NonNull Context context, String title) {
        super(context);
        mContext = context;
        mTitle = title;
    }

    public void setRatingDialogListener(RatingDialogListener listener) {
        mListener = listener;
    }

    public void setRating(Rating rating) {
        mRating = rating.mPoint / 2f;
        mComment = rating.mContent;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rating_layout);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mTitleTextView.setText(mTitle);
        mRatingBar.setRating(mRating);
        mCommentTextView.setText(mComment);
    }
}
