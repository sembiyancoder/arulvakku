package com.arulvakku.app.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.facebook.shimmer.ShimmerFrameLayout;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ShimmerFrameLayout mShimmerFrameLayout;
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("அறிவிப்புகள்");

        mRecyclerView = findViewById(R.id.recyclerview_notifications);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mShimmerFrameLayout = findViewById(R.id.shimmer_frameLayout);
        mLinearLayout = findViewById(R.id.layout_no_notification);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
