package com.maoyuchaxue.catfishclubnewsapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;

import java.io.ByteArrayOutputStream;
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
    public Bitmap getAsBitmap(URL url) throws IOException {
        byte[] cached = getAsBlobFromCache(url);
        Bitmap bitmap;
        if(cached == null){
            bitmap = frontSrc.getAsBitmap(url);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            cache(url, out.toByteArray());
            out.close();

        } else
            bitmap = getBitmapFromBlob(cached);

        return bitmap;
    }

    @Override
    protected ResourceSource getFrontSource() {
        return frontSrc;
    }

    @Override
    protected byte[] getAsBlobFromCache(URL url) throws IOException {
        SQLiteDatabase db = openHelper.getReadableDatabase();

        Cursor cursor = db.query(false, CacheDBOpenHelper.RESOURCES_TABLE_NAME,
                new String[]{CacheDBOpenHelper.FIELD_RESOURCE_BLOB},
                CacheDBOpenHelper.FIELD_RESOURCE_URL + "=?",
                new String[]{url.toString()},
                null, null, null, null
                );
        byte[] res = null;
        if(cursor.moveToFirst())
            res = cursor.getBlob(0);
        cursor.close();
        return res;
    }

    @Override
    protected void cache(URL url, byte[] blob) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        db.beginTransaction();
        ContentValues changes = new ContentValues();
        changes.put(CacheDBOpenHelper.FIELD_RESOURCE_BLOB, blob);

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
