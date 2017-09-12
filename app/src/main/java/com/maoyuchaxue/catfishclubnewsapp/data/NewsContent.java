package com.maoyuchaxue.catfishclubnewsapp.data;

import java.io.Serializable;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public class NewsContent implements Serializable{
    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    public String getJournalist() {
        return journalist;
    }

    public void setJournalist(String journalist) {
        this.journalist = journalist;
    }

    public String getCrawlSource() {
        return crawlSource;
    }

    public void setCrawlSource(String crawlSource) {
        this.crawlSource = crawlSource;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

//    public Date getCrawTime() {
//        return crawTime;
//    }

//    public void setCrawTime(Date crawTime) {
//        this.crawTime = crawTime;
//    }

    private String contentStr = "", journalist = "", crawlSource = "", category = "";

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    private String[] keywords = new String[0];
//    private Date crawTime;

    // empty constructor


}
