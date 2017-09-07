package com.maoyuchaxue.catfishclubnewsapp.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public class CacheDBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cache";
    private static final String NEWS_TABLE_NAME = "news_cache";
    private static final String FIELD_ID = "id";
    private static final String FIELD_INTRO = "intro";
    private static final String FIELD_CATEGORY_TAG = "category_tag";
    private static final String FIELD_AUTHOR = "author";
    private static final String FIELD_URL = "url";
    private static final String FIELD_LANG = "lang";
    private static final String FIELD_SRC = "src";
    private static final String FIELD_PICTURES = "pictures";
    private static final String FIELD_VIDEO = "video";
    private static final String FIELD_CONTENT_STR = "content_str";
    private static final String FIELD_JOURNALIST = "journalist";
    private static final String FIELD_CRAWL_SRC = "crawl_src";
    private static final String FIELD_CATEGORY = "category";

    private static final String NEWS_TABLE_CREATE = "create table if not exists " +
            NEWS_TABLE_NAME + " (" + FIELD_ID + " text primary key, " +
            FIELD_INTRO + " text, " +
            FIELD_CATEGORY_TAG + " integer, " +
            FIELD_AUTHOR + " text, " +
            FIELD_URL + " text, " +
            FIELD_LANG + " text, " +
            FIELD_SRC + " text, " +
            FIELD_PICTURES + " text, " +
            FIELD_VIDEO + " text, " +
            FIELD_CONTENT_STR + " text, " +
            FIELD_JOURNALIST + " text, " +
            FIELD_CRAWL_SRC + " text, " +
            FIELD_CATEGORY + " text);";

    public CacheDBOpenHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NEWS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //brute-force upgrade
        //remove old tables and create new ones
        db.execSQL("drop table if exists " + NEWS_TABLE_NAME + ";");
        db.execSQL(NEWS_TABLE_CREATE);
    }
}
