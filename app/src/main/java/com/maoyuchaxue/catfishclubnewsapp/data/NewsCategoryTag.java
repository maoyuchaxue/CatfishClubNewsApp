package com.maoyuchaxue.catfishclubnewsapp.data;

/**
 * Created by YU_Jason on 2017/9/5.
 */

public enum NewsCategoryTag {
    SCITECH,
    EDUCATION,
    MILITARY,
    DOMESTIC,
    SOCIETY,
    CULTURE,
    AUTOMOBILE,
    INTERNATIONAL,
    SPORTS,
    ECONOMY,
    HEALTH,
    ENTERTAINMENT;

    public static final String[] TITLES = {
            "科技",
            "教育",
            "军事",
            "国内",
            "社会",
            "文化",
            "汽车",
            "国际",
            "体育",
            "财经",
            "健康",
            "娱乐"
    };

    public static final NewsCategoryTag[] CATEGORIES = {
            SCITECH,
            EDUCATION,
            MILITARY,
            DOMESTIC,
            SOCIETY,
            CULTURE,
            AUTOMOBILE,
            INTERNATIONAL,
            SPORTS,
            ECONOMY,
            HEALTH,
            ENTERTAINMENT
    };

    public static NewsCategoryTag getCategoryByTitle(String title){
        for(int i = 0; i < TITLES.length; i ++)
            if(TITLES[i].equals(title))
                return CATEGORIES[i];
        return null;
    }

    @Override
    public String toString(){
        return TITLES[getIndex() - 1];
    }

    public int getIndex(){
        switch(this){
            case SCITECH:
                return 1;
            case EDUCATION:
                return 2;
            case MILITARY:
                return 3;
            case DOMESTIC:
                return 4;
            case SOCIETY:
                return 5;
            case CULTURE:
                return 6;
            case AUTOMOBILE:
                return 7;
            case INTERNATIONAL:
                return 8;
            case SPORTS:
                return 9;
            case ECONOMY:
                return 10;
            case HEALTH:
                return 11;
            case ENTERTAINMENT:
                return 12;
            default:
                return 0;
        }
    }
}
