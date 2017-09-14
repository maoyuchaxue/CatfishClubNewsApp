package com.java.team17.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.java.team17.data.db.CacheDBOpenHelper;

import java.io.IOException;
import java.net.URL;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public class DatabaseResourceCache extends ResourceCache {
    private CacheDBOpenHelper openHelper;
    private ResourceSource frontSrc;

    public DatabaseResourceCache(CacheDBOpenHelper openHelper,
                                 ResourceSource frontSrc){
        this.openHelper = openHelper;
        this.frontSrc = frontSrc;
    }

    @Override
    public void flush() {

    }

    @Override
    protected ResourceSource getFrontSource() {
        return frontSrc;
    }


    @Override
    protected byte[] getAsBlobFromCache(URL url) throws IOException {
        return null;
    }

    @Override
    protected byte[] getAsThumbnailBlobFromCache(URL url) throws IOException{
        Log.i("loaded_pic", "get as tn from cache");
        SQLiteDatabase db = openHelper.getReadableDatabase();

        Cursor cursor = db.query(false, CacheDBOpenHelper.RESOURCES_TABLE_NAME,
                new String[]{CacheDBOpenHelper.FIELD_RESOURCE_TN_BLOB},
                CacheDBOpenHelper.FIELD_RESOURCE_URL + "=?",
                new String[]{url.toString()},
                null, null, null, null
                );
        byte[] res = null;
        if(cursor.moveToFirst() && !cursor.isNull(0))
            res = cursor.getBlob(0);
        cursor.close();

        if (res != null) {
            Log.i("loaded_pic", "get as tn from cache succeeded");
        } else {
            Log.i("loaded_pic", "cannot get from cache");
        }
        return res;
    }

        @Override
    protected byte[] getAsBitmapBlobFromCache(URL url) throws IOException{
        SQLiteDatabase db = openHelper.getReadableDatabase();

        Cursor cursor = db.query(false, CacheDBOpenHelper.RESOURCES_TABLE_NAME,
                new String[]{CacheDBOpenHelper.FIELD_RESOURCE_BM_BLOB},
                CacheDBOpenHelper.FIELD_RESOURCE_URL + "=?",
                new String[]{url.toString()},
                null, null, null, null
                );
        byte[] res = null;
        if(cursor.moveToFirst() && !cursor.isNull(0))
            res = cursor.getBlob(0);
        cursor.close();
        return res;
    }

    @Override
    protected void cache(URL url, byte[] blob) {
        // does nothing
    }

    @Override
    protected void cacheThumbnail(URL url, byte[] blob){
        SQLiteDatabase db = openHelper.getWritableDatabase();

        db.beginTransaction();
        ContentValues changes = new ContentValues();
        changes.put(CacheDBOpenHelper.FIELD_RESOURCE_TN_BLOB, blob);

        int affectedNo = db.update(CacheDBOpenHelper.RESOURCES_TABLE_NAME,
                changes, CacheDBOpenHelper.FIELD_RESOURCE_URL + "=?",
                new String[]{url.toString()});
        if(affectedNo == 0){
            changes.put(CacheDBOpenHelper.FIELD_RESOURCE_URL, url.toString());
            db.insert(CacheDBOpenHelper.RESOURCES_TABLE_NAME,
                    null,
                    changes);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }


        @Override
    protected void cacheBitmap(URL url, byte[] blob){
        SQLiteDatabase db = openHelper.getWritableDatabase();

        db.beginTransaction();
        ContentValues changes = new ContentValues();
        changes.put(CacheDBOpenHelper.FIELD_RESOURCE_BM_BLOB, blob);

        int affectedNo = db.update(CacheDBOpenHelper.RESOURCES_TABLE_NAME,
                changes, CacheDBOpenHelper.FIELD_RESOURCE_URL + "=?",
                new String[]{url.toString()});
        if(affectedNo == 0){
            changes.put(CacheDBOpenHelper.FIELD_RESOURCE_URL, url.toString());
            db.insert(CacheDBOpenHelper.RESOURCES_TABLE_NAME,
                    null,
                    changes);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    protected Bitmap filterBitmap(Bitmap in) {
        return null;
    }
}
