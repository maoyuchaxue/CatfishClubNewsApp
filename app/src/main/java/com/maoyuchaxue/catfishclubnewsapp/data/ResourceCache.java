package com.maoyuchaxue.catfishclubnewsapp.data;

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

    protected abstract byte[] getAsBlobFromCache(URL url) throws IOException;

    protected abstract void cache(URL url, byte[] blob);
    public abstract void flush();
}
