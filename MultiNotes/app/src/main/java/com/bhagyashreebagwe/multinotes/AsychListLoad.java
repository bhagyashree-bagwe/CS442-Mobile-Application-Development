package com.bhagyashreebagwe.multinotes;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by bhagyashree on 2/13/18.
 */

public class AsychListLoad extends AsyncTask<MainActivity, Void, List<Note>> {

    private static final String TAG = "AsychListLoad";
    private MainActivity mainActivity;
    public List<Note> noteArrayList = new ArrayList<>();
    public static boolean running = false;

    public List<Note> readJsonStream(InputStream in) throws IOException, ParseException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readNotesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<Note> readNotesArray(JsonReader reader) throws IOException, ParseException {
        List<Note> notes = new ArrayList<Note>();

        reader.beginArray();
        while (reader.hasNext()) {
            notes.add(readNote(reader));
        }
        reader.endArray();
        return notes;
    }

    public Note readNote(JsonReader reader) throws IOException, ParseException {
        Note note = new Note();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("title")) {
                note.setTitle(reader.nextString());
            } else if (name.equals("content")) {
                note.setContent(reader.nextString());
            } else if (name.equals("date")) {
                note.setStrDate(reader.nextString());
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                Date date = sdf.parse(note.getStrDate());
                note.setDate(date);
                Log.d(TAG, "000000000000000  "+note.getDate());
            }  else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return note;
    }

    @Override
    protected List<Note> doInBackground(MainActivity... mainActivities) {
        try {
            mainActivity=mainActivities[0];
            InputStream is = mainActivity.getApplicationContext().openFileInput(mainActivity.getString(R.string.file_name));
            noteArrayList = readJsonStream(is);
        } catch (FileNotFoundException e) {
            Toast.makeText(mainActivity, "No notes present!", Toast.LENGTH_SHORT).show();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (ParseException e)
        {
            e.printStackTrace();
        }

        return noteArrayList;
    }

    @Override
    protected void onPostExecute(List <Note> notes) {
        super.onPostExecute(notes);
        running=false;
        Log.d(TAG, "onPostExecute: ");
        mainActivity.whenAsynchIsDone(noteArrayList);
    }
}
