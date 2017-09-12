package com.maoyuchaxue.catfishclubnewsapp.data;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import android.graphics.BitmapFactory;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public abstract class ResourceSource {
    public static Bitmap getBitmapFromBlob(byte[] blob){
        if(blob == null)
            return null;
        return BitmapFactory.decodeByteArray(blob, 0, blob.length);
    }
    public Bitmap getAsThumbnail(URL url) throws IOException{
        return filterThumbnail(getBitmapFromBlob(getAsBlob(url)));
    }

    public Bitmap getAsBitmap(URL url) throws IOException{
        return filterBitmap(getBitmapFromBlob(getAsBlob(url)));
    }


    public abstract byte[] getAsBlob(URL url) throws IOException;

    protected Bitmap filterThumbnail(Bitmap in){
        return in;
    }
    protected Bitmap filterBitmap(Bitmap in){
        return in;
    }
    protected byte[] compress(Bitmap in) throws IOException{
        if(in == null)
            return null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        in.compress(Bitmap.CompressFormat.JPEG, 70, out);
        out.close();

        return out.toByteArray();
    }

    protected byte[] getAsThumbnailBlob(URL url) throws IOException{
        return compress(filterThumbnail(getBitmapFromBlob(getAsBlob(url))));
    }

    protected byte[] getAsBitmapBlob(URL url) throws IOException{
        return compress(filterBitmap(getBitmapFromBlob(getAsBlob(url))));
    }
}
