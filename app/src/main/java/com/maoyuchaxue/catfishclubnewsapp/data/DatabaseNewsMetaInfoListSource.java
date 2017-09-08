package com.maoyuchaxue.catfishclubnewsapp.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;
import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by YU_Jason on 2017/9/8.
 */

public abstract class DatabaseNewsMetaInfoListSource implements NewsMetaInfoListSource {
    protected abstract CacheDBOpenHelper getOpenHelper();
    protected abstract String getTableName();

    @Override
    public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByPageNo(int pageNo, String keyword, NewsCategoryTag category) throws NewsSourceException {
        SQLiteDatabase db = getOpenHelper().getReadableDatabase();

        String selection;
        String[] selectionArgs;
        // TODO: support no searching now
        selection = null;
        selectionArgs = null;

        String order = null;
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
                        CacheDBOpenHelper.FIELD_AUTHOR
                },
                selection,
                selectionArgs,
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

                metaInfo.setCategoryTag(NewsCategoryTag.CATEGORIES[cursor.getInt(4)]);
                try {
                    metaInfo.setUrl(new URL(cursor.getString(5)));
                    metaInfo.setVideo(new URL(cursor.getString(7)));
                } catch(MalformedURLException e){
                    e.printStackTrace();
                }

                metaInfo.setTitle(cursor.getString(6));
                metaInfo.setLang(cursor.getString(8));
                metaInfo.setAuthor(cursor.getString(9));

                metaInfos.add(metaInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.i("metainfo", String.valueOf(metaInfos.size()));
        return new Pair<>(metaInfos.toArray(new NewsMetaInfo[0]),
                getPageSize() * pageNo - getPageSize() + 1);
    }
}
