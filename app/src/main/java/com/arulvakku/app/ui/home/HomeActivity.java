package com.arulvakku.app.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.arulvakku.R;
import com.arulvakku.app.MyApplication;
import com.arulvakku.app.ui.NotificationActivity;
import com.arulvakku.app.ui.rosary.RosaryActivity;
import com.arulvakku.app.utils.ShareLink;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView rosaryCardView;
    private TextView txtTitle;
    private AudioManager am;
    private BroadcastReceiver receiver;
    /**
     * This variable used to avoid multiple call on receive
     * silent -0
     * vibrate - 1
     * normal - 2
     */
    View view;
    private int mediaMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        inflateXML();
        getCurrentDay();
        // change music stream volume while activity is running
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        silentStatus();
    }

    private void inflateXML() {
        view = findViewById(R.id.parent);
        txtTitle = findViewById(R.id.textView);
        rosaryCardView = findViewById(R.id.cardView2);
        rosaryCardView.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            ShareLink shareObject = new ShareLink(this, "https://play.google.com/store/apps/details?id=com.arulvakku");
            shareObject.shareLink();
            return true;
        } else if (id == R.id.action_notification) {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.sBus.register(this);

        // set receiver for silent mode
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //code...
//                Log.d("TAG", "onReceive: "+intent.toString());

                int a = am.getRingerMode();
                if (mediaMode != a)
                    silentStatus();
            }
        };
        IntentFilter filter = new IntentFilter(
                AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sBus.unregister(this);
        // Unregister receiver
        unregisterReceiver(receiver);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_view_today_rosary:
                Intent intent = new Intent(this, RosaryActivity.class);
                if (txtTitle.getText().toString().equals("மகிழ்ச்சி நிறை மறை உண்மைகள்")) {
                    intent.putExtra("day", 0);
                } else if (txtTitle.getText().toString().equals("துயர் மறை உண்மைகள்")) {
                    intent.putExtra("day", 1);
                } else if (txtTitle.getText().toString().equals("மகிமை நிறை மறை உண்மைகள்")) {
                    intent.putExtra("day", 2);
                } else if (txtTitle.getText().toString().equals("ஒளி நிறை மறை உண்மைகள்")) {
                    intent.putExtra("day", 3);
                }
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    public void getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY: //மகிமை நிறை மறை உண்மைகள்:
                txtTitle.setText("மகிமை நிறை மறை உண்மைகள்");
                break;
            case Calendar.MONDAY: //மகிழ்ச்சி நிறை மறை உண்மைகள்:
                txtTitle.setText("மகிழ்ச்சி நிறை மறை உண்மைகள்");
                break;
            case Calendar.TUESDAY: //துயர் மறை உண்மைகள்:
                txtTitle.setText("துயர் மறை உண்மைகள்");
                break;
            case Calendar.WEDNESDAY: //மகிமை நிறை மறை உண்மைகள்:
                txtTitle.setText("மகிமை நிறை மறை உண்மைகள்");
                break;
            case Calendar.THURSDAY: //ஒளி நிறை மறை உண்மைகள்
                txtTitle.setText("ஒளி நிறை மறை உண்மைகள்");
                break;
            case Calendar.FRIDAY: //துயர் மறை உண்மைகள்:
                txtTitle.setText("துயர் மறை உண்மைகள்");
                break;
            case Calendar.SATURDAY: //மகிழ்ச்சி நிறை மறை உண்மைகள்:
                txtTitle.setText("மகிழ்ச்சி நிறை மறை உண்மைகள்");
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }


    // Get mobile ringer mode status
    private void silentStatus() {
        mediaMode = am.getRingerMode();
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                Log.i("MyApp", "Silent mode");
//                textView.setText("Silent mode");
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Log.i("MyApp", "Vibrate mode");
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                Log.i("MyApp", "Normal mode");
                notifySilentMode();
                break;
        }
    }

    // Show notification to notify user to put mobile on silent mode
    private void notifySilentMode() {
        Snackbar snackbar = Snackbar
                .make(view, "ஆலயத்தினுள்ளே கைபேசியை அமைதிப்படுத்துக!", Snackbar.LENGTH_LONG)
                .setAction("SETTINGS", view -> openSettings());
        snackbar.show();
    }

}