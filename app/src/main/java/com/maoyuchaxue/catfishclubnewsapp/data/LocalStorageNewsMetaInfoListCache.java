package com.maoyuchaxue.catfishclubnewsapp.data;

import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public class LocalStorageNewsMetaInfoListCache implements NewsMetaInfoListCache {
    @Override
    public NewsMetaInfo[] getNewsMetaInfoList(int pageNo, String keyword, NewsCategoryTag category) throws NewsSourceException {
        return new NewsMetaInfo[0];
    }


    @Override
    public int getPageSize() {
        return 0;
    }

    @Override
    public void close() throws NewsSourceException {

    }

    @Override
    public NewsMetaInfo[] getNewsMetaInfoListFromCache(int pageNo, String keyword, NewsCategoryTag categoryTag) {
        return new NewsMetaInfo[0];
    }

    @Override
    public void flush() {

    }
}