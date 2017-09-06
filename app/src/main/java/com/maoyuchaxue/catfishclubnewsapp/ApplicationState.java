package com.maoyuchaxue.catfishclubnewsapp;

import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.WebNewsContentSource;

/**
 * Only for development.
 * Created by YU_Jason on 2017/9/7.
 */

public class ApplicationState {
    /**
     * Only for transferring data between Activities during development and testing.
     * It will possibly not be incorporated as a final solution.
     *
     * */
    public static NewsContentSource contentSource =
            new WebNewsContentSource("http://166.111.68.66:2042/news/action/query/detail");
}
