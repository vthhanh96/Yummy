package com.example.thesis.yummy.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.thesis.yummy.R;
import com.example.thesis.yummy.view.dialog.adapter.StringListViewAdapter;

public class SelectCommentOptionsDialogFragment extends BaseCustomDialogFragment {

    private ListView lsvEditPost;
    private int step;
    SelectCommentOptionsListener mListener;

    public SelectCommentOptionsDialogFragment() {
        super();
        setView(R.layout.dialog_select_list);
        setHasAction(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = super.onCreateView(inflater, container, savedInstanceState);

        final String[] array = getResources().getStringArray(R.array.dialog_select_comment_options);
        StringListViewAdapter adapter;
        String[] redLines = new String[]{array[(array.length - 1)]};
        adapter = new StringListViewAdapter(getContext(), array, redLines);

        lsvEditPost = mainView.findViewById(R.id.lsvDialogList);
        lsvEditPost.setAdapter(adapter);

        lsvEditPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (array[position].equals("Chỉnh sửa bình luận")) {
                    if (mListener != null) {
                        mListener.editComment();
                    }
                    step = 1;
                    dismiss();
                }
                if (array[position].equals("Xóa bình luận")) {
                    if (mListener != null) {
                        mListener.deleteComment();
                    }
                    step = 1;
                    dismiss();
                }
            }
        });

        return mainView;
    }

    public void onPause() {
        super.onPause();

        if (step == 1) {
            dismissDialog();
        }
    }

    public void setCommentOptionsListener(SelectCommentOptionsListener listener) {
        mListener = listener;
    }

    public interface SelectCommentOptionsListener {
        void editComment();

        void deleteComment();
    }

}
