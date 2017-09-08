package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsViewFragment;

public class NewsViewActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        Intent intent = getIntent();
        NewsMetaInfo metaInfo = (NewsMetaInfo)intent.getSerializableExtra("meta_info");
//        String newsID = intent.getExtras().getString("id");
//        String title = intent.getExtras().getString("title");
//        String newsID = metaInfo.getId();
//        String title = metaInfo.getTitle();

//        Fragment newFragment = NewsViewFragment.newInstance(newsID, title);
        Fragment newFragment = NewsViewFragment.newInstance(metaInfo);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.news_view, newFragment);
        transaction.commit();

        Toolbar toolbar = (Toolbar)findViewById(R.id.news_view_menu_toolbar);
        toolbar.setTitle("新闻详细");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(this);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.news_view_menu_share:
//                        TODO: implement intent to share URL here
                        break;
                    case R.id.news_view_menu_voice:
//                        TODO: implement voice here
                        break;
                }
                return true;
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_view_menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {

        NewsViewActivity.this.finish();
    }
}
