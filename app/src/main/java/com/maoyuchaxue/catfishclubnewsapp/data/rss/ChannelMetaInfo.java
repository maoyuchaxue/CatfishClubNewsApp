package com.maoyuchaxue.catfishclubnewsapp.data.rss;

import java.io.Serializable;

/**
 * Created by YU_Jason on 2017/9/11.
 */

public class ChannelMetaInfo implements Serializable{
    private String title, link, description, language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
