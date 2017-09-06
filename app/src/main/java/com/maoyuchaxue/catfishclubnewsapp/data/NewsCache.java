package com.maoyuchaxue.catfishclubnewsapp.data;

import com.maoyuchaxue.catfishclubnewsapp.data.NewsSource;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public interface NewsCache extends NewsSource {
    NewsMetaInfo[] getNewsMetaInfoListFromCache(int pageNo, String keyword, NewsCategoryTag categoryTag);
    void flush();
}
