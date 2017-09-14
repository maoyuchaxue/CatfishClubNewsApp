package com.java.team17.data;

import com.java.team17.data.exceptions.NewsSourceException;

/**
 * Created by YU_Jason on 2017/9/6.
 */

public interface NewsContentSource {
    void close() throws NewsSourceException;
    NewsContent getNewsContent(NewsMetaInfo metaInfo) throws NewsSourceException;
}
