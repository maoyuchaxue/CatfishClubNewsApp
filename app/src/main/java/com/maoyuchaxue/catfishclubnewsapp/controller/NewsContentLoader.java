package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.maoyuchaxue.catfishclubnewsapp.ApplicationState;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

import java.util.List;

/**
 * Created by catfish on 17/9/7.
 */

public class NewsContentLoader extends AsyncTaskLoader<NewsContent> {
    private String newsID;

    public NewsContentLoader(Context context, String newsID) {
        super(context);
        this.newsID = newsID;
    }

    @Override
    public NewsContent loadInBackground() {
        NewsContent content = null;
        try {
            content = ApplicationState.contentSource.getNewsContent(newsID);
        } catch (NewsSourceException e) {
        }
        return content;
    }

}
