package com.arulvakku.app.ui.bible;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.arulvakku.R;
import com.arulvakku.app.ui.bible.fragments.NewTestamentFragment;
import com.arulvakku.app.ui.bible.fragments.OldTestamentFragment;
import com.arulvakku.app.fragments.TabAdapter;
import com.google.android.material.tabs.TabLayout;

public class BooksActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new OldTestamentFragment(), "பழைய ஏற்பாடு");
        adapter.addFragment(new NewTestamentFragment(), "புதிய ஏற்பாடு");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_books, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.bookmark:
                intent = new Intent(this, BookmarkListActivity.class);
                intent.putExtra("show_all", true);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                return true;
            case R.id.notes:
                intent = new Intent(this, NotesListActivity.class);
                intent.putExtra("show_all", true);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                return true;
            case R.id.search:
                intent = new Intent(BooksActivity.this, SearchVerseActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);

            default:
                return super.onOptionsItemSelected(item);



        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}

