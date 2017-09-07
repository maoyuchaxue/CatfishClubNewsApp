package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.controller.CategoryCheckboxAdapter;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCategoryTag;

import java.util.ArrayList;
import java.util.List;

public class CategoryEditActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        Toolbar toolbar = (Toolbar)findViewById(R.id.category_toolbar);
        toolbar.setTitle("分类设置");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(this);

        List<Boolean> categoryPreferences = getCategoryPreferences();

        ListView listView = (ListView) findViewById(R.id.category_list);
        CategoryCheckboxAdapter adapter = new CategoryCheckboxAdapter(this, categoryPreferences);
        listView.setAdapter(adapter);


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
