package com.maoyuchaxue.catfishclubnewsapp.data;


import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public interface NewsSource {
    NewsMetaInfo[] getNewsMetaInfoList(int pageNo, String keyword, NewsCategoryTag category) throws NewsSourceException;
    NewsContent getNewsContent(String id) throws NewsSourceException;
    int getPageSize();

    void close() throws NewsSourceException;
}
