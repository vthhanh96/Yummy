package com.example.thesis.yummy.view.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thesis.yummy.R;

public class QuestionDialog extends BaseCustomDialogFragment {
    private TextView tvContent,txvTitle;
    private String content;
    public QuestionDialog() {
    }

    @SuppressLint("ValidFragment")
    public QuestionDialog(String content) {
        super();
        setView(R.layout.dialog_question);
        setHasAction(true);
        this.content = content;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = super.onCreateView(inflater, container, savedInstanceState);
        txvTitle  = (TextView) mainView.findViewById(R.id.txvTitle);
        tvContent = (TextView) mainView.findViewById(R.id.tvQuestion);
        setActionName("Đồng ý");
        tvContent.setText(content);
        return mainView;
    }
}
