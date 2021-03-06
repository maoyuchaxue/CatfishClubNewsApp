package com.java.team17.data;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.java.team17.data.db.CacheDBOpenHelper;
import com.java.team17.data.exceptions.NewsSourceException;
import com.java.team17.data.util.Pair;

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

//        @Override
//        protected int getType(){
//            return -1;
//        }

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

        /**
         * Removes the record with a specified id.
         * @param id The specified id.
         * @return If the record is successfully removed, the return value would be <code>true</code>;
         * otherwise it would be <code>false</code>.
         * */
        @Override
        public boolean remove(String id) {
            return BookmarkManager.this.remove(id);
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

    public boolean remove(String id){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int affectedNo = db.delete(CacheDBOpenHelper.BOOKMARK_TABLE_NAME,
                CacheDBOpenHelper.FIELD_ID + "=?",
                new String[]{id});

        return affectedNo != 0;
    }

    public boolean add(NewsMetaInfo metaInfo){
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ContentValues change = new ContentValues();
        change.put(CacheDBOpenHelper.FIELD_INTRO, metaInfo.getIntro());
        change.put(CacheDBOpenHelper.FIELD_AUTHOR, metaInfo.getAuthor());
        change.put(CacheDBOpenHelper.FIELD_TITLE, metaInfo.getTitle());
        if(metaInfo.getCategoryTag() == null)
            change.put(CacheDBOpenHelper.FIELD_CATEGORY_TAG, 0);
        else
            change.put(CacheDBOpenHelper.FIELD_CATEGORY_TAG, metaInfo.getCategoryTag().getIndex());
        change.put(CacheDBOpenHelper.FIELD_URL, metaInfo.getUrl().toString());

        StringBuilder pictureStr = new StringBuilder();

        boolean first = true;
        for(URL url : metaInfo.getPictures()) {
            if (first) {
                first = false;
                pictureStr.append(url.toString());
            } else {
                pictureStr.append(";" + url.toString());
            }
        }

        change.put(CacheDBOpenHelper.FIELD_PICTURES, pictureStr.toString());

        change.put(CacheDBOpenHelper.FIELD_VIDEO, metaInfo.getVideo() == null ? "" : metaInfo.getVideo().toString());
        change.put(CacheDBOpenHelper.FIELD_LANG, metaInfo.getLang());
        change.put(CacheDBOpenHelper.FIELD_SRC, metaInfo.getSrcSite());

        change.put(CacheDBOpenHelper.FIELD_ID, metaInfo.getId());
        change.put(CacheDBOpenHelper.FIELD_TYPE, metaInfo.getType());

        long res = db.insert(CacheDBOpenHelper.BOOKMARK_TABLE_NAME, null, change);

        return res != -1;
    }

    public void modifyBookmarkAccordingToIntent(Intent intent) {
        boolean isModified = intent.getExtras().getBoolean("is_bookmark_modified", false);
        if (isModified) {
            boolean isInBookmark = intent.getExtras().getBoolean("final_bookmark_state", false);
            String id = intent.getExtras().getString("id", null);
            NewsMetaInfo metaInfo = (NewsMetaInfo)intent.getSerializableExtra("meta_info");
            if (isInBookmark) {
                if (metaInfo != null) {
                    add(metaInfo);
                }
            } else {
                if (id != null) {
                    remove(id);
                }
            }
        }
    }
}
