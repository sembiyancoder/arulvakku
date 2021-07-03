package com.arulvakku.app.fcm;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.arulvakku.app.database.PostsDatabaseHelper;
import com.arulvakku.app.model.Notification;
import com.arulvakku.app.utils.Constants;

public class MyFirebaseWorker extends Worker {

    private Context mContext;

    public MyFirebaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        PostsDatabaseHelper postsDatabaseHelper = PostsDatabaseHelper.getInstance(mContext);
        if (postsDatabaseHelper != null) {
            Notification notification = new Notification();
            notification.setmTitle(getInputData().getString(Constants.NOTIFICATION_TITLE));
            notification.setmMessage(getInputData().getString(Constants.NOTIFICATION_MESSAGE));
            notification.setmTime(getInputData().getLong(Constants.NOTIFICATION_TIME,0));
            postsDatabaseHelper.addNotification(notification);
            Log.e("MyFirebaseWorker", "Inserted in database!!!");
        }
        return null;
    }
}