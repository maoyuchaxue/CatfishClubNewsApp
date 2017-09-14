package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
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
import com.maoyuchaxue.catfishclubnewsapp.data.BookmarkManager;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCategoryTag;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCursor;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.data.rss.RSSManager;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsListFragment;
import com.maoyuchaxue.catfishclubnewsapp.fragments.SettingsFragment;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;


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
    private MenuItem searchItem;
    private String globalKeyword = null;

    public void testNetworkState() {
        Context context = getApplicationContext();
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean networkAvailable = (networkInfo != null) && networkInfo.isAvailable();

        boolean isOffline = PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                .getBoolean("offline_mode", false);
        Log.i("offline", "is offline: " + String.valueOf(isOffline));

        if (!networkAvailable && !isOffline) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("没有网络")
                    .setContentText("没有网络就不能访问新闻辣，要不要进入离线模式呢>_<")
                    .setConfirmText("开启离线模式")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sDialog) {
                            PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                                    .edit().putBoolean("offline_mode", true).apply();

                            sDialog
                                    .setTitleText("没有网络")
                                    .setContentText("已开启离线模式")
                                    .setConfirmText("好的")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            MainActivity.this.recreate();
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    })
                    .show();
        }
    }

    public boolean initNightMode() {

        Boolean isNightMode = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("dark_style", false);

        if (isNightMode) {
            if (AppCompatDelegate.getDefaultNightMode() == -1 ||
                    AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                MainActivity.this.recreate();
                return false;
            }
        } else {
            if (AppCompatDelegate.getDefaultNightMode() == -1 ||
                    AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                MainActivity.this.recreate();
                return false;
            }
        }
        return true;
    }

    public void initDrawerFragment() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        SettingsFragment fragment = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.setting_drawer, fragment).commit();

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            private boolean tempIsOffline;

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}
            @Override
            public void onDrawerOpened(View drawerView) {
                tempIsOffline = PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .getBoolean("offline_mode", false);
                drawerView.setClickable(true);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                boolean curIsOffline = PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .getBoolean("offline_mode", false);
                if (tempIsOffline != curIsOffline) {
                    MainActivity.this.recreate();
                }
            }
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
        mViewPager.setOffscreenPageLimit(20);
        mTabLayout.setViewPager(mViewPager);

        refreshTabLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        testNetworkState();
    }


    private void refreshTabLayout() {

        Boolean isOffline = PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                .getBoolean("offline_mode", false);

        Log.i("offline", String.valueOf(isOffline));

        if (isOffline) {
//            in offline mode, search view is meaningless
            if (searchItem != null) {
                if (searchItem.isActionViewExpanded()) {
                    searchItem.collapseActionView();
                }
            }
        }

//        reset to the first tab, otherwise may crash when the focused category is removed
        mTabLayout.setCurrentTab(0);

        SharedPreferences sharedPreferences = getSharedPreferences("category", 0);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        ArrayList<String> titles = new ArrayList<String>();

        if (globalKeyword != null)
            Log.i("catclub", globalKeyword);


        if(!isOffline){
            fragments.add(NewsListFragment.newInstance("", globalKeyword, NewsListFragment.RECOMMEND_FRAGMENT));
            titles.add("推荐");
        }

        fragments.add(NewsListFragment.newInstance("rss", globalKeyword, isOffline ? NewsListFragment.DATABASE_FRAGMENT :
                NewsListFragment.RSS_FRAGMENT));
        titles.add("订阅");


        int fragmentType = isOffline ? NewsListFragment.DATABASE_FRAGMENT : NewsListFragment.WEB_FRAGMENT;
        fragments.add(NewsListFragment.newInstance("", globalKeyword, fragmentType));
        titles.add("综合");

        for (int i = 0; i < NewsCategoryTag.TITLES.length; i++) {
            boolean appears = sharedPreferences.getBoolean(NewsCategoryTag.TITLES_EN[i], true);
            if (appears) {
                fragments.add(NewsListFragment.newInstance(NewsCategoryTag.TITLES_EN[i], globalKeyword, fragmentType));
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

    private void initRSSFeed() {
        String preferencedUrls = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).
                getString("rss_urls", "http://feeds.bbci.co.uk/news/world/rss.xml");
        String preferencedLabels = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).
                getString("rss_labels", "BBC World");

        HashSet<String> defaultLabels = new HashSet<>();
        defaultLabels.add("BBC World");
        Set<String> preferencedSelectedLabels = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).
                getStringSet("rss_selected", defaultLabels);

        Log.i("rss", preferencedUrls);
        Log.i("rss", preferencedLabels);
        Log.i("rss", preferencedSelectedLabels.toString());

        List<String> tlabels = new ArrayList<>();
        List<String> turls = new ArrayList<>();

        String[] labelStrs = preferencedLabels.split("\\|");
        for (String s : labelStrs) {
            if (!s.isEmpty()) {
                tlabels.add(s);
            }
        }

        String[] urlStrs = preferencedUrls.split("\\|");
        for (String s : urlStrs) {
            if (!s.isEmpty()) {
                turls.add(s);
            }
        }

        try {
            RSSManager manager = RSSManager.getInstance(CacheDBOpenHelper.getInstance(getApplicationContext()));
            for (int i = 0; i < tlabels.size(); i++) {
                if (preferencedSelectedLabels.contains(tlabels.get(i))) {
                    manager.addRSSFeed(tlabels.get(i), new URL(turls.get(i)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("offline", "created!");

        if (initNightMode()) {
//            if initNightMode returns false, then the activity is invalid.
            initDrawerFragment();
            initRSSFeed();
            initActionBar();
            initTabLayout();
            initCategoryButton();
            testNetworkState();
        }

        // for testing
//        try{
//            RSSManager.getInstance(CacheDBOpenHelper.getInstance(getApplicationContext())).addRSSFeed("BBC World",
//                    new URL("http://feeds.bbci.co.uk/news/world/rss.xml"));
//        } catch(Exception e){
//            e.printStackTrace();
//        }
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
        searchItem = menu.findItem(R.id.main_menu_search);
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Boolean isOfflineMode = PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .getBoolean("offline_mode", false);
                if (isOfflineMode) {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("不能搜索>_<")
                            .setContentText("离线模式下无法搜索的")
                            .show();
                    return false;
                }
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
        initTabLayout();
//        refreshTabLayout();
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
