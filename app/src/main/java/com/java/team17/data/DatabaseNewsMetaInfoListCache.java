package com.java.team17.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.java.team17.data.db.CacheDBOpenHelper;
import com.java.team17.data.exceptions.NewsSourceException;
import com.java.team17.data.util.Pair;

import java.net.URL;

/**
 * A {@link NewsMetaInfoListCache} which uses a database as its medium.
 *
 *
 * Created by YU_Jason on 2017/9/7.
 */

public class DatabaseNewsMetaInfoListCache implements NewsMetaInfoListCache {
    NewsMetaInfoListSource frontSrc;
    CacheDBOpenHelper openHelper;

    public DatabaseNewsMetaInfoListCache(CacheDBOpenHelper openHelper, NewsMetaInfoListSource frontSrc){
        this.openHelper = openHelper;
        this.frontSrc = frontSrc;
    }

    @Override
    public NewsMetaInfo[] getNewsMetaInfoListFromCache(int pageNo, String keyword,
                                                       NewsCategoryTag categoryTag) {
        return new NewsMetaInfo[0];
    }

    @Override
    public void flush() {

    }

    @Override
    public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByPageNo(int pageNo, String keyword,
                                                                     NewsCategoryTag category, int type)
            throws NewsSourceException {
        // does the simple job of delegating the task to the front source
        Pair<NewsMetaInfo[], Integer> res =
                frontSrc.getNewsMetaInfoListByPageNo(pageNo, keyword, category, -1);
        // and maintains the record in a cache

        for(NewsMetaInfo metaInfo : res.first)
            writeMetaInfoToDatabase(metaInfo);

        return res;
    }

    private void writeMetaInfoToDatabase(NewsMetaInfo metaInfo){
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ContentValues change = new ContentValues();
        change.put(CacheDBOpenHelper.FIELD_INTRO, metaInfo.getIntro());
        change.put(CacheDBOpenHelper.FIELD_AUTHOR, metaInfo.getAuthor());
        change.put(CacheDBOpenHelper.FIELD_TITLE, metaInfo.getTitle());
        change.put(CacheDBOpenHelper.FIELD_CATEGORY_TAG, metaInfo.getCategoryTag().getIndex());
        change.put(CacheDBOpenHelper.FIELD_URL, metaInfo.getUrl().toString());

        StringBuilder pictureStr = new StringBuilder();
        for(URL url : metaInfo.getPictures())
            pictureStr.append(";" + url.toString());
        change.put(CacheDBOpenHelper.FIELD_PICTURES, pictureStr.toString());

        change.put(CacheDBOpenHelper.FIELD_VIDEO, metaInfo.getVideo() == null ? "" : metaInfo.getVideo().toString());
        change.put(CacheDBOpenHelper.FIELD_LANG, metaInfo.getLang());
        change.put(CacheDBOpenHelper.FIELD_SRC, metaInfo.getSrcSite());
        change.put(CacheDBOpenHelper.FIELD_TYPE, metaInfo.getType());


        db.beginTransaction();
        int affectedNo = db.update(CacheDBOpenHelper.NEWS_TABLE_NAME,
                change,
                CacheDBOpenHelper.FIELD_ID + "=?",
                new String[]{metaInfo.getId()}
                );
        if(affectedNo == 0){ // not existing yet
            change.put(CacheDBOpenHelper.FIELD_ID, metaInfo.getId());
            db.insert(CacheDBOpenHelper.NEWS_TABLE_NAME, null, change);
        }
        db.setTransactionSuccessful();
        db.endTransaction();

//        db.close();
    }

    @Override
    public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByIndex(int index,
                                                                    String keyword,
                                                                    NewsCategoryTag category)
            throws NewsSourceException {
        //TODO: the implementation is suspended.
        return null;
    }

    @Override
    public boolean isReversed() {
        return false;
    }

    @Override
    public int getPageSize() {
        return frontSrc.getPageSize();
    }

    @Override
    public int getCapacity() {
        return frontSrc.getCapacity();
    }

    @Override
    public void refresh() {

    }

    @Override
    public boolean remove(String id) {
        //TODO: I don't know whether it is necessary to implement this. I will check whether this class is useful or not.
        return false;
    }

    @Override
    public void close() throws NewsSourceException {
        frontSrc.close();
    }
}
