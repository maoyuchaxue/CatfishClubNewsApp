package com.maoyuchaxue.catfishclubnewsapp.data;

import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;

import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public class LocalStorageNewsMetaInfoListCache implements NewsMetaInfoListCache {
    @Override
    public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByPageNo(int pageNo, String keyword, NewsCategoryTag category) throws NewsSourceException {
        return new Pair<>(new NewsMetaInfo[0], 0);
    }

    @Override
    public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByIndex(int index, String keyword, NewsCategoryTag category) throws NewsSourceException {

        return new Pair<>(new NewsMetaInfo[0], 0);
    }


    @Override
    public int getPageSize() {
        return 0;
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public void refresh() {

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
