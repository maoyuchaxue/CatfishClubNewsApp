package com.maoyuchaxue.catfishclubnewsapp.data.rss;

import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfoListSource;
import com.maoyuchaxue.catfishclubnewsapp.data.db.CacheDBOpenHelper;

/**
 * Created by YU_Jason on 2017/9/11.
 */

public class RSSManager {
    private CacheDBOpenHelper openHelper;
    public RSSManager(CacheDBOpenHelper openHelper){
        this.openHelper = openHelper;
    }

    public NewsMetaInfoListSource getMetaInfoListSource(){
        return null;
    }

    public NewsContentSource getContentSource(){
        return null;
    }
}
