package com.example.thesis.yummy.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;

import com.example.thesis.yummy.R;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectAgeDialog extends Dialog {

    @BindView(R.id.ageFromPicker) MaterialNumberPicker mAgeFromPicker;
    @BindView(R.id.ageToPicker) MaterialNumberPicker mAgeToPicker;

    private SelectAgeDialogListener mListener;
    private int mAgeTo;
    private int mAgeFrom;

    public interface SelectAgeDialogListener{
        void onCancelButtonClicked();
        void onOKButtonClicked(int ageFrom, int ageTo);
    }

    public SelectAgeDialog(@NonNull Context context, int ageTo, int ageFrom) {
        super(context);
        mAgeFrom = ageFrom;
        mAgeTo = ageTo;
    }

    @OnClick(R.id.cancelButton)
    public void cancel() {
        dismiss();
        if(mListener != null) {
            mListener.onCancelButtonClicked();
        }
    }

    @OnClick(R.id.okButton)
    public void ok() {
        dismiss();
        if(mListener != null) {
            mListener.onOKButtonClicked(mAgeFromPicker.getValue(), mAgeToPicker.getValue());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setContentView(R.layout.dialog_select_age);
        ButterKnife.bind(this);
        init();
    }

    public void setSelectAgeDialogListener(SelectAgeDialogListener listener) {
        mListener = listener;
    }

    private void init() {
        mAgeFromPicker.setValue(mAgeFrom);
        mAgeToPicker.setValue(mAgeTo);
    }
}
