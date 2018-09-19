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

public class SelectPostOptionsDialogFragment extends BaseCustomDialogFragment {
    private static final String ARG_KEY_IS_CREATOR = "ARG_KEY_IS_CREATOR";

    private ListView lsvEditPost;
    private int step;
    private boolean mIsCreator;
    SelectPostOptionsListener mListener;

    public static SelectPostOptionsDialogFragment getNewInstance(boolean isCreator) {
        SelectPostOptionsDialogFragment dialogFragment = new SelectPostOptionsDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_KEY_IS_CREATOR, isCreator);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    public SelectPostOptionsDialogFragment() {
        super();
        setView(R.layout.dialog_select_list);
        setHasAction(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsCreator = getArguments().getBoolean(ARG_KEY_IS_CREATOR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = super.onCreateView(inflater, container, savedInstanceState);

        final String[] array = getResources().getStringArray(mIsCreator ? R.array.post_options_creator : R.array.post_options_not_creator);
        StringListViewAdapter adapter;
        if (mIsCreator) {
            String[] redLines = new String[]{array[(array.length - 1)]};
            adapter = new StringListViewAdapter(getContext(), array, redLines);
        } else {
            adapter = new StringListViewAdapter(getContext(), array);
        }

        lsvEditPost = mainView.findViewById(R.id.lsvDialogList);
        lsvEditPost.setAdapter(adapter);

        lsvEditPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (array[position].equals("Chỉnh sửa bài viết")) {
                    if (mListener != null) {
                        mListener.editPost();
                    }
                    step = 1;
                    dismiss();
                }
                if (array[position].equals("Xóa bài viết")) {
                    if (mListener != null) {
                        mListener.deletePost();
                    }
                    step = 1;
                    dismiss();
                }
                if (array[position].equals("Đánh giá bài viết")) {
                    if (mListener != null) {
                        mListener.judgePost();
                    }
                    step = 1;
                    dismiss();
                }
                if (array[position].equals("Báo cáo bài viết")) {
                    if (mListener != null) {
                        mListener.reportPost();
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

    public void setPostOptionsListener(SelectPostOptionsListener listener) {
        mListener = listener;
    }

    public interface SelectPostOptionsListener {
        void editPost();

        void deletePost();

        void judgePost();

        void reportPost();
    }
}
