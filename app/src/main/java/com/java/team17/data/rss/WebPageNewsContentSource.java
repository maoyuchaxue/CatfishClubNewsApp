package com.java.team17.data.rss;

import com.java.team17.data.NewsContent;
import com.java.team17.data.NewsContentSource;
import com.java.team17.data.NewsMetaInfo;
import com.java.team17.data.exceptions.NewsSourceException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Created by YU_Jason on 2017/9/11.
 */

public class WebPageNewsContentSource implements NewsContentSource{
    @Override
    public void close() throws NewsSourceException {

    }

    @Override
    public NewsContent getNewsContent(NewsMetaInfo metaInfo) throws NewsSourceException {
        String url = metaInfo.getUrl().toString();

        NewsContent content;
        content = new NewsContent();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements params = doc.select("p");

            StringBuilder contentStr = new StringBuilder();
            for(Element element: params){
                contentStr.append("<p>" + element.html() + "</p>");
            }

            content.setJournalist("");
            content.setCrawlSource("");
            content.setCategory("");
            content.setContentStr(contentStr.toString());
            content.setKeywords(new String[0]);
        } catch(Exception e){
//            e.printStackTrace();
            content.setContentStr(metaInfo.getIntro());
//            throw new NewsSourceException();
        }
        return content;
    }
}
