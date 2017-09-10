package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.flyco.tablayout.SlidingTabLayout;
import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.controller.CategoryViewPagerAdapter;
import com.maoyuchaxue.catfishclubnewsapp.controller.NewsMetainfoRecyclerViewAdapter;
import com.maoyuchaxue.catfishclubnewsapp.data.BookmarkManager;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCategoryTag;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCursor;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsListFragment;
import com.maoyuchaxue.catfishclubnewsapp.fragments.SettingsFragment;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity
        implements NewsListFragment.OnFragmentInteractionListener {

    public static final int NEWS_VIEW_ACTIVITY = 0;
    public static final int SETTINGS_ACTIVITY = 1;
    public static final int CATEGORY_EDIT_ACTIVITY = 2;
    public static final int BOOKMARK_LIST_ACTIVITY = 3;


    private SlidingTabLayout mTabLayout;
    private ViewPager mViewPager;
    private CategoryViewPagerAdapter mViewPagerAdapter;
    private SearchView mSearchView;
    private DrawerLayout mDrawerLayout;
    private String globalKeyword = null;

    public void initDrawerFragment() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        SettingsFragment fragment = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.setting_drawer, fragment).commit();

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.setClickable(true);
            }
            @Override
            public void onDrawerClosed(View drawerView) {}
            @Override
            public void onDrawerStateChanged(int newState) {}
        });
        mDrawerLayout.closeDrawers();
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_menu_toolbar);
        toolbar.setTitle("新闻列表");
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_white_24dp)) ;
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.main_menu_settings:
//                        intent = new Intent(MainActivity.this, SettingsActivity.class);
//                        startActivityForResult(intent, SETTINGS_ACTIVITY);
                        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                            mDrawerLayout.closeDrawers();
                        } else {
                            mDrawerLayout.openDrawer(Gravity.START);
                        }
                        break;
                    case R.id.main_menu_search:
                        break;
                    case R.id.main_menu_bookmarks:
                        intent = new Intent(MainActivity.this, BookmarkListActivity.class);
                        startActivityForResult(intent, BOOKMARK_LIST_ACTIVITY);
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
        mViewPager.setOffscreenPageLimit(0);
        mTabLayout.setViewPager(mViewPager);

        refreshTabLayout();
    }

    private void refreshTabLayout() {
        mTabLayout.setCurrentTab(0);

        SharedPreferences sharedPreferences = getSharedPreferences("category", 0);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        ArrayList<String> titles = new ArrayList<String>();

        if (globalKeyword != null)
            Log.i("catclub", globalKeyword);
        fragments.add(NewsListFragment.newInstance("", globalKeyword, NewsListFragment.WEB_FRAGMENT));
        titles.add("综合");

        for (int i = 0; i < NewsCategoryTag.TITLES.length; i++) {
            boolean appears = sharedPreferences.getBoolean(NewsCategoryTag.TITLES_EN[i], true);
            if (appears) {
                fragments.add(NewsListFragment.newInstance(NewsCategoryTag.TITLES_EN[i], globalKeyword, NewsListFragment.WEB_FRAGMENT));
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

        initDrawerFragment();
        initActionBar();
        initTabLayout();
        initCategoryButton();
    }

    @Override
    public void onFragmentInteraction(NewsCursor cursor) {
        Intent intent = new Intent(MainActivity.this, NewsViewActivity.class);
        intent.putExtra("meta_info", cursor.getNewsMetaInfo());
        startActivityForResult(intent, NEWS_VIEW_ACTIVITY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.main_menu_search);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setKeywordForFragments(null);
                return true;
            }
        });

        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setKeywordForFragments(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                TODO: add history recommendation
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEWS_VIEW_ACTIVITY:
                BookmarkManager.getInstance(CacheDBOpenHelper.
                        getInstance(getApplicationContext())).modifyBookmarkAccordingToIntent(data);
                break;
            case SETTINGS_ACTIVITY:
                break;
            case CATEGORY_EDIT_ACTIVITY:
                refreshTabLayout();
                break;
            case BOOKMARK_LIST_ACTIVITY:
                break;
            default:
                break;
        }
    }

    public void setKeywordForFragments(String keyword) {
        if (keyword == null) {
            globalKeyword = null;
        } else if (!keyword.isEmpty()) {
            try {
                globalKeyword = URLEncoder.encode(keyword, "utf-8");
            } catch (Exception e) {}
        } else {
            return;
        }
        refreshTabLayout();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
