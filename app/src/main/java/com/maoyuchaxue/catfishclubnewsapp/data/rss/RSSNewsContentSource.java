package com.maoyuchaxue.catfishclubnewsapp.data.rss;

import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Created by YU_Jason on 2017/9/11.
 */

public class RSSNewsContentSource implements NewsContentSource{
    @Override
    public void close() throws NewsSourceException {

    }

    @Override
    public NewsContent getNewsContent(String id) throws NewsSourceException {
        NewsContent content = new NewsContent();
        try {
            URL url = new URL(id);

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");


            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringWriter pageHtmlBuffer = new StringWriter();
            for(int c = reader.read(); c != -1; c = reader.read())
                pageHtmlBuffer.write(c);
            String pageHtml = pageHtmlBuffer.toString();

            pageHtmlBuffer.close();
            reader.close();
            con.disconnect();



        } catch(Exception e){
            e.printStackTrace();
            throw new NewsSourceException();
        }
        return null;
    }
}
