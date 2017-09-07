package com.maoyuchaxue.catfishclubnewsapp.data;

import android.content.Context;

import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

/**
 * Created by YU_Jason on 2017/9/7.
 */

public class DatabaseNewsContentCache implements NewsContentCache {
    private NewsContentSource frontSource;


    //TODO: the signature needs updating to incorporate the parameters for the database
    public DatabaseNewsContentCache(Context context, NewsContentSource frontSource){
        this.frontSource = frontSource;
    }

    @Override
    public void close() throws NewsSourceException {

    }

    @Override
    public NewsContent getNewsContent(String id) throws NewsSourceException {
        NewsContent newsContent = getNewsContentFromCache(id);
        if(newsContent == null)
            newsContent = cacheNewsContent(id);
        return newsContent;
    }

    @Override
    public NewsContent getNewsContentFromCache(String id) throws NewsSourceException {
        //TODO: getNewsContentFromCache
        return null;
    }

    @Override
    public NewsContent cacheNewsContent(String id) throws NewsSourceException {
        NewsContent updatedContent = frontSource.getNewsContent(id);
        writeNewsContentToDatabase(id, updatedContent);

        return updatedContent;
    }

    @Override
    public void flush() {

    }

    private void writeNewsContentToDatabase(String id, NewsContent content){
        //TODO: writeNewsContentToDatabase
    }
}
