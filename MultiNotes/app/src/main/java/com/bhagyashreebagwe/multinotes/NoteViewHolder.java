package com.bhagyashreebagwe.multinotes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by bhagyashree on 2/9/18.
 */

public class NoteViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView date;
    public TextView content;

    public NoteViewHolder(View view)
    {
        super(view);
        title = (TextView) view.findViewById(R.id.note_title);
        date = (TextView) view.findViewById(R.id.note_date);
        content = (TextView) view.findViewById(R.id.note_content);
    }
}
