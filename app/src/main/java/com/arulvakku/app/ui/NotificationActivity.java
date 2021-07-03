package com.arulvakku.app.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.database.PostsDatabaseHelper;
import com.arulvakku.app.model.Notification;
import com.arulvakku.app.ui.notification.LocalNotificationListAdapter;
import com.arulvakku.app.utils.CustomProgress;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements LocalNotificationListAdapter.onItemSelectedListener {

    private RecyclerView mRecyclerView;
    private LinearLayout mNoNotificationLayout;
    private CustomProgress mCustomProgress;
    private LocalNotificationListAdapter mAdapter;
    private PostsDatabaseHelper mPostsDatabaseHelper;
    private List<Notification> mNotificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("அறிவிப்புகள்");
        mPostsDatabaseHelper = mPostsDatabaseHelper.getInstance(this);
        mNotificationList = mPostsDatabaseHelper.getAllNotification();
        inflateXMLView();

    }

    private void inflateXMLView() {
        mRecyclerView = findViewById(R.id.recyclerview_notifications);
        mNoNotificationLayout = findViewById(R.id.layout_no_notification);
        setAdapter();
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


    private void setAdapter() {
        if (mNotificationList != null && mNotificationList.size() > 0) {
            mAdapter = new LocalNotificationListAdapter(this, mNotificationList, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            layoutManager.setReverseLayout(true);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoNotificationLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDeleteMessage(Notification notification, int position) {
        mPostsDatabaseHelper.deleteNotification(notification);
        if (mNotificationList != null && mNotificationList.size() > 0) {
            mNotificationList.remove(position);
            mAdapter.notifyDataSetChanged();
            if (mNotificationList != null && mNotificationList.size() == 0) {
                mNoNotificationLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        }
    }
}
