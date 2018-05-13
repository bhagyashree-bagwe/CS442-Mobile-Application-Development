package com.bhagyashreebagwe.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhagyashree on 2/25/18.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";
    private List<Stock> stockList = new ArrayList <>();
    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

   public void addStock(List<Stock> stockList) {
       ArrayList<String[]> storedList = loadStocks();
       List<Stock> stockListCopy=  new ArrayList <>(stockList);
       int size = stockList.size();
       int size2=storedList.size();
       for(int k=0;k<size;k++) {
       for(int m=0;m<size2;m++){
           String[] temp = storedList.get(m);
           if(stockList.get(k).getStockSymbol().equals(temp[0])){
               stockListCopy.remove(stockList.get(k));
           }
       }
       }
       Stock s = null;
       for (int i = 0; i < stockListCopy.size(); i++) {
           s = stockListCopy.get(i);
           ContentValues values = new ContentValues();
           values.put(SYMBOL, s.getStockSymbol());
           values.put(COMPANY, s.getCompanyName());
           database.insert(TABLE_NAME, null, values);
       }
    }

    public void deleteStock(String symbol) {
        int cnt = database.delete(
                TABLE_NAME, "StockSymbol = ?", new String[] { symbol });
    }

    public ArrayList<String[]> loadStocks() {
        ArrayList<String[]> stocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME, // The table to query
                new String[]{ SYMBOL, COMPANY }, // The columns to return
                null, // The columns for the WHERE clause, null means “*”
                null, // The values for the WHERE clause, null means “*”
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order
        if (cursor != null) { // Only proceed if cursor is not null
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0); // 1 st returned column
                String company = cursor.getString(1); // 2 nd returned column
                stocks.add(new String[] {symbol, company});
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    public void shutDown() {
        database.close();
    }
}
