package com.maoyuchaxue.catfishclubnewsapp.data;

import android.graphics.Bitmap;

import java.io.IOException;
import java.net.URL;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public abstract class ResourceCache extends ResourceSource{
    protected abstract ResourceSource getFrontSource();
    @Override
    public byte[] getAsBlob(URL url) throws IOException{
        byte[] res = getAsBlobFromCache(url);
        if(res != null)
            return res;

        res = getFrontSource().getAsBlob(url);

        cache(url, res);

        return res;
    }

    @Override
    public Bitmap getAsThumbnail(URL url) throws IOException{
        return getBitmapFromBlob(getAsThumbnailBlob(url));
    }

    @Override
    public Bitmap getAsBitmap(URL url) throws IOException{
        return getBitmapFromBlob(getAsBitmapBlob(url));
    }


    @Override
    public byte[] getAsThumbnailBlob(URL url) throws IOException{
        byte[] res = getAsThumbnailBlobFromCache(url);
        if(res != null)
            return res;

        res = getFrontSource().getAsThumbnailBlob(url);

        cacheThumbnail(url, res);

        return res;
    }

    @Override
    public byte[] getAsBitmapBlob(URL url) throws IOException{
        byte[] res = getAsBitmapBlobFromCache(url);
        if(res != null)
            return res;

        res = getFrontSource().getAsBitmapBlob(url);

        cacheBitmap(url, res);

        return res;
    }


    protected abstract byte[] getAsBlobFromCache(URL url) throws IOException;
    protected abstract byte[] getAsThumbnailBlobFromCache(URL url) throws IOException;
    protected abstract byte[] getAsBitmapBlobFromCache(URL url) throws IOException;

    protected abstract void cache(URL url, byte[] blob);
    protected abstract void cacheThumbnail(URL url, byte[] blob);
    protected abstract void cacheBitmap(URL url, byte[] blob);

    public abstract void flush();
}
