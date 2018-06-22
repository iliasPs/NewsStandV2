package com.example.android.newsstandv2;

public class News {

    private String mNewsHeader;

    private String mNewsAuthor;
    private String mNewsImage;
    private String mNewsCategory;
    private String mNewsDate;
    private String mNewsUrl;

    public News(String newsImage, String newsHeader, String newsAuthor, String newsCategory, String newsDate, String newsUrl) {

        mNewsImage = newsImage;
        mNewsHeader = newsHeader;
        mNewsAuthor = newsAuthor;
        mNewsCategory = newsCategory;
        mNewsDate = newsDate;
        mNewsUrl = newsUrl;
    }

    public String getNewsHeader() {
        return mNewsHeader;
    }

    public String getNewsAuthor() {
        return mNewsAuthor;
    }

    public String getNewsImage() {
        return mNewsImage;
    }

    public String getNewsCategory() {
        return mNewsCategory;
    }

    public String getNewsDate() {
        return mNewsDate;
    }

    public String getNewsUrl() {
        return mNewsUrl;
    }
}
