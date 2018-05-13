package com.bhagyashreebagwe.newsgateway;

import java.io.Serializable;

/**
 * Created by bhagyashree on 4/21/18.
 */

public class Article implements Serializable {
String articleAuthor;
String articleTitle;
String articleDescription;
String articleUrlToImage;
String articlePublishedAt;
String srticleUrl;

    public String getSrticleUrl() {
        return srticleUrl;
    }

    public void setSrticleUrl(String srticleUrl) {
        this.srticleUrl = srticleUrl;
    }

    public String getArticleAuthor() {
        return articleAuthor;
    }

    public void setArticleAuthor(String articleAuthor) {
        this.articleAuthor = articleAuthor;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleDescription() {
        return articleDescription;
    }

    public void setArticleDescription(String articleDescription) {
        this.articleDescription = articleDescription;
    }

    public String getArticleUrlToImage() {
        return articleUrlToImage;
    }

    public void setArticleUrlToImage(String articleUrlToImage) {
        this.articleUrlToImage = articleUrlToImage;
    }

    public String getArticlePublishedAt() {
        return articlePublishedAt;
    }

    public void setArticlePublishedAt(String articlePublishedAt) {
        this.articlePublishedAt = articlePublishedAt;
    }
}
