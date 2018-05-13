package com.bhagyashreebagwe.multinotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private static final int EDIT_REQUEST_CODE = 111;
    private static final String EDIT_ADD_NEW = "EDIT_ADD_NEW";
    private static final String EDIT_UPDATE = "EDIT_UPDATE";
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    public List<Note> noteArrayList = new ArrayList <>();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AsychListLoad().execute(this);
    }

    public void whenAsynchIsDone(List<Note> list)
    {
        noteArrayList=list;
        Collections.sort(noteArrayList);
        Collections.reverse(noteArrayList);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        noteAdapter = new NoteAdapter(noteArrayList, this);

        recyclerView.setAdapter(noteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Note oldNote = noteArrayList.get(pos);
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, EDIT_UPDATE);
        intent.putExtra("oldNote", oldNote);
        intent.putExtra("position",pos);
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        final Note oldNote = noteArrayList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                noteArrayList.remove(oldNote);
                noteAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setMessage("Delete note "+oldNote.getTitle()+" ?");

        dialog = builder.create();
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
            case R.id.menu_add :
                Log.d(TAG, "Add menu selected ");
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, EDIT_ADD_NEW);
                startActivityForResult(intent, EDIT_REQUEST_CODE);
                break;
            case R.id.menu_about :
                Log.d(TAG, "About menu selected ");
                Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            writeJsonStream(fos, noteArrayList);
        }catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                    String flag="";
                    int pos;
                    flag = data.getStringExtra("EDIT_FLAG");
                    if(flag.equals("EDIT_UPDATE")) {
                        Note note1 =  (Note) data.getSerializableExtra("modifiedNote");
                        pos=data.getIntExtra("position",0);
                        noteArrayList.set(pos,note1);
                    }
                    else if(flag.equals("EDIT_ADD_NEW")){
                        Note note1 = (Note) data.getSerializableExtra("modifiedNote");
                        noteArrayList.add(note1);
                    }
                    Collections.sort(noteArrayList);
                    Collections.reverse(noteArrayList);
                    noteAdapter.notifyDataSetChanged();
            }
            else {
                Log.d(TAG, "onActivityResult: result Code: " + resultCode);
            }

        } else {
            Log.d(TAG, "onActivityResult: Request Code " + requestCode);
        }
    }

    public void writeJsonStream(FileOutputStream out, List<Note> notes) throws IOException {
        Log.d(TAG, "writeJsonStream: ");
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writeNotesArray(writer, notes);
        writer.close();
    }

    public void writeNotesArray(JsonWriter writer, List<Note> notes) throws IOException {
        writer.beginArray();
        for (Note note : notes) {
            writeNote(writer, note);
        }
        writer.endArray();
    }

    public void writeNote(JsonWriter writer, Note note) throws IOException {
        writer.beginObject();
        writer.name("title").value(note.getTitle());
        writer.name("content").value(note.getContent());
        writer.name("date").value(note.getStrDate());
        writer.endObject();
    }



}

