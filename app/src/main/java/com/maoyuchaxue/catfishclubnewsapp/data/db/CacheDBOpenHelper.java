package com.maoyuchaxue.catfishclubnewsapp.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.constraint.solver.Cache;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public class CacheDBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cache";
    private static final int DB_VERSION = 4;

    public static final String NEWS_TABLE_NAME = "news_cache";
    public static final String FIELD_ID = "id";
    public static final String FIELD_INTRO = "intro";
    public static final String FIELD_CATEGORY_TAG = "category_tag";
    public static final String FIELD_AUTHOR = "author";
    public static final String FIELD_URL = "url";
    public static final String FIELD_LANG = "lang";
    public static final String FIELD_SRC = "src";
    public static final String FIELD_PICTURES = "pictures";
    public static final String FIELD_VIDEO = "video";
    public static final String FIELD_CONTENT_STR = "content_str";
    public static final String FIELD_JOURNALIST = "journalist";
    public static final String FIELD_CRAWL_SRC = "crawl_src";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_TITLE = "title";

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
            FIELD_CATEGORY + " text, " +
            FIELD_TITLE + " text);";


    public static final String RESOURCES_TABLE_NAME = "resources_cache";
    public static final String FIELD_RESOURCE_URL = "url";
    public static final String FIELD_RESOURCE_BLOB = "rblob";
    private static final String RESOURCES_TABLE_CREATE = "create table if not exists " +
            RESOURCES_TABLE_NAME + " (" +
            FIELD_RESOURCE_URL + " text primary key, " +
            FIELD_RESOURCE_BLOB + " blob);";

    public static final String BOOKMARK_TABLE_NAME = "bookmark_cache";
    private static final String BOOKMARK_TABLE_CREATE = "create table if not exists " +
            BOOKMARK_TABLE_NAME + " (" +
            FIELD_ID + " text primary key, " +
            FIELD_INTRO + " text, " +
            FIELD_CATEGORY_TAG + " integer, " +
            FIELD_AUTHOR + " text, " +
            FIELD_TITLE + " text, " +
            FIELD_URL + " text, " +
            FIELD_LANG + " text, " +
            FIELD_SRC + " text, " +
            FIELD_PICTURES + " text, " +
            FIELD_VIDEO + " text);";


    private CacheDBOpenHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    private static CacheDBOpenHelper instance;

    public static synchronized CacheDBOpenHelper getInstance(Context context){
        if(instance == null)
            instance = new CacheDBOpenHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NEWS_TABLE_CREATE);
        db.execSQL(RESOURCES_TABLE_CREATE);
        db.execSQL(BOOKMARK_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //brute-force upgrade
        //remove old tables and create new ones
//        db.execSQL("drop table if exists " + NEWS_TABLE_NAME + ";");
//        db.execSQL(NEWS_TABLE_CREATE);
        if(newVersion >= 4)
            db.execSQL(BOOKMARK_TABLE_CREATE);

        if(newVersion >= 3){
            db.execSQL("drop table if exists " + NEWS_TABLE_NAME + ";");
            db.execSQL(NEWS_TABLE_CREATE);
//            db.execSQL("alter table " + NEWS_TABLE_NAME +
//            "add column " + FIELD_TITLE + " text;");
        }
        if(oldVersion == 1)
            db.execSQL(RESOURCES_TABLE_CREATE);
    }

    @Override
    protected void finalize(){
        close();
    }
}
