package com.arulvakku.ui.ui.bible;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.ui.adapter.ChapterAdapter;
import com.arulvakku.ui.adapter.VerseAdapter;
import com.arulvakku.ui.app.MyApplication;
import com.arulvakku.ui.database.DBHelper;
import com.arulvakku.ui.database.PostsDatabaseHelper;
import com.arulvakku.ui.model.Bookmark;
import com.arulvakku.ui.model.Verses;
import com.arulvakku.ui.utils.Constants;
import com.arulvakku.ui.utils.UtilSingleton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class VerseActivity extends AppCompatActivity implements VerseAdapter.VerseListener, View.OnClickListener {

    String strNote = null;
    private Context context;
    private PostsDatabaseHelper databaseHelper;
    private String book_name;
    private int book_id, book_chapter_count;
    private RecyclerView verseRecyclerView;
    private DBHelper dbHelper;
    private VerseAdapter verseAdapter;
    private List<Verses> versesList = new ArrayList<>();
    private MaterialCardView cardViewChapters;
    private LinearLayoutManager mainLayoutManager;
    MenuItem itemCopy, itemShare, itemNotes, itemBookmark;
    RelativeLayout layout;
    private List<Bookmark> bookModelList;

    private String searchWord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verse);
        inflateXML();
        context = this;

        bookModelList = new ArrayList<>();
        versesList = new ArrayList<>();

        databaseHelper = PostsDatabaseHelper.getInstance(context);
        dbHelper = DBHelper.getInstance(context);
        book_id = Integer.parseInt(this.getIntent().getStringExtra(Constants.BOOK_ID));
        book_chapter_count = Integer.parseInt(this.getIntent().getStringExtra(Constants.BOOK_CHAPTER_COUNT));
        book_name = this.getIntent().getStringExtra(Constants.BOOK_NAME);
        setToolbarTitle(book_name +""+1);

        //clearing the singleton data
        UtilSingleton.getInstance().clearData();
    }

    private void setToolbarTitle(String title) {
        if (title != null && !book_name.isEmpty()) {
            ((AppCompatActivity) this).getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVerseAdapter();
    }

    private void setVerseAdapter() {
        versesList.clear();
        bookModelList.clear();
        versesList = dbHelper.getVerse(book_id);
        bookModelList = databaseHelper.getChapterBookmarks(book_id);
        verseAdapter = new VerseAdapter(this, versesList, bookModelList, this);
        verseRecyclerView.setAdapter(verseAdapter);
        verseAdapter.notifyDataSetChanged();
    }


    private void inflateXML() {
        verseRecyclerView = findViewById(R.id.recycler_view);
        cardViewChapters = findViewById(R.id.card_view_chapters);
        mainLayoutManager = new LinearLayoutManager(this);
        verseRecyclerView.setLayoutManager(mainLayoutManager);
        layout = findViewById(R.id.context_view);
        verseRecyclerView.setHasFixedSize(true);
        cardViewChapters.setOnClickListener(this);
    }

    @Override
    public void updateToolbarTitle(String verse_no) {
        setToolbarTitle(book_name +" " +verse_no);
    }

    @Override
    public void onSelectedVerse(int pos, Verses verses) {
        Verses verse = versesList.get(pos);
        UtilSingleton.getInstance().addSelectedVerse(verse.getFull_id(), verses);
        int count = UtilSingleton.getInstance().getSelectedVerseList().size();
        verseAdapter.notifyDataSetChanged();

        if (count > 0) {
            setToolbarTitle("" + count);
            showMenuItem(true);
        } else {
            setToolbarTitle(book_name);
            showMenuItem(false);
        }

    }

    @Override
    public void onNoResultFound(boolean notFound) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_verses, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        itemCopy = menu.findItem(R.id.copy);
        itemShare = menu.findItem(R.id.share);
        itemNotes = menu.findItem(R.id.notes);
        itemBookmark = menu.findItem(R.id.bookmark);
        showMenuItem(false);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (verseAdapter != null) {
                    verseAdapter.getFilter().filter(newText);
                }
                return true;
            }

        });

        return true;
    }

    private void showMenuItem(boolean isShow) {
        if (isShow) {
            itemCopy.setVisible(true);
            itemShare.setVisible(true);
            itemNotes.setVisible(true);
            itemBookmark.setVisible(true);
        } else {
            itemCopy.setVisible(false);
            itemShare.setVisible(false);
            itemNotes.setVisible(false);
            itemBookmark.setVisible(false);

            setToolbarTitle(book_name);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String verse = getVerse();

        showMenuItem(false);

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.copy:
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", verse + "\n\n" + "https://play.google.com/store/apps/details?id=com.arulvakku&hl=en");
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Snackbar.make(layout, "Text Copied to clipboard", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.share:
                if (verse != null && !verse.isEmpty()) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, verse + "\n\n" + "https://play.google.com/store/apps/details?id=com.arulvakku&hl=en");
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                }
                return true;
            case R.id.notes:
                Intent mainIntent = new Intent(this, CreateNotesActivity.class);
                mainIntent.putExtra("verses", verse);
                mainIntent.putExtra("book_id", book_id);
                startActivity(mainIntent);
                return true;
            case R.id.bookmark:
                List<String> stringList = UtilSingleton.getInstance().getBookmarkedVerseList();
                for (int index = 0; index < stringList.size(); index++) {
                    String verse_no = stringList.get(index);
                    databaseHelper.addBookmark(book_id, verse_no);
                }


                Snackbar.make(layout, "Bookmark Added Successfully", Snackbar.LENGTH_SHORT).show();
                setVerseAdapter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private String getVerse() {
        String strShare = book_name + "\n\n";
        List<Verses> versesHashMap = UtilSingleton.getInstance().getSortedVerseList();
        if (versesHashMap != null && versesHashMap.size() > 0) {

            for (int index = 0; index < versesHashMap.size(); index++) {
                Verses verse = versesHashMap.get(index);
                if (index == 0) {
                    strShare = strShare + verse.getChapter_id() + "-" + verse.getVerse_id() + "." + verse.getVerse() + "\n\n";
                } else if (index == versesHashMap.size() - 1) {
                    strShare = strShare + verse.getChapter_id() + "-" + verse.getVerse_id() + "." + verse.getVerse();
                } else {
                    strShare = strShare + verse.getChapter_id() + "-" + verse.getVerse_id() + "." + verse.getVerse() + "\n\n";
                }
            }
        }

        UtilSingleton.getInstance().clearData();
        verseAdapter.notifyDataSetChanged();
        return strShare;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_view_chapters:
                CustomChapterDetails customChapterDetails = new CustomChapterDetails();
                customChapterDetails.showDialog(this);
                break;
            default:
                break;
        }
    }


    class CustomChapterDetails implements ChapterAdapter.onItemSelectedListener {
        private Dialog dialog;
        private RecyclerView recyclerView;
        private ChapterAdapter chapterAdapter;

        public void showDialog(Context context) {
            dialog = new Dialog(VerseActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_chater_layout);
            recyclerView = dialog.findViewById(R.id.recycler_view);
            chapterAdapter = new ChapterAdapter(VerseActivity.this, book_chapter_count, this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(VerseActivity.this, 5));
            recyclerView.setAdapter(chapterAdapter);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }

        public void hideDialog() {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }

        @Override
        public void onItemSelected(int position) {
            int count = 0;
            if (position > 0) {
                count = dbHelper.getCount("" + book_id, "" + position);
                MyApplication.mCurrentChapterNo = position;
            } else {
                count = 0;
                MyApplication.mCurrentChapterNo = 0;
            }
            mainLayoutManager.scrollToPositionWithOffset(count, 20);
            hideDialog();
        }
    }


}
