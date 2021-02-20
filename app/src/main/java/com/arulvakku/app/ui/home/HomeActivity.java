package com.arulvakku.app.ui.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.arulvakku.R;
import com.arulvakku.app.MyApplication;
import com.arulvakku.app.receiver.AlarmReceiver;
import com.arulvakku.app.receiver.DeviceBootReceiver;
import com.arulvakku.app.ui.NotificationActivity;
import com.arulvakku.app.ui.rosary.RosaryActivity;
import com.arulvakku.app.utils.ShareLink;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialCardView rosaryCardView;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inflateXML();
        getCurrentDay();

        // change music stream volume while activity is running
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        startPushNotification();
    }

    private void inflateXML() {
        txtTitle = findViewById(R.id.txt_title);
        rosaryCardView = findViewById(R.id.card_view_today_rosary);
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
            overridePendingTransition(R.anim.enter, R.anim.exit);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.sBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sBus.unregister(this);
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
                overridePendingTransition(R.anim.enter, R.anim.exit);
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

    private void startPushNotification() {
        Boolean dailyNotify = true;
        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // if user enabled daily notifications
        if (dailyNotify) {
            //region Enable Daily Notifications
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 6);
            calendar.set(Calendar.MINUTE, 5);
            calendar.set(Calendar.SECOND, 1);
            // if notification time is before selected time, send notification the next day
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }
            if (manager != null) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
            //To enable Boot Receiver class
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            //endregion
        } else { //Disable Daily Notifications
            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && manager != null) {
                manager.cancel(pendingIntent);
                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
            }
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}