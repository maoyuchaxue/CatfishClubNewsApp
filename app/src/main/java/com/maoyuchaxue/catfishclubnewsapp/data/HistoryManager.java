package com.maoyuchaxue.catfishclubnewsapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.solver.Cache;
import android.util.Log;

import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;
import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by YU_Jason on 2017/9/8.
 */

public class HistoryManager {
    private static HistoryManager instance;

    private CacheDBOpenHelper openHelper;
    private HistoryMetaInfoListSource metaInfoListSource;

    private HistoryManager(CacheDBOpenHelper openHelper){
        this.openHelper = openHelper;
    }


    private class HistoryNewsContentSource implements NewsContentSource{
        private NewsContentSource frontSource;

        public HistoryNewsContentSource(NewsContentSource frontSource){
            this.frontSource = frontSource;
        }

        @Override
        public void close() throws NewsSourceException {
            // do nothing ...
        }

        @Override
        public NewsContent getNewsContent(NewsMetaInfo metaInfo) throws NewsSourceException {
//            String id = metaInfo.getId();

            SQLiteDatabase db = openHelper.getReadableDatabase();
            Cursor cursor = db.query(false, CacheDBOpenHelper.NEWS_TABLE_NAME, new String[]{CacheDBOpenHelper.FIELD_CONTENT_STR,
                            CacheDBOpenHelper.FIELD_JOURNALIST, CacheDBOpenHelper.FIELD_CATEGORY,
                            CacheDBOpenHelper.FIELD_CRAWL_SRC, CacheDBOpenHelper.FIELD_KEYWORDS}, CacheDBOpenHelper.FIELD_ID + "=?",
                    new String[]{metaInfo.getId()}, null, null, null, null);
            NewsContent newsContent = null;
            if(cursor.moveToFirst()){ // found the record
                String contentStr = cursor.getString(0);
                String journalist = cursor.getString(1);
                String category = cursor.getString(2);
                String crawSrc = cursor.getString(3);
                String[] keywords = cursor.getString(4).split(";");
                cursor.close();

                try{
                    newsContent = new NewsContent();
                    newsContent.setJournalist(journalist);
                    newsContent.setContentStr(contentStr);
                    newsContent.setCategory(category);
                    newsContent.setCrawlSource(crawSrc);
                    newsContent.setKeywords(keywords);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            cursor.close();

            if(newsContent == null && frontSource != null){
                newsContent = frontSource.getNewsContent(metaInfo);
            }

            return newsContent;
        }
    }

    private class HistoryMetaInfoListSource extends DatabaseNewsMetaInfoListSource{
        private static final int PAGE_SIZE = 20;

        @Override
        protected String getTableName(){
            return CacheDBOpenHelper.NEWS_TABLE_NAME;
        }

        @Override
        protected CacheDBOpenHelper getOpenHelper() {
            return openHelper;
        }

        @Override
        public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByIndex(int index, String keyword, NewsCategoryTag category) throws NewsSourceException {
            //TODO

            return null;
        }

        @Override
        public boolean isReversed() {
            return false;
        }

        @Override
        public int getPageSize() {
            return PAGE_SIZE;
        }

        @Override
        public int getCapacity() {
            return 0;
        }

        @Override
        public void refresh() {

        }

        @Override
        public boolean remove(String id) {
            return HistoryManager.this.remove(id);
        }

        @Override
        public void close() throws NewsSourceException {

        }
    }

    public synchronized static HistoryManager getInstance(CacheDBOpenHelper openHelper){
        if(instance == null)
            instance = new HistoryManager(openHelper);
        return instance;
    }

    public NewsContentSource getNewsContentSource(){
        return getNewsContentSource(null);
    }

    public NewsContentSource getNewsContentSource(NewsContentSource frontSource){
        return new HistoryNewsContentSource(frontSource);
    }

    public NewsMetaInfoListSource getNewsMetaInfoListSource(){
        if(metaInfoListSource == null)
            metaInfoListSource = new HistoryMetaInfoListSource();
        return metaInfoListSource;
    }

    public boolean add(NewsMetaInfo metaInfo, NewsContent newsContent){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        //assume it is not yet in database
        //TODO: deal with exceptions

        ContentValues change = new ContentValues();

        boolean first = true;
        StringBuilder keyList = new StringBuilder();
        for(String key : newsContent.getKeywords()) {
            if(!first)
                keyList.append(';');
            keyList.append(key);
            first = false;
        }
        change.put(CacheDBOpenHelper.FIELD_KEYWORDS, keyList.toString());

        change.put(CacheDBOpenHelper.FIELD_CATEGORY, newsContent.getCategory());
        change.put(CacheDBOpenHelper.FIELD_CRAWL_SRC, newsContent.getCrawlSource());
        change.put(CacheDBOpenHelper.FIELD_JOURNALIST, newsContent.getJournalist());
        change.put(CacheDBOpenHelper.FIELD_CONTENT_STR, newsContent.getContentStr());
        change.put(CacheDBOpenHelper.FIELD_INTRO, metaInfo.getIntro());
        change.put(CacheDBOpenHelper.FIELD_AUTHOR, metaInfo.getAuthor());
        change.put(CacheDBOpenHelper.FIELD_TITLE, metaInfo.getTitle());
        if(metaInfo.getCategoryTag() == null)
            change.put(CacheDBOpenHelper.FIELD_CATEGORY_TAG, 0);
        else
            change.put(CacheDBOpenHelper.FIELD_CATEGORY_TAG, metaInfo.getCategoryTag().getIndex());
        change.put(CacheDBOpenHelper.FIELD_URL, metaInfo.getUrl().toString());
        change.put(CacheDBOpenHelper.FIELD_TYPE, metaInfo.getType());

        StringBuilder pictureStr = new StringBuilder();
        first = true;
        for(URL url : metaInfo.getPictures()) {
            if (first) {
                first = false;
                pictureStr.append(url.toString());
            } else {
                pictureStr.append(";" + url.toString());
            }
        };
        change.put(CacheDBOpenHelper.FIELD_PICTURES, pictureStr.toString());

        change.put(CacheDBOpenHelper.FIELD_VIDEO, metaInfo.getVideo() == null ? "" : metaInfo.getVideo().toString());
        change.put(CacheDBOpenHelper.FIELD_LANG, metaInfo.getLang());
        change.put(CacheDBOpenHelper.FIELD_SRC, metaInfo.getSrcSite());
        change.put(CacheDBOpenHelper.FIELD_ID, metaInfo.getId());

        long res = db.insert(CacheDBOpenHelper.NEWS_TABLE_NAME, null, change); //comfortable

        return res != -1;
    }

    public boolean remove(String id){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int affectedNo = db.delete(CacheDBOpenHelper.NEWS_TABLE_NAME,
                CacheDBOpenHelper.FIELD_ID + "=?",
                new String[]{id});

        return affectedNo != 0;
    }

    public boolean isInHistory(String id){
        SQLiteDatabase db = openHelper.getReadableDatabase();

        Cursor cursor = db.query(false, CacheDBOpenHelper.NEWS_TABLE_NAME,
                new String[]{CacheDBOpenHelper.FIELD_ID},
                CacheDBOpenHelper.FIELD_ID + "=?",
                new String[]{id},
                null, null, null, null);

        boolean inHistory = cursor.moveToFirst();
        cursor.close();

        return inHistory;
    }


}
