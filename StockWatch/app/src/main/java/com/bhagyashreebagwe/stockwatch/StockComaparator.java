package com.bhagyashreebagwe.stockwatch;

import java.util.Comparator;

/**
 * Created by bhagyashree on 3/1/18.
 */

public class StockComaparator implements Comparator<Stock> {
    @Override
    public int compare(Stock stock, Stock t1) {
        return stock.getStockSymbol().compareTo(t1.getStockSymbol());
    }
}
