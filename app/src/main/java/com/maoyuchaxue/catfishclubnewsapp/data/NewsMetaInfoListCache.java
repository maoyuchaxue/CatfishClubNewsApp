package com.maoyuchaxue.catfishclubnewsapp.data;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public interface NewsMetaInfoListCache extends NewsMetaInfoListSource {
    NewsMetaInfo[] getNewsMetaInfoListFromCache(int pageNo, String keyword, NewsCategoryTag categoryTag);
    void flush();
}
