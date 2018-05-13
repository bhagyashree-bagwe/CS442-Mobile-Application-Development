package com.bhagyashreebagwe.multinotes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by bhagyashree on 2/9/18.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private static final String TAG = "NoteAdapter";
    private List<Note> noteList;
    private MainActivity mainActivity;

    public NoteAdapter(List<Note> list, MainActivity activity)
    {
        this.noteList = list;
        mainActivity = activity;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.title.setText(note.getTitle());
        holder.date.setText(note.getStrDate());
        holder.content.setText(note.getContent());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
