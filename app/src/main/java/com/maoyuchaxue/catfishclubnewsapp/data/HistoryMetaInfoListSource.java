package com.maoyuchaxue.catfishclubnewsapp.data;

import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;
import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;

/**
 * Created by YU_Jason on 2017/9/8.
 */

public class HistoryMetaInfoListSource implements NewsMetaInfoListSource {
    @Override
    public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByPageNo(int pageNo, String keyword, NewsCategoryTag category) throws NewsSourceException {
        return null;
    }

    @Override
    public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByIndex(int index, String keyword, NewsCategoryTag category) throws NewsSourceException {
        return null;
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
}
