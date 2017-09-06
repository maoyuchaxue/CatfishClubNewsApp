package com.maoyuchaxue.catfishclubnewsapp.data;

import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by YU_Jason on 2017/9/6.
 */


public class WebNewsContentSource implements NewsContentSource {
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmss"); // for parsing the date time
    private static final JsonParser JSON_PARSER = new JsonParser();
    private String apiUrl;

    private String buildDetailString(String id){
        return apiUrl + "?newsId=" + id;
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
    public void close(){

    }
}
