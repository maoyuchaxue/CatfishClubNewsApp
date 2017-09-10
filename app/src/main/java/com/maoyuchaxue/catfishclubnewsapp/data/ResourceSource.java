package com.maoyuchaxue.catfishclubnewsapp.data;

import android.graphics.Bitmap;

import java.io.IOException;
import java.net.URL;

import android.graphics.BitmapFactory;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public abstract class ResourceSource {
    public static Bitmap getBitmapFromBlob(byte[] blob){
        return BitmapFactory.decodeByteArray(blob, 0, blob.length);
    }
    public Bitmap getAsBitmap(URL url) throws IOException{
        return filterBitmap(getBitmapFromBlob(getAsBlob(url)));
    }
    public abstract byte[] getAsBlob(URL url) throws IOException;

    protected Bitmap filterBitmap(Bitmap in){
        return in;
    }
}
