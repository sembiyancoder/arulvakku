package com.arulvakku.ui.ui.bible;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.arulvakku.R;
import com.arulvakku.ui.database.PostsDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class CreateNotesActivity extends AppCompatActivity {

    private TextInputEditText verseEditText, titleEditText, noteEditText;
    private MaterialButton addNoteButton;
    private String verses = "";
    private int book_id = 0;
    private PostsDatabaseHelper postsDatabaseHelper;
    private View contextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        postsDatabaseHelper = new PostsDatabaseHelper(CreateNotesActivity.this);
        verses = this.getIntent().getStringExtra("verses");
        book_id = this.getIntent().getIntExtra("book_id", 0);
        inflateXML();

    }

    private void inflateXML() {
        contextView = findViewById(R.id.context_view);
        titleEditText = findViewById(R.id.title_edit_text);
        verseEditText = findViewById(R.id.verse_edit_text);
        noteEditText = findViewById(R.id.notes_edit_text);
        addNoteButton = findViewById(R.id.add_note_button);
        verseEditText.setText("" + verses);

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long tempId = postsDatabaseHelper.insertVersesNote(book_id, titleEditText.getText().toString(), noteEditText.getText().toString(), verseEditText.getText().toString());

                if (tempId > 0) {
                    Snackbar.make(contextView, "Added Successfully", Snackbar.LENGTH_SHORT).show();
                }

                clearText();
            }
        });


    }


    private void clearText(){
        titleEditText.getText().clear();
        verseEditText.getText().clear();
        noteEditText.getText().clear();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
