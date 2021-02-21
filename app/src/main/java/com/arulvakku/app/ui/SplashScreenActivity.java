package com.arulvakku.app.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.airbnb.lottie.L;
import com.arulvakku.R;
import com.arulvakku.app.database.DBHelper;
import com.arulvakku.app.fcm.CommonNotificationHelper;
import com.arulvakku.app.fcm.DailyNotificationWorker;
import com.arulvakku.app.fcm.MyFirebaseWorker;
import com.arulvakku.app.receiver.AlarmReceiver;
import com.arulvakku.app.ui.home.HomeActivity;
import com.arulvakku.app.ui.prayer_request.PrayerRequestActivity;
import com.arulvakku.app.utils.Constants;
import com.arulvakku.app.utils.UtilSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String TAG = SplashScreenActivity.class.getSimpleName();

    private static final int MY_REQUEST_CODE = 1001;
    private Handler mUiHandler = new Handler();
    private MyWorkerThread mWorkerThread = null;
    private ProgressDialog progressDialog = null;
    private Context mContext;
    private AppUpdateManager appUpdateManager;
    private Task<AppUpdateInfo> appUpdateInfoTask;
    private ShortcutManager shortcutManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        mContext = this;
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), 0);
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        /*
         * Check the daily notification is scheduled or running
         * */
        setUpDailyNotificationWorker();

        //setDailyVerseNotification();
        Bundle extras = this.getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("type")) {
                String type = extras.getString("type");
                String title = extras.getString("title");
                String message = extras.getString("message");
                Intent intent = null;

                if (type.equalsIgnoreCase(CommonNotificationHelper.NOTIFICATION_DAILY_SAINTS)) {
                    String imageURL = extras.getString("imageURL");
                    intent = new Intent(this, DailySaintActivity.class);
                    intent.putExtra("title", title);
                    intent.putExtra("message", message);
                    intent.putExtra("imageURL", imageURL);
                } else if (type.equalsIgnoreCase(CommonNotificationHelper.NOTIFICATION_MASS_READING)) {
                    intent = new Intent(this, TodaysReadingActivity.class);
                    intent.putExtra("title", title);
                    intent.putExtra("message", message);
                } else if (type.equalsIgnoreCase(CommonNotificationHelper.NOTIFICATION_CHANNEL_ANNOUNCEMENTS)) {
                    intent = new Intent(this, NotificationActivity.class);
                } else if (type.equalsIgnoreCase(CommonNotificationHelper.NOTIFICATION_CHANNEL_PRAYER_REQUEST)) {
                    intent = new Intent(this, PrayerRequestActivity.class);
                } else {
                    startActivityIntent();
                }

                if (intent != null) {
                    final DBHelper dbHelper = new DBHelper(this);
                    if (!dbHelper.checkDataBase()) {
                        startActivityIntent();
                    } else {
                        startActivity(intent);
                        finish();
                    }

                }
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            shortcutManager = getSystemService(ShortcutManager.class);
            ShortcutInfo shortcut;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                shortcut = new ShortcutInfo.Builder(this, "second_shortcut")
                        .setShortLabel(getString(R.string.shortcut_long_label_calendar))
                        .setLongLabel(getString(R.string.shortcut_long_label_calendar))
                        .setIcon(Icon.createWithResource(this, R.drawable.ic_calendar))
                        .setIntent(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.arulvakku.com/calendar.php")))
                        .build();
                shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
            }
        }

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        editor = sharedPreferences.edit();
        editor.putString("ANDROID_ID", android_id);
        editor.commit();
    }

    /**
     * Set up the daily notification worker
     */
    private void setUpDailyNotificationWorker() {
        if (!isDailyNotificationWorkerRunning()) {    //Check whether is scheduled or running
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
                    .addTag(getPackageName())
                    .build();

            WorkManager.getInstance(mContext).enqueueUniqueWork(Constants.WORK_DAILY_NOTIFICATION, ExistingWorkPolicy.REPLACE, oneTimeWorkRequest);
        }
    }

    /**
     * To check whether the notification worker is running
     *
     * @return - returns true if running otherwise false
     */
    private boolean isDailyNotificationWorkerRunning() {
        final boolean[] isRunning = {false};

        ListenableFuture<List<WorkInfo>> listListenableFuture = WorkManager.getInstance(mContext).getWorkInfosByTag(getPackageName());

        try {
            List<WorkInfo> workInfoList = listListenableFuture.get();
            for (int i = 0; i < workInfoList.size(); i++) {
                WorkInfo workInfo = workInfoList.get(i);
                switch (workInfo.getState().name()) {
                    case "ENQUEUED":
                        Log.i(TAG, "Notification worker is enqueued");
                        isRunning[0] = true;
                        break;
                    case "RUNNING":
                        Log.i(TAG, "Notification worker is running");
                        isRunning[0] = true;
                        break;
                    case "SUCCEEDED":
                        Log.i(TAG, "Notification worker is completed");
                        break;
                    case "CANCELLED":
                        Log.i(TAG, "Notification worker is cancelled");
                        break;
                    case "FAILED":
                        Log.i(TAG, "Notification worker is failed");
                        break;
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return isRunning[0];
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (UtilSingleton.getInstance().isNetworkAvailable(this)) {
            if (isGooglePlayServicesAvailable(this)) {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("MainActivity", "getInstanceId failed", task.getException());
                                    return;
                                }

                                String token = task.getResult().getToken();
                                Log.d("MainActivity FCM TOKEN", token);


                                editor = sharedPreferences.edit();
                                editor.putString("FCM_TOKEN", token);
                                editor.commit();

                                WorkManager mWorkManager = WorkManager.getInstance();
                                OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(MyFirebaseWorker.class).build();
                                mWorkManager.enqueue(mRequest);

                            }
                        });
            }


            if (isGooglePlayServicesAvailable(SplashScreenActivity.this)) {
                appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                            try {
                                appUpdateManager.startUpdateFlowForResult(
                                        appUpdateInfo,
                                        AppUpdateType.IMMEDIATE,
                                        SplashScreenActivity.this,
                                        MY_REQUEST_CODE);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        } else if (appUpdateInfo.installStatus() == InstallStatus.INSTALLED) {
                            startActivityIntent();
                        } else {
                            startActivityIntent();
                        }
                    }
                });

                appUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        startActivityIntent();
                    }
                });

            } else {
                Toast.makeText(SplashScreenActivity.this, "App won't run unless update google play services", Toast.LENGTH_SHORT).show();
                finish();
            }

        } else {
            startActivityIntent();
        }
    }

    private void startActivityIntent() {
        final DBHelper dbHelper = new DBHelper(this);
        if (!dbHelper.checkDataBase()) {
            mWorkerThread = new MyWorkerThread("myWorkerThread");
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Database installation");
            progressDialog.setMessage("The database needs to be prepared and  optimized for use, Which may take few seconds to a minute. Please  be patient...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        dbHelper.createDataBase();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callIntent();
                        }
                    });
                }
            };
            mWorkerThread.start();
            mWorkerThread.prepareHandler();
            mWorkerThread.postTask(task);
        } else {
            callIntent();
        }
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public void callIntent() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        boolean isLoggedIn = sharedPreferences.getBoolean("IsLoggedIn", false);
        Intent mainIntent = null;
        if (isLoggedIn) {
            mainIntent = new Intent(mContext, HomeActivity.class);
        } else {
            mainIntent = new Intent(mContext, LoginActivity.class);
        }
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mWorkerThread != null) {
            mWorkerThread.quit();
        }
        super.onDestroy();
    }

    private void setDailyVerseNotification() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 06);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 1);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
        }
    }

    public class MyWorkerThread extends HandlerThread {
        private Handler mWorkerHandler;

        public MyWorkerThread(String name) {
            super(name);
        }

        public void postTask(Runnable task) {
            mWorkerHandler.post(task);
        }

        public void prepareHandler() {
            mWorkerHandler = new Handler(getLooper());
        }
    }


}
