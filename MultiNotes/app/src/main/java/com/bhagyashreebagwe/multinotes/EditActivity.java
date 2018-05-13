package com.bhagyashreebagwe.multinotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";
    private EditText title;
    private EditText content;
    private Note note;
    private String EDIT_FLAG;
    private boolean isSave=false;
    private AlertDialog dialog;
    private int pos;
    Note oldNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        isSave=false;
        title = findViewById(R.id.noteTitle);
        content = findViewById(R.id.noteContent);

        content.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            EDIT_FLAG = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        if(EDIT_FLAG.equals("EDIT_UPDATE"))
        {
            oldNote = (Note) intent.getSerializableExtra("oldNote");
            title.setText(oldNote.getTitle());
            content.setText(oldNote.getContent());
            pos=intent.getIntExtra("position",0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.saveMenu :
                saveNote();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveNote()
    {
        Log.d(TAG, "Saving Note... ");
        if(!title.getText().toString().trim().equals("")) {
            note = new Note();
            note.setContent(content.getText().toString());
            note.setTitle(title.getText().toString());
            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
            String dateString = sdf.format(date);
            note.setStrDate(dateString);
            note.setDate(new Date());
            isSave = true;
            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "The un-titled activity was not saved!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {


        if(EDIT_FLAG.equals("EDIT_UPDATE"))
        {
            if(!title.getText().toString().trim().equals(oldNote.getTitle()) ||
                    !content.getText().toString().trim().equals(oldNote.getContent()))
            {
                forDialog();
            }
            else
            {
                super.onBackPressed();
            }
        }
        else if(!title.getText().toString().trim().equals("") && !isSave) {
            forDialog();
        }
        else if (isSave) {
            Intent data = new Intent();
            data.putExtra("modifiedNote", note);
            data.putExtra("EDIT_FLAG",EDIT_FLAG);
            data.putExtra("position",pos);
            setResult(RESULT_OK, data);
            super.onBackPressed();
        }
        else
        {
            Toast.makeText(this, "The un-titled activity was not saved!", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }

    }

    public void forDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveNote();
                Intent data = new Intent();
                data.putExtra("modifiedNote", note);
                data.putExtra("EDIT_FLAG",EDIT_FLAG);
                data.putExtra("position",pos);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
            }
        });

        builder.setMessage("save note "+title.getText().toString()+" ?");
        builder.setTitle("Your note is not saved!");

        dialog = builder.create();
        dialog.show();
    }
}

