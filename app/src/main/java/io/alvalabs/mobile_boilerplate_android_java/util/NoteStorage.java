package io.alvalabs.mobile_boilerplate_android_java.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import io.alvalabs.mobile_boilerplate_android_java.model.Note;

public class NoteStorage {
    private static final String PREF_NAME = "NotePrefs";
    private static final String KEY_NOTES = "notes";
    private final SharedPreferences preferences;
    private final Gson gson;

    public NoteStorage(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveNotes(List<Note> notes) {
        String json = gson.toJson(notes);
        preferences.edit().putString(KEY_NOTES, json).apply();
    }

    public List<Note> getNotes() {
        String json = preferences.getString(KEY_NOTES, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Note>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void addNote(Note note) {
        List<Note> notes = getNotes();
        notes.add(note);
        saveNotes(notes);
    }

    public void updateNote(Note note) {
        List<Note> notes = getNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(note.getId())) {
                notes.set(i, note);
                break;
            }
        }
        saveNotes(notes);
    }

    public void deleteNote(Note note) {
        List<Note> notes = getNotes();
        notes.removeIf(n -> n.getId().equals(note.getId()));
        saveNotes(notes);
    }
} 