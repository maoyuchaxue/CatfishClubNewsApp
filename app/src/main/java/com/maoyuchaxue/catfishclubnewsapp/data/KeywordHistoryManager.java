package com.maoyuchaxue.catfishclubnewsapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.solver.Cache;

import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;
import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;

/**
 * Created by YU_Jason on 2017/9/14.
 */

public class KeywordHistoryManager {
    private static KeywordHistoryManager instance;
    private static final int KEYWORD_USE_LIM = 5;
    private static final int KEYWORD_STORE_LIM = 50;
    private static final double SCORE_DECAY_RATE = 0.8;
    private static final double RAND_RANGE = 0.1;

    private CacheDBOpenHelper openHelper;
    private KeywordHistoryManager(CacheDBOpenHelper openHelper){
        this.openHelper = openHelper;
    }

    public synchronized static KeywordHistoryManager getInstance(CacheDBOpenHelper openHelper){
        if(instance == null)
            instance = new KeywordHistoryManager(openHelper);
        return instance;
    }

    public NewsList getRecommendedNewsList(NewsMetaInfoListSource metaInfoListSource, NewsContentSource contentSource){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query(false, CacheDBOpenHelper.KEYWORD_TABLE_NAME,
                new String[]{CacheDBOpenHelper.FIELD_KEYWORD},
                null, null, null, null, CacheDBOpenHelper.FIELD_SCORE + " desc",
                Integer.toString(KEYWORD_USE_LIM));
        String searchKey = "";
        boolean first = true;
        if(cursor.moveToFirst()){
            do{
                if(!first)
                    searchKey += "%20";
                first = false;
                searchKey += cursor.getString(0);
            } while(cursor.moveToNext());
        }
        cursor.close();

        return new SourceNewsList(metaInfoListSource, contentSource, searchKey, null, 0);
    }

    public void addKeys(String[] keys){
        int cnt = 0;

        SQLiteDatabase db = openHelper.getWritableDatabase();

        db.beginTransaction();
        Cursor cursor = db.query(false, CacheDBOpenHelper.KEYWORD_TABLE_NAME,
                new String[]{CacheDBOpenHelper.FIELD_KEYWORD, CacheDBOpenHelper.FIELD_SCORE},
                null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                ++ cnt;

                double ns = cursor.getDouble(1) * SCORE_DECAY_RATE;
                String keyword = cursor.getString(0);

                ContentValues change = new ContentValues();
                change.put(CacheDBOpenHelper.FIELD_SCORE, ns);
                db.update(CacheDBOpenHelper.KEYWORD_TABLE_NAME, change,
                        CacheDBOpenHelper.FIELD_KEYWORD + "=?", new String[]{keyword});
            } while(cursor.moveToNext());
        }
        cursor.close();

        db.setTransactionSuccessful();
        db.endTransaction();

        db.beginTransaction();
//        long cnt = db.update("")
        double currentScore = (Math.random() - 0.5) * RAND_RANGE + 1.0;
        for(String key : keys){
            cursor = db.query(false, CacheDBOpenHelper.KEYWORD_TABLE_NAME,
                    new String[]{CacheDBOpenHelper.FIELD_SCORE},
                    CacheDBOpenHelper.FIELD_KEYWORD + "=?",
                    new String[]{key}, null, null, null, null);
            if(cursor.moveToFirst()){
                double ns = cursor.getDouble(0) + currentScore;

                ContentValues change = new ContentValues();
                change.put(CacheDBOpenHelper.FIELD_SCORE, ns);

                db.update(CacheDBOpenHelper.KEYWORD_TABLE_NAME, change,
                        CacheDBOpenHelper.FIELD_KEYWORD + "=?", new String[]{key});
            } else{
                ContentValues change = new ContentValues();
                change.put(CacheDBOpenHelper.FIELD_SCORE, currentScore);
                change.put(CacheDBOpenHelper.FIELD_KEYWORD, key);

                db.insert(CacheDBOpenHelper.KEYWORD_TABLE_NAME,
                        null, change);

                ++ cnt;
            }
            cursor.close();

            currentScore *= SCORE_DECAY_RATE;
        }

        int removeCnt = Math.max(0, cnt - KEYWORD_STORE_LIM);
        if(removeCnt > 0) {
            cursor = db.query(false, CacheDBOpenHelper.KEYWORD_TABLE_NAME,
                    new String[]{CacheDBOpenHelper.FIELD_KEYWORD},
                    null, null, null, null, CacheDBOpenHelper.FIELD_SCORE + " asc", Integer.toString(removeCnt));
            cursor.moveToNext();
            do{
                db.delete(CacheDBOpenHelper.KEYWORD_TABLE_NAME, CacheDBOpenHelper.FIELD_KEYWORD,
                        new String[]{cursor.getString(0)});
            } while(cursor.moveToNext());
            cursor.close();
        }

        db.endTransaction();
    }

//    private class KeywordHistoryRecommendedNewsMetaInfoListSource implements NewsMetaInfoListSource{
//
//        @Override
//        public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByPageNo(int pageNo, String keyword, NewsCategoryTag category, int type) throws NewsSourceException {
//
//        }
//
//        @Override
//        public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByIndex(int index, String keyword, NewsCategoryTag category) throws NewsSourceException {
//            return null;
//        }
//
//        @Override
//        public boolean isReversed() {
//            return false;
//        }
//
//        @Override
//        public int getPageSize() {
//            return 0;
//        }
//
//        @Override
//        public int getCapacity() {
//            return 0;
//        }
//
//        @Override
//        public void refresh() {
//
//        }
//
//        @Override
//        public boolean remove(String id) {
//            return false;
//        }
//
//        @Override
//        public void close() throws NewsSourceException {
//
//        }
//    }
}
