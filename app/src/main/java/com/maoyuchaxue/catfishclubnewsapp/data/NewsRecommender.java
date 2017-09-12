package com.maoyuchaxue.catfishclubnewsapp.data;

/**
 * Created by YU_Jason on 2017/9/10.
 */

public interface NewsRecommender {
    NewsList recommend(NewsContent content, int limit);
}
