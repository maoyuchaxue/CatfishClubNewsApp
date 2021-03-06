package com.java.team17.data;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public class NewsMetaInfo implements Serializable{
    private NewsCategoryTag categoryTag = null;
    private int type = 0;
    private String id = null;
    private String srcSite = "";
    private String title = "";
//    private Date time;
    private URL url = null;
    private String author = "", lang = "";

    public void setPictures(URL[] pictures) {
        this.pictures = pictures;
    }

    private URL[] pictures = new URL[0];
    private URL video = null;
    private String intro = "";

    public NewsMetaInfo(){}

    public NewsMetaInfo(String id){
        this.id = id;
    }

    /**
     * @deprecated It is recommended to use the empty constructor and setters instead.
     *
     *
     * */
    public NewsMetaInfo(NewsCategoryTag categoryTag, String id, String srcSite,
                        String title, URL url,
                        String author, String lang, URL[] pictures,
                        URL video, String intro) {
        this.categoryTag = categoryTag;
        this.id = id;
        this.srcSite = srcSite;
        this.title = title;
//        this.time = time;
        this.url = url;
        this.author = author;
        this.lang = lang;
        this.pictures = pictures;
        this.video = video;
        this.intro = intro;
    }

    public NewsCategoryTag getCategoryTag() {
        return categoryTag;
    }


    public String getId() {
        return id;
    }

    public String getSrcSite() {
        return srcSite;
    }

    public String getTitle() {
        return title;
    }

//    public Date getTime() {
//        return time;
//    }

    public URL getUrl() {
        return url;
    }

    public String getAuthor() {
        return author;
    }

    public String getLang() {
        return lang;
    }

    public URL[] getPictures() {
        return pictures;
    }

    public URL getVideo() {
        return video;
    }

    public String getIntro() {
        return intro;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setCategoryTag(NewsCategoryTag categoryTag) {
        this.categoryTag = categoryTag;
    }

    public void setSrcSite(String srcSite) {
        this.srcSite = srcSite;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public void setTime(Date time) {
//        this.time = time;
//    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setVideo(URL video) {
        this.video = video;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

