package com.arulvakku.app.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.arulvakku.R;
import com.arulvakku.app.ui.SplashScreenActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;


/**
 * Created by Vivek E on 21-05-2019.
 */
public class CommonNotificationHelper {

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    public static final String NOTIFICATION_CHANNEL_ANNOUNCEMENTS = "Announcements";
    public static final String NOTIFICATION_DAILY_SAINTS = "Daily Saints";
    public static final String NOTIFICATION_MASS_READING = "Daily Mass Readings";
    public static final String NOTIFICATION_CHANNEL_DEFAULT = "Daily Verses";
    public static final String NOTIFICATION_CHANNEL_PRAYER_REQUEST = "Prayer Request";

    public static final String NOTIFICATION_CHANNEL_ANNOUNCEMENTS_ID = "10001";
    public static final String NOTIFICATION_CHANNEL_DEFAULT_ID = "10004";
    public static final String NOTIFICATION_DAILY_SAINTS_ID = "10002";
    public static final String NOTIFICATION_MASS_READING_ID = "10003";

    public static final String NOTIFICATION_CHANNEL_PRAYER_REQUEST_ID = "10005";


    public CommonNotificationHelper(Context context) {
        mContext = context;
    }

    /**
     * Create and push the notification
     */
    public void createNotification(String title, String message, String type, String imageURL) {

        Intent resultIntent = new Intent(mContext, SplashScreenActivity.class);
        resultIntent.putExtra("is_clicked", true);
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("message", message);
        resultIntent.putExtra("type", type);
        resultIntent.putExtra("imageURL", imageURL);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/raw/notification"); // NOTIFICATION MP3



        if (type.equalsIgnoreCase(NOTIFICATION_DAILY_SAINTS)) {

            mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_DAILY_SAINTS);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);


            Bitmap bitmap = getBitmapfromUrl(imageURL);
            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(bitmap)
                    .setSound(alarmSound)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)) /*Notification with Image*/
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent);
        } else if (type.equalsIgnoreCase(NOTIFICATION_MASS_READING)) {

            mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_MASS_READING);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);

            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                    .setSound(alarmSound)
                    .setContentIntent(resultPendingIntent);
        } else if (type.equalsIgnoreCase(NOTIFICATION_CHANNEL_ANNOUNCEMENTS)) {

            mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ANNOUNCEMENTS);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);

            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                    .setSound(alarmSound)
                    .setContentIntent(resultPendingIntent);
        } else if (type.equalsIgnoreCase(NOTIFICATION_CHANNEL_PRAYER_REQUEST)) {

            mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_PRAYER_REQUEST);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);

            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                    .setSound(alarmSound)
                    .setContentIntent(resultPendingIntent);
        } else {

            mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ANNOUNCEMENTS);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);


            mBuilder.setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                    .setSound(alarmSound)
                    .setContentIntent(resultPendingIntent);
        }


        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = null;
            if (type.equalsIgnoreCase(NOTIFICATION_DAILY_SAINTS)) {
                notificationChannel = new NotificationChannel(NOTIFICATION_DAILY_SAINTS_ID, NOTIFICATION_DAILY_SAINTS, importance);
                mBuilder.setChannelId(NOTIFICATION_DAILY_SAINTS_ID);
            } else if (type.equalsIgnoreCase(NOTIFICATION_MASS_READING)) {
                notificationChannel = new NotificationChannel(NOTIFICATION_MASS_READING_ID, NOTIFICATION_MASS_READING, importance);
                mBuilder.setChannelId(NOTIFICATION_MASS_READING_ID);
            } else if (type.equalsIgnoreCase(NOTIFICATION_CHANNEL_ANNOUNCEMENTS)) {
                notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ANNOUNCEMENTS_ID, NOTIFICATION_CHANNEL_ANNOUNCEMENTS, importance);
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ANNOUNCEMENTS_ID);
            } else if (type.equalsIgnoreCase(NOTIFICATION_CHANNEL_PRAYER_REQUEST)) {
                notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_PRAYER_REQUEST_ID, NOTIFICATION_CHANNEL_PRAYER_REQUEST, importance);
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_PRAYER_REQUEST_ID);
            }else {
                notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_DEFAULT_ID, NOTIFICATION_CHANNEL_DEFAULT, importance);
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_DEFAULT_ID);
            }
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;

            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        assert mNotificationManager != null;
        mNotificationManager.notify( new Random().nextInt(100)/* Request Code */, mBuilder.build());
    }




    /*
     *To get a Bitmap image from the URL received
     * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

}
