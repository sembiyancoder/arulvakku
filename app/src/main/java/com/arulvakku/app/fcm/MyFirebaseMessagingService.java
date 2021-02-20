package com.arulvakku.app.fcm;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.arulvakku.R;
import com.arulvakku.app.database.DatabaseClient;
import com.arulvakku.app.model.PushNotification;
import com.arulvakku.app.utils.UtilSingleton;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleDataMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), "இன்றைய வசனம்","");
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            String type = remoteMessage.getData().get("type").toString();
            String title = "", body = "";
            String imageURL = "";

            if (type.equalsIgnoreCase(CommonNotificationHelper.NOTIFICATION_DAILY_SAINTS)) {
                title = remoteMessage.getData().get("title").toString();
                body = remoteMessage.getData().get("body").toString();
                imageURL = remoteMessage.getData().get("imageURL").toString();
                handleDataMessage(title, body, CommonNotificationHelper.NOTIFICATION_DAILY_SAINTS,imageURL);

            } else if (type.equalsIgnoreCase(CommonNotificationHelper.NOTIFICATION_MASS_READING)) {
                title = remoteMessage.getData().get("title").toString();
                body = remoteMessage.getData().get("body").toString();
                handleDataMessage(title, body, CommonNotificationHelper.NOTIFICATION_MASS_READING,"");
            } else if (type.equalsIgnoreCase(CommonNotificationHelper.NOTIFICATION_CHANNEL_PRAYER_REQUEST)) {
                title = remoteMessage.getData().get("title").toString();
                body = remoteMessage.getData().get("body").toString();
                handleDataMessage(title, body, CommonNotificationHelper.NOTIFICATION_CHANNEL_PRAYER_REQUEST,"");
            }else if (type.equalsIgnoreCase(CommonNotificationHelper.NOTIFICATION_CHANNEL_ANNOUNCEMENTS)) {
                title = remoteMessage.getData().get("title").toString();
                body = remoteMessage.getData().get("body").toString();
                new SavePushNotification(title, body, type).execute();
                handleDataMessage(title, body, CommonNotificationHelper.NOTIFICATION_CHANNEL_ANNOUNCEMENTS,"");
            } else {
                title = remoteMessage.getData().get("title").toString();
                body = remoteMessage.getData().get("body").toString();
                handleDataMessage(title, body, CommonNotificationHelper.NOTIFICATION_CHANNEL_ANNOUNCEMENTS,"");
            }
        }
    }


    private void handleDataMessage(String title, String message, String type,String imageURL) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            /*CommonNotificationHelper commonNotificationHelper = new CommonNotificationHelper(this);
            commonNotificationHelper.foreGroundNotification(title, message);
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("body", message);
            pushNotification.putExtra("title", title);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);*/
            CommonNotificationHelper commonNotificationHelper = new CommonNotificationHelper(this);
            commonNotificationHelper.createNotification(title, message, type,imageURL);
        } else {
            // If the app is in background, firebase itself handles the notification
            CommonNotificationHelper commonNotificationHelper = new CommonNotificationHelper(this);
            commonNotificationHelper.createNotification(title, message, type,imageURL);
        }
    }


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), 0);
        editor = sharedPreferences.edit();
        editor.putString("FCM_TOKEN", token);
        editor.commit();

        if (UtilSingleton.getInstance().isNetworkAvailable(this)) {
            WorkManager mWorkManager = WorkManager.getInstance();
            OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(MyFirebaseWorker.class).build();
            mWorkManager.enqueue(mRequest);
        }
    }


    class SavePushNotification extends AsyncTask<Void, Void, Void> {

        private String title = "", message = "", timestamp = "";

        public SavePushNotification(String title, String message, String timestamp) {
            this.title = title;
            this.timestamp = timestamp;
            this.message = message;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //creating a task
            PushNotification pushNotification = new PushNotification();
            pushNotification.setMessage(message);
            pushNotification.setTimestamp(timestamp);
            pushNotification.setTitle(title);

            //adding to database
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .pushNotificationDao()
                    .insert(pushNotification);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(MyFirebaseMessagingService.class.getSimpleName(), "Notification Updated");
        }
    }
}