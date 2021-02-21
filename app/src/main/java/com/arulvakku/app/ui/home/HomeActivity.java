package com.arulvakku.app.ui.home;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
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

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView rosaryCardView;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inflateXML();
        getCurrentDay();

        // change music stream volume while activity is running
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    private void inflateXML() {
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
}