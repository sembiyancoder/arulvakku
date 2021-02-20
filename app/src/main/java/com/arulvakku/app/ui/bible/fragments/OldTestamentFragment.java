package com.arulvakku.app.ui.bible.fragments;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.ui.bible.BookmarkListActivity;
import com.arulvakku.app.ui.bible.NotesListActivity;
import com.arulvakku.app.ui.bible.VerseActivity;
import com.arulvakku.app.adapter.BooksListAdapter;
import com.arulvakku.app.MyApplication;
import com.arulvakku.app.database.DBHelper;
import com.arulvakku.app.model.BookModel;
import com.arulvakku.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class OldTestamentFragment extends Fragment implements BooksListAdapter.onItemSelectedListener {


    private RecyclerView recyclerView;
    private List<BookModel> bookModelList = new ArrayList<>();
    private BooksListAdapter adapter;
    private TextView txtNoBooksFound;
    private DBHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chapter_fragment, container, false);
        dbHelper = DBHelper.getInstance(getActivity());

        bookModelList = dbHelper.getOldTestament();

        recyclerView = view.findViewById(R.id.chapter_list_view);
        txtNoBooksFound = view.findViewById(R.id.txt_no_books_found);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new BooksListAdapter(getContext(), bookModelList, this);
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_book_search, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }

        });
    }


    @Override
    public void onNoResultFound(boolean notFound) {
        if (notFound) {
            recyclerView.setVisibility(View.GONE);
            txtNoBooksFound.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            txtNoBooksFound.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(BookModel bookModel) {
        //setting first chapter verse number 1 as default
        MyApplication.mCurrentChapterNo = 1;
        Intent verseIntent = new Intent(getActivity(), VerseActivity.class);
        verseIntent.putExtra(Constants.BOOK_NAME, bookModel.getChapter_name());
        verseIntent.putExtra(Constants.BOOK_CHAPTER_COUNT, bookModel.getChapter_count());
        verseIntent.putExtra(Constants.BOOK_ID, bookModel.getChapter_id());
        verseIntent.putExtra(Constants.SHARE_ID, bookModel.getChapter_share_no());
        startActivity(verseIntent);
        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void onNotesSelected(BookModel bookModel) {
        Intent mainIntent = new Intent(getActivity(), NotesListActivity.class);
        mainIntent.putExtra("book_id", bookModel.getChapter_id());
        startActivity(mainIntent);
    }

    @Override
    public void onBookmarkSelected(BookModel bookModel) {
        Intent mainIntent = new Intent(getActivity(), BookmarkListActivity.class);
        mainIntent.putExtra("book_id", bookModel.getChapter_id());
        startActivity(mainIntent);
    }
}