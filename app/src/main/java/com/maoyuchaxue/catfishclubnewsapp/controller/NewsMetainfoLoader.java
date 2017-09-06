package com.maoyuchaxue.catfishclubnewsapp.controller;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.maoyuchaxue.catfishclubnewsapp.data.NewsCategoryTag;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsCursor;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsList;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfoListSource;
import com.maoyuchaxue.catfishclubnewsapp.data.SourceNewsList;
import com.maoyuchaxue.catfishclubnewsapp.data.WebNewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.WebNewsMetaInfoListSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by catfish on 17/9/6.
 */

public class NewsMetainfoLoader extends AsyncTaskLoader<List<NewsCursor>> {
    private int loadBatch;
    private NewsCursor curCursor;
    private NewsList newsList;

    public NewsMetainfoLoader(Context context, int loadBatch, NewsCursor cursor, NewsList newsList) {
        super(context);
        this.loadBatch = loadBatch;
        this.curCursor = cursor;
        this.newsList = newsList;
    }

    @Override
    public List<NewsCursor> loadInBackground() {
        List<NewsCursor> cursors = new ArrayList<NewsCursor>();
        Log.i("catclub", "loading here");
        if (curCursor == null) {
            curCursor = newsList.getHeadCursor();
            Log.i("catclub", "get head cursor");
        }
        for (int i = 0; i < loadBatch; i++) {
            Log.i("catclub", "loaded" + String.valueOf(i));
            cursors.add(curCursor);
            curCursor = curCursor.next();
        }
        return cursors;
    }

    public NewsCursor getCurrentCursor() {
        return curCursor;
    }

}
