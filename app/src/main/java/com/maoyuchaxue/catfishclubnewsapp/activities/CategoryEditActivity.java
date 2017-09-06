package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.controller.CategoryCheckboxAdapter;

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

        ListView listView = (ListView) findViewById(R.id.category_list);
        CategoryCheckboxAdapter adapter = new CategoryCheckboxAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {

        CategoryEditActivity.this.finish();
    }
}
