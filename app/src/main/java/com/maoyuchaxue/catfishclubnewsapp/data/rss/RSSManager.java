package com.maoyuchaxue.catfishclubnewsapp.data.rss;

import android.database.sqlite.SQLiteDatabase;

import com.maoyuchaxue.catfishclubnewsapp.data.DatabaseNewsContentCache;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfoListSource;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

/**
 * Created by YU_Jason on 2017/9/11.
 */

public class RSSManager {
    private CacheDBOpenHelper openHelper;
    private static final WebPageNewsContentSource FRONT_CONTENT_SOURCE =
            new WebPageNewsContentSource();

    private class RSSNewsContentSource implements NewsContentSource{
        @Override
        public void close() throws NewsSourceException {
        }

        @Override
        public NewsContent getNewsContent(String id) throws NewsSourceException {
            SQLiteDatabase db = openHelper.getReadableDatabase();
//            db.query(false, )
            return null;
        }

    }


    public RSSManager(CacheDBOpenHelper openHelper){
        this.openHelper = openHelper;
    }

    public NewsMetaInfoListSource getNewsMetaInfoListSource(){
        return null;
    }
    public NewsContentSource getNewsContentSource() {
        return new RSSNewsContentSource();
    }
}

