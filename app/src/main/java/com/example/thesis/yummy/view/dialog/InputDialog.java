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
public class InputDialog extends BaseCustomDialogFragment {

    @BindView(R.id.contentTextView) TextView mContentTextView;

    public interface InputDialogListener {
        void onCancelClick();
        void onDoneClick(String content);
    }

    private InputDialogListener mListener;

    public InputDialog() {
        super();
        setView(R.layout.dialog_input);
        setHasAction(true);
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

    public void setListener(InputDialogListener listener) {
        mListener = listener;
    }

    public void setContentInput(String content) {
        mContentTextView.setText(content);
    }

    private void init() {
        initListener();
        setActionName(getString(R.string.done));
    }

    private void initListener() {
        setDialogActionListener(new CustomDialogActionListener() {
            @Override
            public void dialogCancel() {
                dismiss();
                if(mListener != null) {
                    mListener.onCancelClick();
                }
            }

            @Override
            public void dialogPerformAction() {
                dismiss();
                if(mListener != null) {
                    mListener.onDoneClick(mContentTextView.getText().toString());
                }
            }
        });
    }
}
