package com.bhagyashreebagwe.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by bhagyashree on 4/21/18.
 */

public class NewsArticleDownloader extends AsyncTask<String, Integer, String> {
    private static final String TAG = "NewsArticleDownloader";
    private String sourceId;
    private NewsService service;
    private String API_KEY ="70dab759d858467598a6652e30340cc8";
    private String ARTICLE_QUERY_1 = "https://newsapi.org/v1/articles?source=";
    private String ARTICLE_QUERY_2 = "&apiKey="+API_KEY;
    private Uri.Builder buildURL = null;
    private StringBuilder sb1;
    private boolean noDataFound=false;
    boolean isNoDataFound =true;
    private ArrayList<Article> articleArrayList = new ArrayList <Article>();

    public NewsArticleDownloader(NewsService service, String sourceId){
        this.sourceId = sourceId;
        this.service= service;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        service.setArticles(articleArrayList);
    }

    @Override
    protected String doInBackground(String... strings) {
        String query ="";

        query = ARTICLE_QUERY_1+sourceId+ARTICLE_QUERY_2;

        buildURL = Uri.parse(query).buildUpon();
        connectToAPI();
        if(!isNoDataFound) {
            parseJSON1(sb1.toString());
        }
        return null;
    }


    public void connectToAPI() {

        String urlToUse = buildURL.build().toString();
        sb1 = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            {
                noDataFound=true;
            }
            else {
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line=null;
                while ((line = reader.readLine()) != null) {
                    sb1.append(line).append('\n');
                }
                isNoDataFound=false;

            }
        }
        catch(FileNotFoundException fe){
            Log.d(TAG, "FileNotFoundException ");
        }
        catch (Exception e) {
            //e.printStackTrace();
            Log.d(TAG, "Exception doInBackground: " + e.getMessage());
        }
    }


    private void parseJSON1(String s) {
        try{
            if(!noDataFound){
                JSONObject jObjMain = new JSONObject(s);
                JSONArray articles = jObjMain.getJSONArray("articles");
                for(int i=0;i<articles.length();i++){
                    JSONObject art = (JSONObject) articles.get(i);
                    Article artObj = new Article();
                    artObj.setArticleAuthor(art.getString("author"));
                    artObj.setArticleDescription(art.getString("description"));
                    artObj.setArticlePublishedAt(art.getString("publishedAt"));
                    artObj.setArticleTitle(art.getString("title"));
                    artObj.setArticleUrlToImage(art.getString("urlToImage"));
                    artObj.setSrticleUrl(art.getString("url"));
                    articleArrayList.add(artObj);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
