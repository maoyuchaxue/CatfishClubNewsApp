package com.java.team17.data;

/**
 * A cache does nothing. It just stores all those records it has encountered in a cache
 * medium (e.g., a database or a file).
 * Created by YU_Jason on 2017/9/5.
 */

public interface NewsMetaInfoListCache extends NewsMetaInfoListSource {
    NewsMetaInfo[] getNewsMetaInfoListFromCache(int pageNo, String keyword, NewsCategoryTag categoryTag);

    void flush();
}
