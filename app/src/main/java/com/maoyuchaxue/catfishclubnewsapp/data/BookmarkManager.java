package com.maoyuchaxue.catfishclubnewsapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.solver.Cache;

import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;
import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;

import java.net.URL;

/**
 * Created by YU_Jason on 2017/9/8.
 */

public class BookmarkManager {
    private static BookmarkManager instance;
    private CacheDBOpenHelper openHelper;
    private NewsMetaInfoListSource metaInfoListSource;

    private BookmarkManager(CacheDBOpenHelper openHelper){
        this.openHelper = openHelper;
    }

    private class BookmarkNewsMetaInfoListSource extends DatabaseNewsMetaInfoListSource{
        private static final int PAGE_SIZE = 20;

        @Override
        protected String getTableName(){
            return CacheDBOpenHelper.BOOKMARK_TABLE_NAME;
        }

        @Override
        protected CacheDBOpenHelper getOpenHelper(){
            return openHelper;
        }

        @Override
        public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByIndex(int index, String keyword, NewsCategoryTag category) throws NewsSourceException {
            //TODO: do nothing?
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
        public boolean remove(String id) {
            //TODO: remove in BookmarkMetaInfoListSource
            return false;
        }

        @Override
        public void close() throws NewsSourceException {

        }
    }

    public static BookmarkManager getInstance(CacheDBOpenHelper openHelper){
        if(instance == null)
            instance = new BookmarkManager(openHelper);
        return instance;
    }


    public NewsMetaInfoListSource getNewsMetaInfoListSource(){
        if(metaInfoListSource == null)
            metaInfoListSource = new BookmarkNewsMetaInfoListSource();
        return metaInfoListSource;
    }

    public boolean isBookmarked(String id){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query(false, CacheDBOpenHelper.BOOKMARK_TABLE_NAME,
                new String[]{CacheDBOpenHelper.FIELD_ID},
                CacheDBOpenHelper.FIELD_ID + "=?",
                new String[]{id},
                null, null, null, null);
        boolean bookmarked = cursor.moveToFirst();
        cursor.close();

        return bookmarked;
    }

    public boolean bookmark(NewsMetaInfo metaInfo){
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

        change.put(CacheDBOpenHelper.FIELD_ID, metaInfo.getId());

        long res = db.insert(CacheDBOpenHelper.BOOKMARK_TABLE_NAME, null, change);

        return res != -1;
    }
}
