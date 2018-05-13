package com.bhagyashreebagwe.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by bhagyashree on 2/24/18.
 */

public class StockViewHolder extends RecyclerView.ViewHolder {
    public TextView stockSymbol;
    public TextView price;
    public TextView priceChange;
    public TextView companyName;

    public StockViewHolder(View itemView) {
        super(itemView);
        stockSymbol = (TextView) itemView.findViewById(R.id.stockSymbol);
        price = (TextView) itemView.findViewById(R.id.price);
        priceChange = (TextView) itemView.findViewById(R.id.priceChange);
        companyName = (TextView) itemView.findViewById(R.id.companyName);
    }
}
