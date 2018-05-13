package com.bhagyashreebagwe.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by bhagyashree on 2/24/18.
 */

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private List<Stock> stockList;
    private MainActivity mainActivity;

    public StockAdapter(List<Stock> stockList,MainActivity mainActivity) {
        this.stockList=stockList;
        this.mainActivity=mainActivity;
    }

    @Override
    public StockViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.stockSymbol.setText(stock.getStockSymbol());
        holder.price.setText(Double.toString(stock.getPrice()));
        holder.companyName.setText(stock.getCompanyName());
        if(stock.getPriceChange()>=0) {
            String priceChange="▲ "+stock.getPriceChange()+" ("+stock.getChangePercentage()+"%)";
            holder.priceChange.setText(priceChange);
            holder.priceChange.setTextColor(Color.GREEN);
            holder.stockSymbol.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.companyName.setTextColor(Color.GREEN);
        }
        else
        {
            String priceChange="▼ "+stock.getPriceChange()+" ("+stock.getChangePercentage()+"%)";
            holder.priceChange.setText(priceChange);
            holder.priceChange.setTextColor(Color.RED);
            holder.stockSymbol.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.companyName.setTextColor(Color.RED);
        }
        }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
