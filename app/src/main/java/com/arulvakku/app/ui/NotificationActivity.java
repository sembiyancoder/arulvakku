package com.arulvakku.app.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.adapter.PushNotificationAdapter;
import com.arulvakku.app.database.DatabaseClient;
import com.arulvakku.app.model.PushNotification;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class NotificationActivity extends AppCompatActivity implements PushNotificationAdapter.onItemListener {

    private RecyclerView mRecyclerView;
    private PushNotificationAdapter mAdapter;
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

        getPushNotificationList();
    }


    private void getPushNotificationList() {
        class GetTasks extends AsyncTask<Void, Void, List<PushNotification>> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mShimmerFrameLayout.startShimmerAnimation();
                mShimmerFrameLayout.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<PushNotification> doInBackground(Void... voids) {
                List<PushNotification> taskList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .pushNotificationDao()
                        .getAll();
                return taskList;
            }

            @Override

            protected void onPostExecute(List<PushNotification> tasks) {
                super.onPostExecute(tasks);
                if (tasks != null && tasks.size() > 0) {
                    mLinearLayout.setVisibility(View.GONE);
                    mAdapter = new PushNotificationAdapter(NotificationActivity.this, tasks, NotificationActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    mLinearLayout.setVisibility(View.VISIBLE);
                }
                mShimmerFrameLayout.stopShimmerAnimation();
                mShimmerFrameLayout.setVisibility(View.GONE);
            }
        }
        GetTasks gt = new GetTasks();
        gt.execute();
    }


    private void deleteNotification(final PushNotification pushNotification) {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .pushNotificationDao()
                        .delete(pushNotification);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                getPushNotificationList();
            }
        }
        DeleteTask dt = new DeleteTask();
        dt.execute();
    }


    @Override
    public void onDeleteItem(PushNotification pushNotification) {
        deleteNotification(pushNotification);
    }


    @Override
    public void onResume() {
        super.onResume();
        mShimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        mShimmerFrameLayout.stopShimmerAnimation();
        super.onPause();
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
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
