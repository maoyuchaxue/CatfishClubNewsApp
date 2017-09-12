package com.maoyuchaxue.catfishclubnewsapp.data;

import android.util.Log;

import java.net.URLEncoder;

/**
 * Created by YU_Jason on 2017/9/10.
 */

public class KeywordNewsRecommender implements NewsRecommender {
    private NewsMetaInfoListSource source;
    private NewsContentSource contentSource;
    public KeywordNewsRecommender(NewsMetaInfoListSource source, NewsContentSource contentSource){
        this.source = source;
        this.contentSource = contentSource;
    }

    @Override
    public NewsList recommend(NewsContent content, int limit) {
        StringBuilder searchKey = new StringBuilder();
        boolean isFirst = true;
        for(String key : content.getKeywords()) {
            if (isFirst) {
                searchKey.append(key);
                isFirst = false;
            } else {
                searchKey.append("%20"+key);
            }
        }

        Log.i("recommend", searchKey.toString());
        return new SourceNewsList(source, contentSource, searchKey.toString(), null);
    }
}
