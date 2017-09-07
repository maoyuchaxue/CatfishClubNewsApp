package com.maoyuchaxue.catfishclubnewsapp.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public class CacheDBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cache";
    private static final String NEWS_CONTENT_TABLE_NAME = "newscontent_cache";
    private static final String FIELD_ID = "id";
    private static final String FIELD_CONTENT_STR = "content_str";
    private static final String FIELD_JOURNALIST = "journalist";
    private static final String FIELD_CRAWL_SRC = "crawl_src";
    private static final String FIELD_CATEGORY = "category";

    private static final String NEWS_CONTENT_CREATE_TABLE = "create table if not exists " +
            NEWS_CONTENT_TABLE_NAME + " (" + FIELD_ID + " text primary key, " +
            FIELD_CONTENT_STR + " text, " +
            FIELD_JOURNALIST + " text, " +
            FIELD_CRAWL_SRC + " text, " +
            FIELD_CATEGORY + " text);";

    public CacheDBOpenHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NEWS_CONTENT_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //brute-force upgrade
        //remove old tables and create new ones
        db.execSQL("drop table if exists " + NEWS_CONTENT_TABLE_NAME);
        db.execSQL(NEWS_CONTENT_CREATE_TABLE);
    }
}
