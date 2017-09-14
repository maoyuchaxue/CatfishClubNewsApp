package com.java.team17.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.java.team17.data.db.CacheDBOpenHelper;
import com.java.team17.data.exceptions.NewsSourceException;
import com.java.team17.data.util.Pair;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by YU_Jason on 2017/9/8.
 */

public abstract class DatabaseNewsMetaInfoListSource implements NewsMetaInfoListSource {
    protected abstract CacheDBOpenHelper getOpenHelper();
    protected abstract String getTableName();

//    protected abstract int getType();

    @Override
    public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByPageNo(int pageNo, String keyword, NewsCategoryTag category, int type) throws NewsSourceException {
        SQLiteDatabase db = getOpenHelper().getReadableDatabase();

//        int type = getType();

        String selection = "";
        ArrayList<String> selectionArgs = new ArrayList<>();

        if (category == null) {
            Log.i("offline", "null");
        } else {
            Log.i("offline", category.toString());
        }

        boolean first = true;
        if(category != null){
            selection += (first ? "" : " and " )+ CacheDBOpenHelper.FIELD_CATEGORY_TAG + "=?";
            first = false;
            selectionArgs.add(
                    Integer.toString(category.getIndex())
            );
        }

        if(type != -1){
            selection += (first ? "" : " and ") + CacheDBOpenHelper.FIELD_TYPE + "=?";
            first = false;
            selectionArgs.add(Integer.toString(type));
        }

        String order = "rowid desc";
        String limit = (getPageSize() * pageNo - getPageSize()) + "," + getPageSize();

        Cursor cursor = db.query(false, getTableName(),
                new String[]{CacheDBOpenHelper.FIELD_ID,
                        CacheDBOpenHelper.FIELD_SRC,
                        CacheDBOpenHelper.FIELD_INTRO,
                        CacheDBOpenHelper.FIELD_PICTURES,
                        CacheDBOpenHelper.FIELD_CATEGORY_TAG,
                        CacheDBOpenHelper.FIELD_URL,
                        CacheDBOpenHelper.FIELD_TITLE,
                        CacheDBOpenHelper.FIELD_VIDEO,
                        CacheDBOpenHelper.FIELD_LANG,
                        CacheDBOpenHelper.FIELD_AUTHOR,
                        CacheDBOpenHelper.FIELD_TYPE
                },
                selection,
                selectionArgs.toArray(new String[0]),
                null,
                null,
                order,
                limit
        );
        ArrayList<NewsMetaInfo> metaInfos = new ArrayList<NewsMetaInfo>();
        if(cursor.moveToFirst()) {
            do {
                NewsMetaInfo metaInfo = new NewsMetaInfo(cursor.getString(0));
                metaInfo.setSrcSite(cursor.getString(1));
                metaInfo.setIntro(cursor.getString(2));

                Log.i("catclub", cursor.getString(3));
                String s = cursor.getString(3).trim();
                if (s.isEmpty())
                    metaInfo.setPictures(new URL[0]);
                else {
                    String[] pictures = cursor.getString(3).split(";");
                    URL[] urls = new URL[pictures.length];
                    for (int i = 0; i < pictures.length; i++)
                        try {
                            urls[i] = new URL(pictures[i]);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    metaInfo.setPictures(urls);
                }

                int catInt = cursor.getInt(4);
                metaInfo.setCategoryTag(catInt == 0 ? null :
                        NewsCategoryTag.CATEGORIES[catInt - 1]);
                try {
                    metaInfo.setUrl(new URL(cursor.getString(5)));
                } catch(MalformedURLException e){
                    e.printStackTrace();
                }
                try {
                    metaInfo.setVideo(new URL(cursor.getString(7)));
                } catch(MalformedURLException e){
                    e.printStackTrace();
                }

                metaInfo.setTitle(cursor.getString(6));
                metaInfo.setLang(cursor.getString(8));
                metaInfo.setAuthor(cursor.getString(9));
                metaInfo.setType(cursor.getInt(10));

                metaInfos.add(metaInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.i("metainfo", String.valueOf(metaInfos.size()));
        return new Pair<>(metaInfos.toArray(new NewsMetaInfo[0]),
                getPageSize() * pageNo - getPageSize() + 1);
    }
}
