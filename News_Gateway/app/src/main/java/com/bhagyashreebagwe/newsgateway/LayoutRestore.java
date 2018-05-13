package com.bhagyashreebagwe.newsgateway;

import android.view.Menu;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bhagyashree on 4/22/18.
 */

public class LayoutRestore implements Serializable {
    private ArrayList<Source> sourceList = new ArrayList<Source>();
    private ArrayList<Article> articleList = new ArrayList <Article>();
    private ArrayList<String> categories = new ArrayList <String>();
    private int currentSource;
    private int currentArticle;

    public ArrayList <Source> getSourceList() {
        return sourceList;
    }

    public void setSourceList(ArrayList <Source> sourceList) {
        this.sourceList = sourceList;
    }

    public ArrayList <Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(ArrayList <Article> articleList) {
        this.articleList = articleList;
    }

    public ArrayList <String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList <String> categories) {
        this.categories = categories;
    }

    public int getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(int currentSource) {
        this.currentSource = currentSource;
    }

    public int getCurrentArticle() {
        return currentArticle;
    }

    public void setCurrentArticle(int currentArticle) {
        this.currentArticle = currentArticle;
    }
}
