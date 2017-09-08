package com.maoyuchaxue.catfishclubnewsapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.solver.Cache;

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
        public NewsContent getNewsContent(String id) throws NewsSourceException {
            SQLiteDatabase db = openHelper.getReadableDatabase();
            Cursor cursor = db.query(false, CacheDBOpenHelper.NEWS_TABLE_NAME, new String[]{CacheDBOpenHelper.FIELD_CONTENT_STR,
                            CacheDBOpenHelper.FIELD_JOURNALIST, CacheDBOpenHelper.FIELD_CATEGORY,
                            CacheDBOpenHelper.FIELD_CRAWL_SRC}, CacheDBOpenHelper.FIELD_ID + "=?",
                    new String[]{id}, null, null, null, null);
            NewsContent newsContent = null;
            if(cursor.moveToFirst()){ // found the record
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

            if(newsContent == null && frontSource != null){
                newsContent = frontSource.getNewsContent(id);
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

    public void add(NewsMetaInfo metaInfo, NewsContent newsContent){
        //TODO: add news to history
        SQLiteDatabase db = openHelper.getWritableDatabase();
        //assume it is not yet in database
        //TODO: deal with exceptions

        ContentValues change = new ContentValues();
                change.put(CacheDBOpenHelper.FIELD_CATEGORY, newsContent.getCategory());
        change.put(CacheDBOpenHelper.FIELD_CRAWL_SRC, newsContent.getCrawlSource());
        change.put(CacheDBOpenHelper.FIELD_JOURNALIST, newsContent.getJournalist());
        change.put(CacheDBOpenHelper.FIELD_CONTENT_STR, newsContent.getContentStr());
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
        change.put(CacheDBOpenHelper.FIELD_ID, metaInfo.getId());

        db.insert(CacheDBOpenHelper.NEWS_TABLE_NAME, null, change); //comfortable
    }

    public void remove(String id){
        //TODO: remove a piece of news from history
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
