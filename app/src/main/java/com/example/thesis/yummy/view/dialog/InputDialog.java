package com.example.thesis.yummy.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thesis.yummy.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputDialog extends Dialog {

    @BindView(R.id.edt_input) EditText mEdtInput;

    private Context mContext;
    private InputDialogListener mListener;
    private String mContent;

    public void setListener(InputDialogListener listener) {
        mListener = listener;
    }

    public interface InputDialogListener{
        void onCancelClick();
        void onDoneClick(String content);
    }

    public InputDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public void setContentInput(String content) {
        mContent = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();

        assert window != null;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setContentView(R.layout.dialog_input);
        ButterKnife.bind(this);

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        init();
    }

    private void init() {
        mEdtInput.setText(mContent);
    }

    @OnClick(R.id.tv_cancel)
    public void onCancel() {
        dismiss();
        mListener.onCancelClick();
    }

    @OnClick(R.id.tv_done)
    public void onDone() {
        if(TextUtils.isEmpty(mEdtInput.getText().toString().trim())) {
            Toast.makeText(mContext, "Bạn phải nhập nội dung bình luận.", Toast.LENGTH_SHORT).show();
        }
        dismiss();
        mListener.onDoneClick(mEdtInput.getText().toString());
    }
}
