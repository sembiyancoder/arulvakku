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
import com.arulvakku.app.adapter.BookmarkListAdapter;
import com.arulvakku.app.database.DBHelper;
import com.arulvakku.app.database.PostsDatabaseHelper;
import com.arulvakku.app.model.BookModel;
import com.arulvakku.app.model.Bookmark;
import com.arulvakku.app.utils.UtilSingleton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class BookmarkListActivity extends AppCompatActivity implements BookmarkListAdapter.onItemSelectedListener {

    PostsDatabaseHelper postsDatabaseHelper;
    RecyclerView recyclerView;
    DBHelper dbHelper;
    private BookmarkListAdapter adapter;
    private ConstraintLayout contextView;
    private TextView txtNoItemFound;
    Intent intent;
    boolean isShowAll;
    String book_id = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("புத்தககுறிகள்");

        txtNoItemFound = findViewById(R.id.txt_bookmark);
        recyclerView = findViewById(R.id.recycler_view);
        contextView = findViewById(R.id.context_view);
        postsDatabaseHelper = new PostsDatabaseHelper(BookmarkListActivity.this);
        dbHelper = new DBHelper(BookmarkListActivity.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        intent = this.getIntent();
        isShowAll = intent.getBooleanExtra("show_all", false);
        book_id = intent.getStringExtra("book_id");
        setAdapter();
    }

    private void setAdapter() {
        List<Bookmark> bookmarkList;
        if (isShowAll) {
            bookmarkList = postsDatabaseHelper.getAllBookmarks();
        } else {
            bookmarkList = postsDatabaseHelper.getChapterBookmarks(Integer.parseInt(book_id));
        }

        if (bookmarkList != null && bookmarkList.size() > 0) {
            adapter = new BookmarkListAdapter(this, bookmarkList, this);
            recyclerView.setAdapter(adapter);
            txtNoItemFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            txtNoItemFound.setText("புத்தககுறிகள் இல்லை");
            txtNoItemFound.setVisibility(View.VISIBLE);
        }

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

    @Override
    public void onItemSelected(BookModel bookModel) {

    }

    @Override
    public void onDeleteItem(Bookmark bookmark) {
        postsDatabaseHelper.deleteBookmark(bookmark);
        setAdapter();
        Snackbar.make(contextView, "Deleted Successfully", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onShareBookmark(Bookmark bookModel) {
        String title = UtilSingleton.getInstance().getBookmarkAndVerse(bookModel.getVerse());
        String verse = "" + dbHelper.getVerseDay(bookModel.getVerse());
        String strShare = title + "\n\n" + verse + "\n\n" + "https://play.google.com/store/apps/details?id=com.arulvakku&hl=en";
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
