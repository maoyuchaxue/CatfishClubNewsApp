package com.maoyuchaxue.catfishclubnewsapp.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.maoyuchaxue.catfishclubnewsapp.R;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCursor;
import com.maoyuchaxue.catfishclubnewsapp.fragments.NewsListFragment;

public class BookmarkListActivity extends AppCompatActivity
        implements View.OnClickListener, NewsListFragment.OnFragmentInteractionListener {

    private NewsListFragment bookmarkListFragment = null;
    private static final int NEWS_VIEW_ACTIVITY = 0;

    private void resetFragment() {
        bookmarkListFragment.reloadFromBeginning();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_list);

        Toolbar toolbar = (Toolbar)findViewById(R.id.bookmark_list_toolbar);
        toolbar.setTitle("收藏夹");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(this);

        bookmarkListFragment = NewsListFragment.newInstance("", null, NewsListFragment.BOOKMARK_FRAGMENT);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.bookmark_list_fragment, bookmarkListFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        BookmarkListActivity.this.finish();
    }

    @Override
    public void onFragmentInteraction(NewsCursor cursor) {
        Intent intent = new Intent(BookmarkListActivity.this, NewsViewActivity.class);
        intent.putExtra("meta_info", cursor.getNewsMetaInfo());
        startActivityForResult(intent, NEWS_VIEW_ACTIVITY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEWS_VIEW_ACTIVITY:
                resetFragment();
                break;
            default:
                break;
        }
    }

}
