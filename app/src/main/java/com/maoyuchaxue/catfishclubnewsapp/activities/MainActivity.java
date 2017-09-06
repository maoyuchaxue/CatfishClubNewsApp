package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.controller.CategoryViewPagerAdapter;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsListFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewsListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_menu_toolbar);
        toolbar.setTitle("新闻列表");

        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.main_menu_settings:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivityForResult(intent, 1);
                        break;
                    case R.id.main_menu_search:
                        break;
                }

                return true;
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_menu_tablayout);

        ViewPager viewPager = (ViewPager) findViewById(R.id.main_menu_viewpager);


        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(NewsListFragment.newInstance(-1, "", false));
        fragments.add(NewsListFragment.newInstance(1, "", false));
        fragments.add(NewsListFragment.newInstance(2, "", false));
        fragments.add(NewsListFragment.newInstance(3, "", false));

        ArrayList<String> titles = new ArrayList<String>();
        titles.add("ZH");
        titles.add("C1");
        titles.add("C2");
        titles.add("C3");

        CategoryViewPagerAdapter viewPagerAdapter = new CategoryViewPagerAdapter(getSupportFragmentManager(),
                fragments, titles);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

//        Fragment newFragment = NewsListFragment.newInstance(-1, "", false);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//        transaction.replace(R.id.news_info_list, newFragment);
//        transaction.commit();
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
