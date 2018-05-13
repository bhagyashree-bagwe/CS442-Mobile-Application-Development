package com.bhagyashreebagwe.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{
    private static final String TAG = "MainActivity";
    private List<Stock> stockList = new ArrayList <>();
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private DatabaseHandler databaseHandler;
    private final static  String STOCK_SEARCH="SEARCH_STOCK";
    private final static  String STOCK_UPDATE="UPDATE_STOCK";
    private final static String marketWatchURL = "http://www.marketwatch.com/investing/stock/some_stock";
    private ConnectivityManager connectivityManager;
    private boolean connected = false;
    private SwipeRefreshLayout swiper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        stockAdapter = new StockAdapter(stockList, this);

        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHandler = new DatabaseHandler(this);

        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
    }

    @Override
    protected void onResume() {
        boolean flag=true;
        Log.d(TAG, "onResume: ");
        ArrayList<String[]> list = databaseHandler.loadStocks();
        stockList.clear();
        for(int i=0;i<list.size();i++){
            String[] temp= list.get(i);
            doAsyncLoad1(temp[0]+","+temp[1],STOCK_UPDATE);
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        databaseHandler.addStock(stockList);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock s = stockList.get(pos);
        String url = marketWatchURL+"/"+s.getStockSymbol();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        final Stock stock = stockList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stockList.remove(stock);
                databaseHandler.deleteStock(stock.getStockSymbol());
                stockAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setIcon(R.drawable.ic_delete_black_24dp);
        builder.setMessage("Delete stock symbol "+stock.getStockSymbol()+" ?");
        builder.setTitle("Delete Stock");
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.addStock :
                if(isOnline())
                {
                    addStock();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Stocks cannot be added without a network connection");
                    builder.setTitle("No Network Connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addStock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);;
        et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        et.setGravity(Gravity.CENTER_HORIZONTAL);

        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(checkDuplicate(et)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Stocks symbol "+et.getText().toString()+" is already displayed");
                    builder.setTitle("Duplicate Stock");
                    builder.setIcon(R.drawable.ic_warning_black_24dp);
                    AlertDialog dialog1 = builder.create();
                    dialog1.show();
                } else
                {
                    doAsyncLoad1(et.getText().toString(), STOCK_SEARCH);
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Please enter a stock symbol:");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean checkDuplicate(EditText et){
        Stock temp=null;
        for(int i=0;i<stockList.size();i++)
        {
            temp=stockList.get(i);
            if(temp.getStockSymbol().equals(et.getText().toString()))
                return true;
        }
        return  false;
    }

    public void processNewStock(String symbolAndCompnayName){
        AsynchTask1 ast1= new AsynchTask1(this);
        if(isOnline()) {
            ast1.execute(symbolAndCompnayName, STOCK_UPDATE);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Stocks cannot be added without a network connection");
            builder.setTitle("No Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void doAsyncLoad1(String symbol, String indicator) {
        AsynchTask1 ast1= new AsynchTask1(this);
        if(isOnline()) {
            ast1.execute(symbol, indicator);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Stocks cannot be added without a network connection");
            builder.setTitle("No Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void addNewStock(Stock stock) {
        if(stock!=null) {
            stockList.add(stock);
            Collections.sort(stockList, new StockComaparator());
            stockAdapter.notifyDataSetChanged();
        }
    }

    public void showListDialog(HashMap<String,String> list) {
        final CharSequence[] sArray = new CharSequence[list.size()];
        Iterator it = list.entrySet().iterator();
        int i=0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            sArray[i]=pair.getKey()+","+pair.getValue();
            i++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");
        builder.setItems(sArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                doAsyncLoad1(sArray[which].toString(),MainActivity.STOCK_UPDATE);
            }
        });
        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }

    public void showNoStockFoundAlert(String symbol){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Data for stock symbol");
        builder.setTitle("Symbol Not Found: "+symbol);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void doRefresh(){
        if(isOnline()) {
            for (int i = 0; i < stockList.size(); i++) {
                String tempVar = stockList.get(i).getStockSymbol() + "," + stockList.get(i).getCompanyName();
                stockList.remove(stockList.get(i));
                doAsyncLoad1(tempVar, MainActivity.STOCK_UPDATE);
            }
            swiper.setRefreshing(false);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Stocks cannot be refreshed without a network connection");
            builder.setTitle("No Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            swiper.setRefreshing(false);
        }
    }

}
