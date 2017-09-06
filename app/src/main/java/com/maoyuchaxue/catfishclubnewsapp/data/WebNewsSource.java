package com.maoyuchaxue.catfishclubnewsapp.data;

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

public class WebNewsSource implements NewsSource {
    private static final int PAGE_SIZE = 20;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmss"); // for parsing the date time
    private String queryUrl, detailUrl;
    private static final JsonParser JSON_PARSER = new JsonParser();

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

//        System.out.println(queryStr);
        return queryStr.toString();
    }

    private String buildDetailString(String id){
        return detailUrl + "?newsId=" + id;
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
            for (int i = 0; i < pics.length; i++) {
                try {
                    picsUrl.add(new URL(pics[i]));
                } catch(MalformedURLException e){
                    e.printStackTrace();
                    // do nothing
                }
            }
            metaInfo.setPictures(picsUrl.toArray(new URL[0]));
        }

        // parse the url of the video
        String video = json.get("news_Video").getAsString();
        metaInfo.setVideo(video.equals("") ? null : new URL(video));

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

    private NewsContent createContentFromJSON(JsonObject json) throws JsonParseException, ParseException{
        NewsContent content = new NewsContent();

        content.setContentStr(json.get("news_Content").getAsString());
        content.setCrawlSource(json.get("crawl_Source").getAsString());
        content.setCrawTime(DATE_FORMAT.parse(json.get("crawl_Time").getAsString()));
        content.setCategory(json.get("news_Category").getAsString());
        content.setJournalist(json.get("news_Journal").getAsString());

        return content;
    }


    @Override
    public NewsMetaInfo[] getNewsMetaInfoList(int pageNo, String keyword,
                                              NewsCategoryTag category) throws NewsSourceException {
        NewsMetaInfo[] list = null;
        try {
            URL url = new URL(buildQueryString(pageNo, keyword, category));


            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

//            JsonObject haha = new JsonObject();

            JsonObject json = JSON_PARSER.parse(readContentFromConnection(con)).getAsJsonObject();
            list = createMetaInfoListFromJSON(json);

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

        return list;
    }

    /**
     *
     *
     * */
    @Override
    public NewsContent getNewsContent(String id) throws NewsSourceException {
        NewsContent content = null;
        try{
            URL url = new URL(buildDetailString(id));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            JsonObject json = JSON_PARSER.parse(readContentFromConnection(con)).getAsJsonObject();
            //JsonObject json = new JsonObject(readContentFromConnection(con));
            content = createContentFromJSON(json);

            con.disconnect();
        } catch(IOException e){
            //TODO: deal with the exception
        } catch(JsonParseException e){
            //TODO: deal with JsonParseExceptiong #2
        } catch(ParseException e){
            //TODO: deal with ParseException #2
        }
        return content;
    }

    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public void close() throws NewsSourceException {
        // simply do nothing
    }

    // for testing
    public static void main(String args[]) throws Exception{
        WebNewsSource newsSource = new WebNewsSource(
                "http://166.111.68.66:2042/news/action/query/latest",
                "http://166.111.68.66:2042/news/action/query/detail");
        for(NewsMetaInfo metaInfo : newsSource.getNewsMetaInfoList(1, null, null))
            System.out.println(metaInfo.getTitle());

    }
}
