package com.java.team17.controller;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.java.team17.data.NewsContent;
import com.java.team17.data.NewsContentSource;
import com.java.team17.data.NewsMetaInfo;
import com.java.team17.data.exceptions.NewsSourceException;

/**
 * Created by catfish on 17/9/7.
 */

public class NewsContentLoader extends AsyncTaskLoader<NewsContent> {
    private NewsMetaInfo newsMetaInfo;
    private NewsContentSource contentSource;

    public NewsContentLoader(Context context, NewsMetaInfo newsMetaInfo, NewsContentSource contentSource) {
        super(context);
        this.newsMetaInfo = newsMetaInfo;
        this.contentSource = contentSource;
    }

    @Override
    public NewsContent loadInBackground() {
        NewsContent content = null;
        try {
            content = contentSource.getNewsContent(newsMetaInfo);
        } catch (NewsSourceException e) {
        }
        return content;
    }

}
