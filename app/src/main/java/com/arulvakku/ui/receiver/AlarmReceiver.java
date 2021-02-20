package com.arulvakku.ui.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import com.arulvakku.R;
import com.arulvakku.ui.ui.home.HomeActivity;
import com.arulvakku.ui.database.DBHelper;
import com.arulvakku.ui.utils.UtilSingleton;

import java.util.Calendar;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    private Context context;
    private String txtVerse = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        prepareNotification();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(context));
        SharedPreferences.Editor sharedPrefEditor = prefs.edit();

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "இன்றைய வசனம்", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("இன்றைய வசனம்");
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, "default");
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("இன்றைய வசனம்")
                .setContentText(txtVerse)
                .setContentIntent(pendingI);

        if (nm != null) {
            nm.notify(1, b.build());
            Calendar nextNotifyTime = Calendar.getInstance();
            nextNotifyTime.add(Calendar.DATE, 1);
            sharedPrefEditor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
            sharedPrefEditor.apply();
        }
    }

    private void prepareNotification() {
        String current_date = UtilSingleton.getInstance().getDate();
        String verse_id = UtilSingleton.getInstance().getVerse(current_date);

        if (verse_id != null && verse_id.length() > 0 && verse_id.length() <= 8) {
            DBHelper dbHelper = DBHelper.getInstance(context);

            String book_no = verse_id.substring(0, 2);
            String chapter_no = verse_id.substring(2, 5);
            String verse_no = verse_id.substring(5, 8);
            String day_verse = dbHelper.getVerseDay(verse_id);

            if (day_verse != null) {
                if (day_verse.equalsIgnoreCase("Same as above")) {
                } else {
                    txtVerse = UtilSingleton.getInstance().bookName[Integer.parseInt(book_no) - 1].trim() + " " + Integer.parseInt(chapter_no) + " : " + Integer.parseInt(verse_no) + "\n" + day_verse;
                }
            }
        }
    }
}