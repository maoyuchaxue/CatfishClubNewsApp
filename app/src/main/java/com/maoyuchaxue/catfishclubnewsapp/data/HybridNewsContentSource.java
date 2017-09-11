package com.maoyuchaxue.catfishclubnewsapp.data;

import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by YU_Jason on 2017/9/11.
 */

public class HybridNewsContentSource implements NewsContentSource {
    private NewsContentSource sourceForUrl, sourceForNonUrl;
    public HybridNewsContentSource(NewsContentSource sourceForUrl, NewsContentSource sourceForNonUrl){
        this.sourceForNonUrl = sourceForNonUrl;
        this.sourceForUrl = sourceForUrl;
    }

    @Override
    public void close() throws NewsSourceException {

    }

    private boolean isUrl(String id){
        try{
            new URL(id);
        } catch(MalformedURLException e){
            return false;
        }
        return true;
    }

    @Override
    public NewsContent getNewsContent(String id) throws NewsSourceException {
        // check the form of id
        if(isUrl(id))
            return sourceForUrl.getNewsContent(id);
        return sourceForNonUrl.getNewsContent(id);
    }
}
