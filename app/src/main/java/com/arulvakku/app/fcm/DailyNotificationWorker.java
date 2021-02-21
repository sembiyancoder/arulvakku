package com.arulvakku.app.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.arulvakku.R;
import com.arulvakku.app.database.DBHelper;
import com.arulvakku.app.ui.SplashScreenActivity;
import com.arulvakku.app.utils.Constants;
import com.arulvakku.app.utils.UtilSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DailyNotificationWorker extends Worker {

    private final Context mContext;

    public DailyNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        String todayDate = UtilSingleton.getDate();
        try {
            String strAssetJSON = loadJSONFromAsset();

            JSONObject obj = new JSONObject(strAssetJSON);
            JSONArray jsonArray = obj.optJSONArray("Result");
            DBHelper dbHelper = DBHelper.getInstance(mContext);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String date = jsonObject.optString("sVersesDate");
                if (date.equalsIgnoreCase(todayDate)) {
                    String verse_id = jsonObject.optString("sVerses");
                    String book_no = verse_id.substring(0, 2);
                    String chapter_no = verse_id.substring(2, 5);
                    String verse_no = verse_id.substring(5, 8);

                    String verseOfTheDay = dbHelper.getVerseDay(verse_id);
                    String book = UtilSingleton.bookName[Integer.parseInt(book_no) - 1].trim() + " " + Integer.parseInt(chapter_no) + " : " + Integer.parseInt(verse_no);

                    showNotification(mContext, mContext.getString(R.string.app_name), book + "\n" + verseOfTheDay, new Intent(mContext, SplashScreenActivity.class));
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        reScheduleDailyNotification();
    }

    private void reScheduleDailyNotification() {
        long initialDelayMills;

        Calendar currentCalendar = Calendar.getInstance();  //Current date time

        Calendar targetCalendar = Calendar.getInstance();   //Target date time with 06 AM default
        targetCalendar.set(Calendar.HOUR_OF_DAY, 6);
        targetCalendar.set(Calendar.MINUTE, 0);

        if (currentCalendar.equals(targetCalendar)) {         //Check the current time is 06 AM
            initialDelayMills = 1000;
        } else if (currentCalendar.before(targetCalendar)) {  //Check the current time is before 06 AM
            initialDelayMills = targetCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
        } else {                                              //If the current time is after 06 AM
            // Add one more day in the calendar and calculate the millis
            targetCalendar.add(Calendar.DATE, 1);
            initialDelayMills = targetCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
        }

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(DailyNotificationWorker.class)
                .setInitialDelay(initialDelayMills, TimeUnit.MILLISECONDS)
                .addTag(mContext.getPackageName())
                .build();

        WorkManager.getInstance(mContext).enqueueUniqueWork(Constants.WORK_DAILY_NOTIFICATION, ExistingWorkPolicy.REPLACE, oneTimeWorkRequest);
        Toast.makeText(mContext, "Notification scheduled again " + initialDelayMills, Toast.LENGTH_LONG).show();
    }

    public static void showNotification(Context context, String title, String message, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews contentViewSmall = new RemoteViews(context.getPackageName(), R.layout.layout_custom_notification);

        //Share intent
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_TITLE, "Share Link");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        PendingIntent pendingPrevIntent = PendingIntent.getService(context, 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentViewSmall.setOnClickPendingIntent(R.id.textViewShare, pendingPrevIntent);

        contentViewSmall.setTextViewText(R.id.textViewNotificationTitle, title);
        contentViewSmall.setTextViewText(R.id.textViewVerse, message);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(Constants.DAILY_NOTIFICATION_CHANNEL_ID, Constants.DAILY_NOTIFICATION_CHANNEL_NAME, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.DAILY_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(title)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(message);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();
        notification.contentView = contentViewSmall;

        notificationManager.notify(Constants.DAILY_NOTIFICATION_ID, notification);
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("daily_verses.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

}
