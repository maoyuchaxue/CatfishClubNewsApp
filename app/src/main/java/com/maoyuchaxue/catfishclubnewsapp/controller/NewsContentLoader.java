package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.maoyuchaxue.catfishclubnewsapp.ApplicationState;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

import java.util.List;

/**
 * Created by catfish on 17/9/7.
 */

public class NewsContentLoader extends AsyncTaskLoader<NewsContent> {
    private String newsID;
    private NewsContentSource contentSource;

    public NewsContentLoader(Context context, String newsID, NewsContentSource contentSource) {
        super(context);
        this.newsID = newsID;
        this.contentSource = contentSource;
    }

    @Override
    public NewsContent loadInBackground() {
        NewsContent content = null;
        try {
            content = contentSource.getNewsContent(newsID);
        } catch (NewsSourceException e) {
        }
        return content;
    }

}
