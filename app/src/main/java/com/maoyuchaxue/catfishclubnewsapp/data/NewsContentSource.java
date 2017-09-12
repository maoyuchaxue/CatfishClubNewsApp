package com.maoyuchaxue.catfishclubnewsapp.data;

import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

/**
 * Created by YU_Jason on 2017/9/6.
 */

public interface NewsContentSource {
    void close() throws NewsSourceException;
    NewsContent getNewsContent(NewsMetaInfo metaInfo) throws NewsSourceException;
}
