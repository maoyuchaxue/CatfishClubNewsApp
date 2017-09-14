package com.java.team17.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public class CacheDBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "cache";
    private static final int DB_VERSION = 10;

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
    public static final String FIELD_KEYWORDS = "keywords";
    public static final String FIELD_TYPE = "type";

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
            FIELD_TITLE + " text, " +
            FIELD_KEYWORDS + " text, " +
            FIELD_TYPE + " integer not null default 0);";


    public static final String RESOURCES_TABLE_NAME = "resources_cache";
    public static final String FIELD_RESOURCE_URL = "url";
    public static final String FIELD_RESOURCE_TN_BLOB = "thumbnail_blob";
    public static final String FIELD_RESOURCE_BM_BLOB = "bitmap_blob";
    private static final String RESOURCES_TABLE_CREATE = "create table if not exists " +
            RESOURCES_TABLE_NAME + " (" +
            FIELD_RESOURCE_URL + " text primary key, " +
            FIELD_RESOURCE_TN_BLOB + " blob, " +
            FIELD_RESOURCE_BM_BLOB + " blob);";

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
            FIELD_VIDEO + " text," +
            FIELD_TYPE + " integer not null default 0);";

    public static final String RSS_TABLE_NAME = "rss_cache";
    public static final String FIELD_DESC = "description";
    public static final String FIELD_LINK = "link";

    private static final String RSS_TABLE_CREATE = "create table if not exists " +
            RSS_TABLE_NAME + " (" +
            FIELD_ID + " text primary key, " +
            FIELD_URL + " text);";
//            FIELD_TITLE + " text, " +
//            FIELD_LINK + " text, " +
//            FIELD_DESC + " text);";

    public static final String RSS_NEWS_TABLE_NAME = "rss_news_cache";
//    private static final String RSS_NEWS_TABLE_CREATE =
    private static final String RSS_NEWS_TABLE_CREATE = "create table if not exists " +
            RSS_NEWS_TABLE_NAME + " (" +
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

    public static final String KEYWORD_TABLE_NAME = "keywords";
    public static final String FIELD_KEYWORD = "keyword";
    public static final String FIELD_SCORE = "score";
    private static final String KEYWORD_TABLE_CREATE = "create table if not exists " +
            KEYWORD_TABLE_NAME + " (" +
            FIELD_KEYWORD + " text primary key, " +
            FIELD_SCORE + " real not null);";


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
        db.execSQL(RSS_TABLE_CREATE);
        db.execSQL(KEYWORD_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //brute-force upgrade
        //remove old tables and create new ones
//        db.execSQL("drop table if exists " + NEWS_TABLE_NAME + ";");
//        db.execSQL(NEWS_TABLE_CREATE);
        if(oldVersion < 4)
            db.execSQL(BOOKMARK_TABLE_CREATE);

        if(oldVersion < 3 || (newVersion >= 7 && oldVersion < 7) || newVersion == 8){
            db.execSQL("drop table if exists " + NEWS_TABLE_NAME + ";");
            db.execSQL(NEWS_TABLE_CREATE);
//            db.execSQL("alter table " + NEWS_TABLE_NAME +
//            "add column " + FIELD_TITLE + " text;");
        }

        if(newVersion == 8){
            db.execSQL("drop table if exists " + BOOKMARK_TABLE_NAME + ";");
            db.execSQL(BOOKMARK_TABLE_CREATE);
        }

        if(oldVersion < 5){
            db.execSQL("delete from " + NEWS_TABLE_NAME + ";");
        }

        if(oldVersion == 1)
            db.execSQL(RESOURCES_TABLE_CREATE);

        if(oldVersion < 6){
            db.execSQL("drop table if exists " + RESOURCES_TABLE_NAME + ";");
            db.execSQL(RESOURCES_TABLE_CREATE);
        }

        if(oldVersion < 8)
            db.execSQL(RSS_TABLE_CREATE);

        if(newVersion == 9){
            db.execSQL("drop table if exists " + RSS_TABLE_NAME + ";");
            db.execSQL(RSS_TABLE_CREATE);
        }

        if(newVersion == 10)
            db.execSQL(KEYWORD_TABLE_CREATE);
    }

    @Override
    protected void finalize(){
        close();
    }
}
