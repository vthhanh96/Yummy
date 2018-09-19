package com.example.thesis.yummy.view.dialog.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.thesis.yummy.R;

public class StringListViewAdapter extends ArrayAdapter<String> {
    private String[] redLineItems;

    public StringListViewAdapter(Context context, String[] items, String[] redLineItems) {
        super(context, 0, items);
        this.redLineItems = redLineItems;
    }

    public StringListViewAdapter(Context context, String[] items) {
        super(context, 0, items);
        this.redLineItems = new String[]{};
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_text_center, parent, false);
        }

        String item = getItem(position);
        boolean isRedLine = false;

        for (int i = 0; i < redLineItems.length; i++) {
            if (redLineItems[i].equals(item)) {
                isRedLine = true;
            }
        }

        TextView tvItem = (TextView) convertView.findViewById(R.id.tvListItem);
        TextView tvItemRed = (TextView) convertView.findViewById(R.id.tvListItemRed);

        if (isRedLine) {
            tvItemRed.setVisibility(View.VISIBLE);
            tvItem.setVisibility(View.GONE);
            tvItemRed.setText(item);
        } else {
            tvItemRed.setVisibility(View.GONE);
            tvItem.setVisibility(View.VISIBLE);
            tvItem.setText(item);
        }


        return convertView;
    }


}