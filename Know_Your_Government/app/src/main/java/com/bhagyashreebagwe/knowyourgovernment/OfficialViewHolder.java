package com.bhagyashreebagwe.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by bhagyashree on 3/31/18.
 */

public class OfficialViewHolder extends RecyclerView.ViewHolder{
    public TextView office;
    public TextView nameAndParty;

    public OfficialViewHolder(View view) {
        super(view);
        office = (TextView) view.findViewById(R.id.location);
        nameAndParty = (TextView) view.findViewById(R.id.nameAndParty);
    }
}
