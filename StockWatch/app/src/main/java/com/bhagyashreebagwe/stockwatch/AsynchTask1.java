package com.bhagyashreebagwe.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bhagyashree on 2/25/18.
 */

public class AsynchTask1 extends AsyncTask<String, Void, String> {

    private static final String TAG = "AsynchTask1";
    private final String stockSearchURL = "http://d.yimg.com/aq/autoc?region=US&lang=en-US";
    private final String stockUpdateURL = "https://api.iextrading.com/1.0/stock";
    private Uri.Builder buildURL = null;
    private StringBuilder sb;
    private MainActivity mainActivity;
    private HashMap <String, String> stockDetails = new HashMap <>();
    private Stock stockData;
    private String indicator;
    private String symbol;
    private boolean noDataFound=false;

    public AsynchTask1(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPostExecute(String s) {
        if (indicator.equals("SEARCH_STOCK") && stockDetails.size() == 1) {
            Map.Entry <String, String> entry = stockDetails.entrySet().iterator().next();
            String key = entry.getKey();
            String value = entry.getValue();
            mainActivity.processNewStock(key + "," + value);
        } else if (indicator.equals("SEARCH_STOCK") && stockDetails.size() > 1) {
            mainActivity.showListDialog(stockDetails);
        }
        else if (indicator.equals("SEARCH_STOCK") && stockDetails.size() == 0) {
            mainActivity.showNoStockFoundAlert(symbol);
        }else if (indicator.equals("UPDATE_STOCK") && stockData!=null) {
            mainActivity.addNewStock(stockData);
        }
        else
        {
            mainActivity.showNoStockFoundAlert(symbol);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        stockDetails.clear();
        indicator = strings[1];
        symbol=strings[0];
        noDataFound=false;

        if (indicator.equals("SEARCH_STOCK")) {
            buildURL = Uri.parse(stockSearchURL).buildUpon();
            buildURL.appendQueryParameter("query", strings[0]);
            connectToAPI();
            parseJSON1(sb.toString());

        } else if (indicator.equals("UPDATE_STOCK")) {
            buildURL = Uri.parse(stockUpdateURL).buildUpon();
            String param[] = strings[0].split(",");
            buildURL.appendPath(param[0]);
            buildURL.appendPath("quote");
            connectToAPI();
            parseJSON2(sb.toString());
            if(stockData!=null) {
                stockData.setCompanyName(param[1]);
            }
        }
        return null;
    }

    private void parseJSON1(String s) {
        try {
            if(!noDataFound) {
                JSONObject jObjMain = new JSONObject(s);
                JSONArray result = jObjMain.getJSONObject("ResultSet").getJSONArray("Result");
                stockDetails.clear();
                for (int i = 0; i < result.length(); i++) {
                    JSONObject stock = (JSONObject) result.get(i);
                    if (stock.getString("type").equals("S") && !stock.getString("symbol").contains(".")) {
                        stockDetails.put(stock.getString("symbol"), stock.getString("name"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseJSON2(String s) {
        try {
            if(!noDataFound) {
                stockData = new Stock();
                JSONObject jObjMain = new JSONObject(s);
                stockData.setStockSymbol(jObjMain.getString("symbol"));
                stockData.setPrice(jObjMain.getDouble("latestPrice"));
                stockData.setPriceChange(jObjMain.getDouble("change"));
                stockData.setChangePercentage(jObjMain.getDouble("changePercent"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectToAPI() {
        String urlToUse = buildURL.build().toString();
        sb = new StringBuilder();
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

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Exception doInBackground: " + e.getMessage());
        }
    }
}
