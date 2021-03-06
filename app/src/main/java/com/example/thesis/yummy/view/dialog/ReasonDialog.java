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
public class ReasonDialog extends BaseCustomDialogFragment {

    @BindView(R.id.titleTextView) TextView mTitleTextView;
    @BindView(R.id.reasonTextView) TextView mReasonTextView;

    public interface ReasonDialogListener{
        void onCancelButtonClicked();
        void onAcceptButtonClicked(String reason);
    }

    private String mTitle;
    private ReasonDialogListener mListener;

    public ReasonDialog(String title) {
        super();
        setView(R.layout.dialog_reason);
        setHasAction(true);
        mTitle = title;
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

    public void setReasonDialogListener(ReasonDialogListener listener) {
        mListener = listener;
    }

    private void init() {
        initData();
        initListener();
    }

    private void initData() {
        mTitleTextView.setText(mTitle);
        setActionName(getString(R.string.accept));
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
                dismiss();
                if(mListener != null) {
                    mListener.onAcceptButtonClicked(mReasonTextView.getText().toString());
                }
            }
        });
    }
}
