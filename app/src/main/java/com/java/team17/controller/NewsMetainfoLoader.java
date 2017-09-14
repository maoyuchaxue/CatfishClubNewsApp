package com.java.team17.controller;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.java.team17.data.NewsCursor;
import com.java.team17.data.NewsList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by catfish on 17/9/6.
 */

public class NewsMetainfoLoader extends AsyncTaskLoader<List<NewsCursor>> {
    private int loadBatch;
    private NewsCursor curCursor;
    private NewsList newsList;
    private boolean finished;

    public NewsMetainfoLoader(Context context, int loadBatch, NewsCursor cursor, NewsList newsList) {
        super(context);
        this.loadBatch = loadBatch;
        this.curCursor = cursor;
        this.newsList = newsList;
        this.finished = false;
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public List<NewsCursor> loadInBackground() {
        List<NewsCursor> cursors = new ArrayList<NewsCursor>();

        if (curCursor == null && !finished) {
            newsList.refresh();
            curCursor = newsList.getHeadCursor();
        }
        for (int i = 0; i < loadBatch; i++) {
            if (curCursor == null) {
                Log.i("recommend", "finished2");
                finished = true;
                break;
            }
            cursors.add(curCursor);
            curCursor = curCursor.next();
        }

        if (curCursor == null) {
            Log.i("recommend", "finished");
            finished = true;
        }

        Log.i("recommend", String.valueOf(cursors.size()));
        return cursors;
    }

    public NewsCursor getCurrentCursor() {
        return curCursor;
    }

}
