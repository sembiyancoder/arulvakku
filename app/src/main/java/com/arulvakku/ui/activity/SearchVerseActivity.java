package com.arulvakku.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.ui.adapter.SearchResultRecyclerAdapter;
import com.arulvakku.ui.database.DBHelper;
import com.arulvakku.ui.model.SearchWord;

import java.util.ArrayList;
import java.util.List;

public class SearchVerseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private SearchResultRecyclerAdapter adapter;
    private String searchWord;
    private List<SearchWord> searchWordList;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_verse);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbHelper = DBHelper.getInstance(this);
        recyclerView = findViewById(R.id.recyclerview);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchWord = query.trim();
                new SearchWordAsyn().execute();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }



    public class SearchWordAsyn extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchWordList = new ArrayList<>();
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            searchWordList = dbHelper.searchWord(searchWord);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (searchWordList.size() > 0) {
                adapter = new SearchResultRecyclerAdapter(searchWordList, searchWord);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchVerseActivity.this));
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();

                adapter.SetOnItemClickListener(new SearchResultRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String book = searchWordList.get(position).getId().substring(0, 2);
                        String chapter = searchWordList.get(position).getId().substring(2, 5);
                        String verse = searchWordList.get(position).getId().substring(5, 8);
                    }
                });
            } else {
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

}
