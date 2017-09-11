package com.maoyuchaxue.catfishclubnewsapp.data;

import android.text.Html;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by YU_Jason on 2017/9/6.
 */


public class WebNewsContentSource implements NewsContentSource {
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmss"); // for parsing the date time
    private static final JsonParser JSON_PARSER = new JsonParser();
    private String apiUrl;
    private static final String ENTITY_LINK_PREFIX = "https://baike.baidu.com/item/";
    private static final int KEYWORD_LIM = 3;

    private static final List<String> replaceStrings = Arrays.asList("。 ","？ ", "！ ", "… ", "\\. ", "\\? ", "\\! ", "” ", "— ", "\" ");

    public WebNewsContentSource(String apiUrl){
        this.apiUrl = apiUrl;
    }

    private String buildDetailString(String id){
        return apiUrl + "?newsId=" + id;
    }

    private void addNameEntities(JsonArray json, ArrayList<String> list){
        for(JsonElement element : json)
            list.add(element.getAsJsonObject().get("word").getAsString());
    }

    private String[] getNameEntities(JsonObject json){
        ArrayList<String> list = new ArrayList<>();
        addNameEntities(json.get("persons").getAsJsonArray(), list);
        addNameEntities(json.get("organizations").getAsJsonArray(), list);
        addNameEntities(json.get("locations").getAsJsonArray(), list);

        return list.toArray(new String[0]);
    }

    private NewsContent createContentFromJSON(JsonObject json) throws JsonParseException, ParseException{
        NewsContent content = new NewsContent();

        content.setCrawlSource(json.get("crawl_Source").getAsString());
//        content.setCrawTime(DATE_FORMAT.parse(json.get("crawl_Time").getAsString()));
        try {
            content.setCategory(json.get("news_Category").getAsString());
        } catch(Exception e){
            content.setCategory("");
        }
        content.setJournalist(json.get("news_Journal").getAsString());

        String rawContent = json.get("news_Content").getAsString();

        // from catfish's line splitting
        for (String s : replaceStrings) {
            String target = s.replace(" ", "\n");
            rawContent = rawContent.replaceAll(s, target);
        }

        String[] lines = rawContent.split("\n");

        String finalContent = "";
        for (String s : lines) {
            finalContent += "<p>" + Html.escapeHtml("        " + s.replace("　", "  ").trim()) + "</p>";
        }

//        Log.i("WebNewsContentSource", finalContent);

        // add links to name entities
        String[] nameEntities = getNameEntities(json);
        StringBuilder contentWithLinks = new StringBuilder(finalContent);
        for(String entity : nameEntities){
            String escaped = Html.escapeHtml(entity);
//            Log.i("WebNewsContentSource", Html.escapeHtml(entity));
            int index = contentWithLinks.indexOf(escaped);
            if (index >= 0) {
                contentWithLinks.replace(index, index + escaped.length(),
                        "<a href=\"" + ENTITY_LINK_PREFIX + entity +
                                "\">" + entity + "</a>");
            }
        }
        content.setContentStr(contentWithLinks.toString());

        int i = 0;
        JsonArray keywordArray = json.get("Keywords").getAsJsonArray();
        ArrayList<String> keyList = new ArrayList<>();
        for(JsonElement element : keywordArray){
            if(i >= KEYWORD_LIM)
                break;
            JsonObject object = element.getAsJsonObject();
            keyList.add(object.get("word").getAsString());

            ++ i;
        }
        content.setKeywords(keyList.toArray(new String[0]));

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
