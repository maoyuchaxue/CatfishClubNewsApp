package com.maoyuchaxue.catfishclubnewsapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.solver.Cache;
import android.util.Log;

import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public class DatabaseNewsContentCache implements NewsContentCache {
    private NewsContentSource frontSource;
    private CacheDBOpenHelper openHelper;


    public DatabaseNewsContentCache(CacheDBOpenHelper openHelper, NewsContentSource frontSource){
        this.openHelper = openHelper;
        this.frontSource = frontSource;
    }

    @Override
    public void close() throws NewsSourceException {

    }

    @Override
    public NewsContent getNewsContent(NewsMetaInfo metaInfo) throws NewsSourceException {
        String id = metaInfo.getId();

        NewsContent newsContent = getNewsContentFromCache(id);
        if(newsContent == null)
            newsContent = cacheNewsContent(metaInfo);
        return newsContent;
    }
@Override
    public NewsContent getNewsContentFromCache(String id) throws NewsSourceException {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query(false, CacheDBOpenHelper.NEWS_TABLE_NAME, new String[]{CacheDBOpenHelper.FIELD_CONTENT_STR,
                CacheDBOpenHelper.FIELD_JOURNALIST, CacheDBOpenHelper.FIELD_CATEGORY,
                CacheDBOpenHelper.FIELD_CRAWL_SRC, CacheDBOpenHelper.FIELD_KEYWORDS}, CacheDBOpenHelper.FIELD_ID + "=?",
                new String[]{id}, null, null, null, null);
        NewsContent newsContent = null;
        if(cursor.moveToFirst() && !cursor.isNull(0)){ // found the record
            String contentStr = cursor.getString(0);
            String journalist = cursor.getString(1);
            String category = cursor.getString(2);
            String crawSrc = cursor.getString(3);
            String[] keys = cursor.getString(4).split(";");


            cursor.close();


            try{
                newsContent = new NewsContent();
                newsContent.setJournalist(journalist);
                newsContent.setContentStr(contentStr);
                newsContent.setCategory(category);
                newsContent.setCrawlSource(crawSrc);
                newsContent.setKeywords(keys);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        cursor.close();
//        db.close();
        return newsContent;
    }


    @Override
    public NewsContent cacheNewsContent(NewsMetaInfo metaInfo) throws NewsSourceException {
        String id = metaInfo.getId();

        NewsContent updatedContent = frontSource.getNewsContent(metaInfo);
        writeNewsContentToDatabase(id, updatedContent);

        return updatedContent;
    }

    @Override
    public void flush() {

    }

    private void writeNewsContentToDatabase(String id, NewsContent newsContent) throws NewsSourceException{

        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
//        Cursor cursor = db.query(false, CacheDBOpenHelper.NEWS_TABLE_NAME, new String[]{},
//                CacheDBOpenHelper.FIELD_ID + "=?", new String[]{id},
//                null, null, null, null);
//        if(cursor.moveToFirst()){
//            db.execSQL();
//        }
//        Log.i("FISH", newsContent.getContentStr());
        ContentValues change = new ContentValues();
        change.put(CacheDBOpenHelper.FIELD_CATEGORY, newsContent.getCategory());
        change.put(CacheDBOpenHelper.FIELD_CRAWL_SRC, newsContent.getCrawlSource());
        change.put(CacheDBOpenHelper.FIELD_JOURNALIST, newsContent.getJournalist());
        change.put(CacheDBOpenHelper.FIELD_CONTENT_STR, newsContent.getContentStr());

        // put the keys
        boolean first = true;
        StringBuilder keyList = new StringBuilder();
        for(String key : newsContent.getKeywords()) {
            if(!first)
                keyList.append(';');
            keyList.append(key);
            first = false;
        }
        change.put(CacheDBOpenHelper.FIELD_KEYWORDS, keyList.toString());

        int affectedNo = db.update(CacheDBOpenHelper.NEWS_TABLE_NAME,
                change,
                CacheDBOpenHelper.FIELD_ID + "=?",
                new String[]{id});

        if(affectedNo == 0){ // not existing so far
            // insert a new row
            change.put(CacheDBOpenHelper.FIELD_ID, id);
            Log.i("FISH", Long.toString(db.insert(CacheDBOpenHelper.NEWS_TABLE_NAME,
                    null,
                    change)));

        }
        db.setTransactionSuccessful();
        db.endTransaction();
//        db.close();

    }
}
