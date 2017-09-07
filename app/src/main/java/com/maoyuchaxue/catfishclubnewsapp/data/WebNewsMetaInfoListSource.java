package com.maoyuchaxue.catfishclubnewsapp.data;

import android.util.Log;

import com.maoyuchaxue.catfishclubnewsapp.data.util.Pair;

import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public class WebNewsMetaInfoListSource implements NewsMetaInfoListSource {
    private static final int PAGE_SIZE = 20;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmss"); // for parsing the date time
    private static final JsonParser JSON_PARSER = new JsonParser();

    private String apiUrl;
    private int capacity;

    /**
     * @param apiUrl The URL for the fetching the service API.
     *
     * */
    public WebNewsMetaInfoListSource(String apiUrl){
        this.apiUrl = apiUrl;
        refresh();
    }

    private String buildQueryString(int pageNo, String keyword, NewsCategoryTag category){
        StringBuilder queryStr = new StringBuilder(apiUrl);
        queryStr.append("?pageSize=" + PAGE_SIZE);
        queryStr.append("&pageNo=" + pageNo);
        if(keyword != null)
            queryStr.append("&keyword=" + keyword);
        if(category != null)
            queryStr.append("&category=" + category.getIndex());

//        System.out.println(queryStr);
        return queryStr.toString();
    }

    private String readContentFromConnection(URLConnection con) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        while(true){
            int c = reader.read();
            if(c == -1)
                break;
            content.append((char)c);
        }
    //    System.out.println(content.toString());
        return content.toString();
    }

    private NewsMetaInfo createMetaInfoFromJson(JsonObject json) throws JsonParseException, MalformedURLException, ParseException{
        NewsMetaInfo metaInfo = new NewsMetaInfo(json.get("news_ID").getAsString());
        metaInfo.setCategoryTag(NewsCategoryTag.getCategoryByTitle(json.get("newsClassTag").getAsString()));
        metaInfo.setSrcSite(json.get("news_Source").getAsString());
        metaInfo.setTitle(json.get("news_Title").getAsString());
        metaInfo.setUrl(new URL(json.get("news_URL").getAsString()));
        metaInfo.setAuthor(json.get("news_Author").getAsString());
        metaInfo.setLang(json.get("lang_Type").getAsString());

        // parse the urls of the pictures
        String picStr = json.get("news_Pictures").getAsString().trim();
        if(picStr.equals(""))
            metaInfo.setPictures(new URL[0]);
        else {
            String[] pics = picStr.split(";");
            ArrayList<URL> picsUrl = new ArrayList<URL>();
            for (String pu : pics) {
                try {
                    picsUrl.add(new URL(pu));
                } catch(MalformedURLException e){
                    System.err.println("(A malformed URL found!)");
                    //e.printStackTrace();
                    // do nothing
                }
            }
            metaInfo.setPictures(picsUrl.toArray(new URL[0]));
        }

        // parse the url of the video
        String video = json.get("news_Video").getAsString();
        //System.err.println(video);
        try {
            metaInfo.setVideo(video.equals("") ? null : new URL(video));
        } catch(MalformedURLException e){
            metaInfo.setVideo(null);
        }

        metaInfo.setIntro(json.get("news_Intro").getAsString());

        metaInfo.setTime(DATE_FORMAT.parse(json.get("news_Time").getAsString()));

        return metaInfo;
    }

    private NewsMetaInfo[] createMetaInfoListFromJSON(JsonObject json) throws JsonParseException, MalformedURLException, ParseException{
        JsonArray array = json.getAsJsonArray("list");
        int n = array.size(); // the number of items in the list
        NewsMetaInfo[] list = new NewsMetaInfo[n];

        for(int i = 0; i < n; i ++)
            list[i] = createMetaInfoFromJson(array.get(i).getAsJsonObject());

        return list;
    }


    @Override
    public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByPageNo(int pageNo, String keyword,
                                                                     NewsCategoryTag category) throws NewsSourceException {
        NewsMetaInfo[] list = null;
        int st = 0;
        try {
            URL url = new URL(buildQueryString(pageNo, keyword, category));


            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

//            JsonObject haha = new JsonObject();

//            System.err.println(pageNo);
            JsonObject json = JSON_PARSER.parse(readContentFromConnection(con)).getAsJsonObject();
            list = createMetaInfoListFromJSON(json);
            // compute the index of the starting record
            st = json.get("totalRecords").getAsInt() - (pageNo - 1) * PAGE_SIZE - 1;

            con.disconnect();
        } catch(IOException e){
            // TODO: a more elaborate exception with details
            e.printStackTrace();
            throw new NewsSourceException();
        } catch(JsonParseException e){
            //TODO: deal with JsonParseExceptiong
        } catch(ParseException e){
            //TODO: deal with ParseException
        }

        return new Pair<>(list, st);
    }

    @Override
    public Pair<NewsMetaInfo[], Integer> getNewsMetaInfoListByIndex(int index, String keyword, NewsCategoryTag category) throws NewsSourceException {
        // TODO: do nothing about it
        return new Pair<>(new NewsMetaInfo[0], 0);
    }


    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void refresh(){
        // TODO: update the capacity
    }

    @Override
    public void close() throws NewsSourceException {
        // simply do nothing
    }
/*
    // for testing
    public static void main(String args[]) throws Exception{
        WebNewsMetaInfoListSource newsSource = new WebNewsMetaInfoListSource(
                "http://166.111.68.66:2042/news/action/query/latest",
                "http://166.111.68.66:2042/news/action/query/detail");
        for(NewsMetaInfo metaInfo : newsSource.getNewsMetaInfoListByPageNo(1, null, null))
            System.out.println(metaInfo.getTitle());

    }
    */
}
