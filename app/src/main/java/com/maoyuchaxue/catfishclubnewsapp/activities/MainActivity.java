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
import android.widget.Button;

import com.flyco.tablayout.SlidingTabLayout;
import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.controller.CategoryViewPagerAdapter;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsListFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NewsListFragment.OnFragmentInteractionListener {

    private void initActionBar() {
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
    }

    private void initTabLayout() {
        SlidingTabLayout tabLayout = (SlidingTabLayout) findViewById(R.id.main_menu_tablayout);

        ViewPager viewPager = (ViewPager) findViewById(R.id.main_menu_viewpager);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(NewsListFragment.newInstance(-1, "", false));
        fragments.add(NewsListFragment.newInstance(1, "", false));
        fragments.add(NewsListFragment.newInstance(2, "", false));
        fragments.add(NewsListFragment.newInstance(3, "", false));
        fragments.add(NewsListFragment.newInstance(4, "", false));
        fragments.add(NewsListFragment.newInstance(5, "", false));
        fragments.add(NewsListFragment.newInstance(6, "", false));
        fragments.add(NewsListFragment.newInstance(7, "", false));
        fragments.add(NewsListFragment.newInstance(8, "", false));
        fragments.add(NewsListFragment.newInstance(9, "", false));

        ArrayList<String> titles = new ArrayList<String>();
        titles.add("ZH");
        titles.add("C1");
        titles.add("C2");
        titles.add("C3");
        titles.add("C1");
        titles.add("C2");
        titles.add("C3");
        titles.add("C1");
        titles.add("C2");
        titles.add("C3");

        CategoryViewPagerAdapter viewPagerAdapter = new CategoryViewPagerAdapter(getSupportFragmentManager(),
                fragments, titles);
        viewPager.setAdapter(viewPagerAdapter);

        String[] titleStrs = new String[10];
        Object[] objs = titles.toArray();
        for (int i = 0; i < objs.length; i++) {
            titleStrs[i] = (String) objs[i];
        }

        tabLayout.setViewPager(viewPager, titleStrs);
    }

    private void initCategoryButton() {
        Button categoryButton = (Button) findViewById(R.id.main_menu_category_button);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CategoryEditActivity.class);
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActionBar();
        initTabLayout();
        initCategoryButton();
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
