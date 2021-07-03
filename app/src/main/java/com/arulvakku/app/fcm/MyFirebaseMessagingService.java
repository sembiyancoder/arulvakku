package com.arulvakku.app.fcm;

import android.content.SharedPreferences;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.arulvakku.R;
import com.arulvakku.app.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String notification_type = remoteMessage.getData().get("notification_type");
            if (title != null && body != null && notification_type != null && notification_type.equalsIgnoreCase("Notifications")) {
                insertInDB(title, body);
                handleDataMessage(title, body, notification_type, "");
            } else if (title != null && body != null && notification_type.equalsIgnoreCase("Prayer Request")) {
                handleDataMessage(title, body, notification_type, "");
            } else if (title != null && body != null && notification_type.equalsIgnoreCase("Daily Saints")) {
                handleDataMessage(title, body, notification_type, "");
            } else if (title != null && body != null && notification_type.equalsIgnoreCase("Daily Mass Readings")) {
                handleDataMessage(title, body, notification_type, "");
            } else if (title != null && body != null && notification_type.equalsIgnoreCase("Test Notification")) {
                handleDataMessage(title, body, notification_type, "");
            } else {
                handleDataMessage(title, body, notification_type, "");
            }
        }
    }

    /**
     * Saving the notification in database to show in the notitication activity
     */
    private void insertInDB(String title, String body) {
        Data inputData = new Data.Builder()
                .putString(Constants.NOTIFICATION_TITLE, title)
                .putString(Constants.NOTIFICATION_MESSAGE, body)
                .putLong(Constants.NOTIFICATION_TIME, System.currentTimeMillis())
                .build();

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MyFirebaseWorker.class)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
    }

    private void handleDataMessage(String title, String message, String type, String imageURL) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            CommonNotificationHelper commonNotificationHelper = new CommonNotificationHelper(this);
            commonNotificationHelper.createNotification(title, message, type, imageURL);
        } else {
            CommonNotificationHelper commonNotificationHelper = new CommonNotificationHelper(this);
            commonNotificationHelper.createNotification(title, message, type, imageURL);
        }

    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        if (token != null && !token.isEmpty()) {
            SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.FCM_TOKEN, token);
            editor.putBoolean(Constants.FCM_TOKEN_UPDATED, false);
            editor.commit();
        }
    }
}