package io.alvalabs.mobile_boilerplate_android_java;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.UUID;
import io.alvalabs.mobile_boilerplate_android_java.adapter.NoteAdapter;
import io.alvalabs.mobile_boilerplate_android_java.model.Note;
import io.alvalabs.mobile_boilerplate_android_java.util.NoteStorage;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {
    private NoteAdapter adapter;
    private NoteStorage noteStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteStorage = new NoteStorage(this);
        setupRecyclerView();
        setupAddNoteFab();
        loadNotes();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupAddNoteFab() {
        FloatingActionButton fab = findViewById(R.id.fabAddNote);
        fab.setOnClickListener(v -> showAddNoteDialog());
    }

    private void loadNotes() {
        adapter.setNotes(noteStorage.getNotes());
    }

    private void showAddNoteDialog() {
        showNoteDialog("Add Note", "", note -> {
            note.setId(UUID.randomUUID().toString());
            note.setTimestamp(System.currentTimeMillis());
            noteStorage.addNote(note);
            loadNotes();
        });
    }

    private void showEditNoteDialog(Note note) {
        showNoteDialog("Edit Note", note.getContent(), editedNote -> {
            editedNote.setId(note.getId());
            editedNote.setTimestamp(System.currentTimeMillis());
            noteStorage.updateNote(editedNote);
            loadNotes();
        });
    }

    private void showNoteDialog(String title, String content, NoteCallback callback) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setText(content);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String noteContent = input.getText().toString().trim();
                    if (!noteContent.isEmpty()) {
                        Note note = new Note(null, noteContent, 0);
                        callback.onNoteAction(note);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onNoteClick(Note note) {
        showEditNoteDialog(note);
    }

    @Override
    public void onDeleteClick(Note note) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    noteStorage.deleteNote(note);
                    loadNotes();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private interface NoteCallback {
        void onNoteAction(Note note);
    }
}