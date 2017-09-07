package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.flyco.tablayout.SlidingTabLayout;
import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.controller.CategoryViewPagerAdapter;
import com.maoyuchaxue.catfishclubnewsapp.controller.NewsMetainfoRecyclerViewAdapter;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCategoryTag;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCursor;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity
        implements NewsListFragment.OnFragmentInteractionListener {

    public static final int NEWS_VIEW_ACTIVITY = 0;
    public static final int SETTINGS_ACTIVITY = 1;
    public static final int CATEGORY_EDIT_ACTIVITY = 2;


    private SlidingTabLayout mTabLayout;
    private ViewPager mViewPager;
    private CategoryViewPagerAdapter mViewPagerAdapter;

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

                        startActivityForResult(intent, SETTINGS_ACTIVITY);
                        break;
                    case R.id.main_menu_search:
                        break;
                }

                return true;
            }
        });
    }

    private void initTabLayout() {
        mTabLayout = (SlidingTabLayout) findViewById(R.id.main_menu_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.main_menu_viewpager);

        mViewPagerAdapter = new CategoryViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setViewPager(mViewPager);

        refreshTabLayout();
    }

    private void refreshTabLayout() {
        mTabLayout.setCurrentTab(0);

        SharedPreferences sharedPreferences = getSharedPreferences("category", 0);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        ArrayList<String> titles = new ArrayList<String>();

        fragments.add(NewsListFragment.newInstance("", null, false));
        titles.add("综合");

        for (int i = 0; i < NewsCategoryTag.TITLES.length; i++) {
            boolean appears = sharedPreferences.getBoolean(NewsCategoryTag.TITLES_EN[i], true);
            if (appears) {
                fragments.add(NewsListFragment.newInstance(NewsCategoryTag.TITLES_EN[i], null, false));
                titles.add(NewsCategoryTag.TITLES[i]);
            }
        }

        String[] mtitles = new String[titles.size()];
        Object[] mobjs = titles.toArray();
        for (int i = 0; i < titles.size(); i++) {
            mtitles[i] = (String) mobjs[i];
        }

        mViewPagerAdapter.resetWithData(fragments, titles);
        mTabLayout.setViewPager(mViewPager, mtitles);
        mViewPagerAdapter.notifyDataSetChanged();
    }

    private void initCategoryButton() {
        Button categoryButton = (Button) findViewById(R.id.main_menu_category_button);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CategoryEditActivity.class);
                startActivityForResult(intent, CATEGORY_EDIT_ACTIVITY);
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
    public void onFragmentInteraction(NewsCursor cursor) {
        Intent intent = new Intent(MainActivity.this, NewsViewActivity.class);
        intent.putExtra("id", cursor.getNewsMetaInfo().getId());
        intent.putExtra("title", cursor.getNewsMetaInfo().getTitle());
        startActivityForResult(intent, NEWS_VIEW_ACTIVITY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEWS_VIEW_ACTIVITY:
                break;
            case SETTINGS_ACTIVITY:
                break;
            case CATEGORY_EDIT_ACTIVITY:
                refreshTabLayout();
                break;
            default:
                break;
        }
    }
}
