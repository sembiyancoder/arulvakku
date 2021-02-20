package com.arulvakku.app.ui.bible;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.ui.bible.adapter.NotesListAdapter;
import com.arulvakku.app.database.PostsDatabaseHelper;
import com.arulvakku.app.model.BookModel;
import com.arulvakku.app.model.Notes;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class NotesListActivity extends AppCompatActivity implements NotesListAdapter.onItemSelectedListener {

    PostsDatabaseHelper postsDatabaseHelper;
    RecyclerView recyclerView;
    private NotesListAdapter adapter;
    private ConstraintLayout contextView;
    private TextView txtNoItemFound;
    Intent intent;
    boolean isShowAll;
    String book_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("குறிப்புக்கள்");

        recyclerView = findViewById(R.id.recycler_view);
        contextView = findViewById(R.id.context_view);
        txtNoItemFound = findViewById(R.id.txt_bookmark);

        postsDatabaseHelper = new PostsDatabaseHelper(NotesListActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        intent = this.getIntent();
        isShowAll = intent.getBooleanExtra("show_all", false);
        book_id = intent.getStringExtra("book_id");
        setAdapter();
    }

    private void setAdapter(){
        List<Notes> notesList;
        if (isShowAll) {
            notesList = postsDatabaseHelper.getAllNotes();
        } else {
            notesList = postsDatabaseHelper.getChapterNotes(Integer.parseInt(book_id));
        }
        if (notesList != null && notesList.size() > 0) {
            adapter = new NotesListAdapter(this, notesList, this);
            recyclerView.setAdapter(adapter);
            txtNoItemFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            txtNoItemFound.setText("குறிப்புக்கள் இல்லை");
            recyclerView.setVisibility(View.GONE);
            txtNoItemFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(BookModel bookModel) {

    }

    @Override
    public void onDeleteItem(Notes notes) {
        postsDatabaseHelper.deleteNotes(notes);
        setAdapter();
        Snackbar.make(contextView, "Deleted Successfully", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onShareBookmark(Notes bookModel) {
        String title = bookModel.getTitle();
        String verse = bookModel.getVerse();
        String notes = bookModel.getNotes();
        String strShare = title + "\n\n" + verse + "\n\n" + notes + "\n\n" + "https://play.google.com/store/apps/details?id=com.arulvakku&hl=en";
        shareBookmarkedVerse(strShare);
    }

    private void shareBookmarkedVerse(String verse) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, verse);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
    }

}
