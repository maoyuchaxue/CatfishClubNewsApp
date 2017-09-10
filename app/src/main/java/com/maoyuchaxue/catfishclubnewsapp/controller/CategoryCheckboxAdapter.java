package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCategoryTag;

/**
 * Created by catfish on 17/9/6.
 */

public class CategoryCheckboxAdapter extends BaseAdapter {
    private List<Boolean> isChosen;
    private Context context;
    private SharedPreferences preference;

    public CategoryCheckboxAdapter(Context context, List<Boolean> isChosen) {
        this.context = context;
        this.isChosen = isChosen;
        this.preference = context.getSharedPreferences("category", 0);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return NewsCategoryTag.TITLES.length;
    }

    @Override
    public Object getItem(int position) {
        return NewsCategoryTag.TITLES[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.category_unit_layout, null);
        TextView summaryTextView = (TextView) convertView.findViewById(R.id.category_summary);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.category_checkbox);
        checkBox.setChecked(isChosen.get(position));

        checkBox.setTag(NewsCategoryTag.TITLES_EN[position]);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String tag = (String) compoundButton.getTag();
                preference.edit().putBoolean(tag, b).apply();
            }
        });

        summaryTextView.setText(NewsCategoryTag.TITLES[position]);
        return convertView;
    }


}
