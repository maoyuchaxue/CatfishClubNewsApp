package com.maoyuchaxue.catfishclubnewsapp.data;

import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public class WebNewsSource implements NewsSource {
    private static int PAGE_SIZE = 20;
    private String queryUrl, detailUrl;

    /**
     * @param queryUrl The URL for the query service API.
     * @param detailUrl The URL for the detail service API.
     *
     * */
    public WebNewsSource(String queryUrl, String detailUrl){
        this.queryUrl = queryUrl;
        this.detailUrl = detailUrl;
    }

    private String buildQueryString(int pageNo, String keyword, NewsCategoryTag category){
        StringBuilder queryStr = new StringBuilder(queryUrl);
        queryStr.append("?pageSize=" + PAGE_SIZE);
        queryStr.append("&pageNo=" + pageNo);
        if(keyword != null)
            queryStr.append("&keyword=" + keyword);
        if(category != null)
            queryStr.append("&category=" + category.getIndex());

        return queryStr.toString();
    }

    private String buildDetailString(String id){
        return detailUrl + "?newsId=" + id;
    }


    @Override
    public NewsMetaInfo[] getNewsMetaInfoList(int pageNo, String keyword,
                                              NewsCategoryTag category) throws NewsSourceException {
        try {
            URL url = new URL(buildQueryString(pageNo, keyword, category));


            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            //TODO: manipulate the returned json data


            con.disconnect();
        } catch(IOException e){
            // TODO: a more elaborate exception with details
            throw new NewsSourceException();
        }

        return new NewsMetaInfo[0];
    }

    /**
     *
     *
     * */
    @Override
    public NewsContent getNewsContent(String id) throws NewsSourceException {
        try{
            URL url = new URL(buildDetailString(id));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            //TODO: manipulate the returned json data to fetch the news content

            con.disconnect();
        } catch(IOException e){
            //TODO: deal with the exception
        }
        return null;
    }

    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public void close() throws NewsSourceException {
        // simply do nothing
    }
}
