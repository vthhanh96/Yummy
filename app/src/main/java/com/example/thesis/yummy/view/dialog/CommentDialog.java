package com.example.thesis.yummy.view.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.view.dialog.listener.CustomDialogActionListener;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class CommentDialog extends BaseCustomDialogFragment {

    @BindView(R.id.commentTextView) TextView mCommentTextView;

    public interface CommentDialogListener {
        void onCancelButtonClicked();
        void onCommentButtonClicked(String reason);
    }

    private CommentDialogListener mListener;

    public CommentDialog() {
        super();
        setView(R.layout.dialog_comment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void setCommentDialogListener(CommentDialogListener listener) {
        mListener = listener;
    }

    private void init() {
        initListener();
    }

    private void initListener() {
        setDialogActionListener(new CustomDialogActionListener() {
            @Override
            public void dialogCancel() {
                dismiss();
                if(mListener != null) {
                    mListener.onCancelButtonClicked();
                }
            }

            @Override
            public void dialogPerformAction() {
                if(mListener != null) {
                    mListener.onCommentButtonClicked(mCommentTextView.getText().toString());
                }
            }
        });
    }
}
