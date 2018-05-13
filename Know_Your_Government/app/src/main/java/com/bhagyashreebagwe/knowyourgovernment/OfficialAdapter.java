package com.bhagyashreebagwe.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by bhagyashree on 3/31/18.
 */

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder> {
    private static final String TAG = "OfficialAdapter";
    private List<Official> officialList;
    private MainActivity mainAct;

    public OfficialAdapter(List<Official> officialList, MainActivity ma){
        this.officialList = officialList;
        mainAct = ma;
    }

    @Override
    public OfficialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_list, parent, false);

        itemView.setOnClickListener(mainAct);

        return new OfficialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OfficialViewHolder holder, int position) {
        Official official = officialList.get(position);
        holder.office.setText(official.getOffice());
        if(!official.getParty().equalsIgnoreCase("Unknown") && !official.getParty().equalsIgnoreCase("No data provided")) {
            holder.nameAndParty.setText(official.getName() + "(" + official.getParty() + ")");
        }
        else
        {
            holder.nameAndParty.setText(official.getName());
        }
    }

    @Override
    public int getItemCount() {
        if(officialList!=null)
        return officialList.size();
        else
            return 0;
    }
}
