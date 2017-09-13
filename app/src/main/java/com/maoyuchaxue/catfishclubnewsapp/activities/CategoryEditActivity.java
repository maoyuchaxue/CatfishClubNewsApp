package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.donkingliang.labels.LabelsView;
import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCategoryTag;

import java.util.ArrayList;
import java.util.List;

public class CategoryEditActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences preferences;
    private LabelsView labelsView, rssLabelsView;
    ArrayList<String> labels, rssLabels;

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
        rssLabelsView = (LabelsView) findViewById(R.id.rss_labels_view);

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

        rssLabels = new ArrayList<>();
        rssLabels.add("+");
        rssLabelsView.setLabels(rssLabels);
        rssLabelsView.setOnLabelSelectChangeListener(new LabelsView.OnLabelSelectChangeListener() {
            @Override
            public void onLabelSelectChange(View label, String labelText, boolean isSelect, int position) {
                if (labelText.equals("+") && isSelect) {
                    View dialogView = LayoutInflater.from(CategoryEditActivity.this).
                            inflate(R.layout.rss_source_dialog_layout, null);
                    final EditText feedEditText = (EditText) dialogView.findViewById(R.id.rss_source_url);
                    final EditText labelEditText = (EditText) dialogView.findViewById(R.id.rss_source_label);

                    AlertDialog.Builder builder = new AlertDialog.Builder(CategoryEditActivity.this);
                    builder.setTitle("添加RSS源");
                    builder.setView(dialogView);
                    builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.i("rss", feedEditText.getText().toString() + " " + labelEditText.getText().toString());
                            rssLabels.add(0, labelEditText.getText().toString());
                            rssLabelsView.setLabels(rssLabels);
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    builder.show();

                }
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
