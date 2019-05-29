package com.example.newsapp;

import java.util.Date;

public class NewsItem {
    private String mTitle;
    private String mContributor;
    private String mSectionName;
    private Date mDate;
    private String mUrl;

    public NewsItem(String mTitle, String mContributor, String mSectionName, Date mDate, String mUrl) {
        this.mTitle = mTitle;
        this.mContributor = mContributor;
        this.mSectionName = mSectionName;
        this.mDate = mDate;
        this.mUrl = mUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmContributor() {
        return mContributor;
    }

    public String getmSectionName() {
        return mSectionName;
    }

    public Date getmDate() {
        return mDate;
    }

    public String getmUrl() {
        return mUrl;
    }
}
