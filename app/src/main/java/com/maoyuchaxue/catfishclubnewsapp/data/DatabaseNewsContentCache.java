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
    public NewsContent getNewsContent(String id) throws NewsSourceException {
        NewsContent newsContent = getNewsContentFromCache(id);
        if(newsContent == null)
            newsContent = cacheNewsContent(id);
        return newsContent;
    }

    @Override
    public NewsContent getNewsContentFromCache(String id) throws NewsSourceException {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query(false, CacheDBOpenHelper.NEWS_TABLE_NAME, new String[]{CacheDBOpenHelper.FIELD_CONTENT_STR,
                CacheDBOpenHelper.FIELD_JOURNALIST, CacheDBOpenHelper.FIELD_CATEGORY,
                CacheDBOpenHelper.FIELD_CRAWL_SRC}, CacheDBOpenHelper.FIELD_ID + "=?",
                new String[]{id}, null, null, null, null);
        NewsContent newsContent = null;
        if(cursor.moveToFirst() && cursor.getString(0) != null){ // found the record
            String contentStr = cursor.getString(0);
            String journalist = cursor.getString(1);
            String category = cursor.getString(2);
            String crawSrc = cursor.getString(3);
            cursor.close();


            try{
                newsContent = new NewsContent();
                newsContent.setJournalist(journalist);
                newsContent.setContentStr(contentStr);
                newsContent.setCategory(category);
                newsContent.setCrawlSource(crawSrc);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        return newsContent;
    }


    @Override
    public NewsContent cacheNewsContent(String id) throws NewsSourceException {
        NewsContent updatedContent = frontSource.getNewsContent(id);
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
        db.close();

    }
}
