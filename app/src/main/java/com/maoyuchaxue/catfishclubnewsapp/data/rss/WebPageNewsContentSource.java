package com.maoyuchaxue.catfishclubnewsapp.data.rss;

import com.maoyuchaxue.catfishclubnewsapp.data.NewsContent;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsContentSource;
import com.maoyuchaxue.catfishclubnewsapp.data.NewsMetaInfo;
import com.maoyuchaxue.catfishclubnewsapp.data.exceptions.NewsSourceException;

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
        try {
            Document doc = Jsoup.connect(url).get();
            Elements params = doc.select("p");

            StringBuilder contentStr = new StringBuilder();
            for(Element element: params){
                contentStr.append("<p>" + element.html() + "</p>");
            }

            content = new NewsContent();

            content.setJournalist("");
            content.setCrawlSource("");
            content.setCategory("");
            content.setContentStr(contentStr.toString());
            content.setKeywords(new String[0]);
        } catch(Exception e){
            e.printStackTrace();
            throw new NewsSourceException();
        }
        return content;
    }
}
