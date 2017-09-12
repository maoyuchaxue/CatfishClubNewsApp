package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.donkingliang.labels.LabelsView;
import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCategoryTag;

import java.util.ArrayList;
import java.util.List;

public class CategoryEditActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private LabelsView labelsView;
    ArrayList<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        this.preferences = CategoryEditActivity.this.getSharedPreferences("category", 0);
        Toolbar toolbar = (Toolbar)findViewById(R.id.category_toolbar);
        toolbar.setTitle("分类设置");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(this);

        List<Boolean> categoryPreferences = getCategoryPreferences();

        labelsView = (LabelsView) findViewById(R.id.labels_view);
        labels = new ArrayList<>();
        ArrayList<Integer> selectedLabels = new ArrayList<>();
        for (int i = 0; i < NewsCategoryTag.TITLES.length; i++) {
            labels.add(NewsCategoryTag.TITLES[i]);
            if (categoryPreferences.get(i)) {
                selectedLabels.add(i);
            }
        }

        int[] selected = new int[selectedLabels.size()];
        for (int i = 0; i < selectedLabels.size(); i++) {
            selected[i] = selectedLabels.get(i);
        }

        labelsView.setLabels(labels);
        labelsView.setSelects(selected);

        labelsView.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(View label, String labelText, boolean isSelect, int position) {
                try {
                    String tag = NewsCategoryTag.TITLES_EN[position];
                    preferences.edit().putBoolean(tag, isSelect).apply();
                } catch (Exception e) {}
            }
        });



    }

    @Override
    public void onClick(View view) {
        CategoryEditActivity.this.finish();
    }


    private List<Boolean> getCategoryPreferences() {
        ArrayList<Boolean> categoryPreferences = new ArrayList<Boolean>();
        SharedPreferences sharedPreferences = getSharedPreferences("category", 0);
        for (int i = 0; i < NewsCategoryTag.TITLES.length; i++) {
            boolean appears = sharedPreferences.getBoolean(NewsCategoryTag.TITLES_EN[i], true);
            categoryPreferences.add(appears);
        }
        return categoryPreferences;
    }
}
