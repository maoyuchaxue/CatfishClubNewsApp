package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsListFragment;

public class MainActivity extends AppCompatActivity implements NewsListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_menu_toolbar);
        toolbar.setTitle("新闻列表");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.main_menu_settings:
//                        TODO: implement intent to setting activity here
                        break;
                    case R.id.main_menu_search:
                        break;
                }
                return true;
            }
        });

        setSupportActionBar(toolbar);

        Fragment newFragment = NewsListFragment.newInstance(-1, "", false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.news_info_list, newFragment);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(String newsID) {
        Intent intent = new Intent(MainActivity.this, NewsViewActivity.class);
        intent.putExtra("id", newsID);
        startActivityForResult(intent, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
