package com.java.team17.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.java.team17.R;
import com.java.team17.data.HistoryManager;
import com.java.team17.data.KeywordNewsRecommender;
import com.java.team17.data.NewsContent;
import com.java.team17.data.NewsContentSource;
import com.java.team17.data.NewsCursor;
import com.java.team17.data.NewsList;
import com.java.team17.data.NewsMetaInfo;
import com.java.team17.data.NewsMetaInfoListSource;
import com.java.team17.data.WebNewsContentSource;
import com.java.team17.data.WebNewsMetaInfoListSource;
import com.java.team17.data.db.CacheDBOpenHelper;
import com.java.team17.fragments.NewsViewFragment;

import java.util.List;

/**
 * Created by catfish on 17/9/11.
 */

public class RecommendListViewAdapter extends BaseAdapter
        implements LoaderManager.LoaderCallbacks<List<NewsCursor>> {

    private List<NewsCursor> cursors;
    private LoaderManager loaderManager;
    private Context context;
    private KeywordNewsRecommender recommender;
    private NewsList newsList = null;

    public RecommendListViewAdapter(Context context, LoaderManager loaderManager) {
        this.context = context;
        this.loaderManager = loaderManager;
        this.cursors = null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if (cursors != null) {
            return cursors.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.news_view_text_unit, parent);
        TextView titleView = (TextView) convertView.findViewById(R.id.recommend_title);
        TextView introView = (TextView) convertView.findViewById(R.id.recommend_intro);
        TextView authorView = (TextView) convertView.findViewById(R.id.recommend_author);

        NewsMetaInfo metaInfo = cursors.get(position).getNewsMetaInfo();
        titleView.setText(metaInfo.getTitle());
        introView.setText(metaInfo.getIntro());

        String author = metaInfo.getAuthor();
        if (author.isEmpty()) {
            author = "匿名作者";
        }
        authorView.setText(author);

        Log.i("recommend", metaInfo.getTitle());

        return convertView;
    }

    public void startLoading(NewsContent content, int limit) {
        Log.i("recommend", "start loading");
        NewsMetaInfoListSource listSource = new WebNewsMetaInfoListSource(
                "http://166.111.68.66:2042/news/action/query/search");

        NewsContentSource contentSource = HistoryManager.getInstance(CacheDBOpenHelper.
                getInstance(context.getApplicationContext())).getNewsContentSource(
                new WebNewsContentSource("http://166.111.68.66:2042/news/action/query/detail"));

        recommender = new KeywordNewsRecommender(listSource, contentSource);
        newsList = recommender.recommend(content, limit);

        loaderManager.initLoader(NewsViewFragment.NEWS_RECOMMEND_LOADER_ID, null, this)
                .forceLoad();
    }



    @Override
    public Loader<List<NewsCursor> > onCreateLoader(int id, Bundle args) {
        return new NewsMetainfoLoader(context, 5, null, newsList);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsCursor>> loader, List<NewsCursor> data) {
        Log.i("recommend", "load finished, data size: " + data.size());
        this.cursors = data;
        Log.i("recommend", data.get(0).getNewsMetaInfo().getTitle());
        notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<NewsCursor>> loader) {}
}
