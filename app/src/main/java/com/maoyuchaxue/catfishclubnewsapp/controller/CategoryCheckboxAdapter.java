package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.maoyuchaxue.catfishclubnewsapp.R;

/**
 * Created by catfish on 17/9/6.
 */

public class CategoryCheckboxAdapter extends BaseAdapter {
    static final private List<String> names = Arrays.asList("综合", "教育");
    private Context context;

    public CategoryCheckboxAdapter(Context context) {
        this.context = context;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.category_unit_layout, null);
        TextView summaryTextView = (TextView) convertView.findViewById(R.id.category_summary);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.category_checkbox);

        summaryTextView.setText(names.get(position));

        return convertView;
    }

}
