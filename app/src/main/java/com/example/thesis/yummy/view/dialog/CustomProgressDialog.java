package com.example.thesis.yummy.view.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.thesis.yummy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomProgressDialog extends ProgressDialog {

    @BindView(R.id.txtTitle) TextView mTxtTitle;

    private String title;

    public CustomProgressDialog(Context context, String title) {
        super(context, R.style.CustomDialog);
        this.title = title;
    }

    public CustomProgressDialog(Context context, int theme, String title) {
        super(context, theme);
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setContentView(R.layout.dialog_custom_progress_layout);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        mTxtTitle.setText(title);
    }
}