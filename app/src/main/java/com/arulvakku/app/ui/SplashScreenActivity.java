package com.arulvakku.app.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.arulvakku.R;
import com.arulvakku.app.database.DBHelper;
import com.arulvakku.app.ui.home.HomeActivity;
import com.arulvakku.app.utils.UtilSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import java.io.IOException;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 1001;
    private Handler mUiHandler = new Handler();
    private MyWorkerThread mWorkerThread = null;
    private ProgressDialog mProgressDialog = null;
    private Context mContext;
    private AppUpdateManager mAppUpdateManager;
    private Task<AppUpdateInfo> mAppUpdateInfoTask;
    private SharedPreferences mSharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        mContext = this;
        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), 0);
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();
    }

    /**
     * Set up the daily notification worker
     */
    /*private void setUpDailyNotificationWorker() {
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
    }*/

    /**
     * To check whether the notification worker is running
     *
     * @return - returns true if running otherwise false
     */
    /*private boolean isDailyNotificationWorkerRunning() {
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
    }*/
    @Override
    protected void onStart() {
        super.onStart();
        if (UtilSingleton.getInstance().isNetworkAvailable(this)) {
            if (isGooglePlayServicesAvailable(SplashScreenActivity.this)) {
                mAppUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                            try {
                                mAppUpdateManager.startUpdateFlowForResult(
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

                mAppUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
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

    /*
     * Copying the arulvakku database to sqlite database
     * */
    private void startActivityIntent() {
        final DBHelper dbHelper = new DBHelper(this);
        if (!dbHelper.checkDataBase()) {
            mWorkerThread = new MyWorkerThread("myWorkerThread");
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle("Database installation");
            mProgressDialog.setMessage("The database needs to be prepared and  optimized for use, Which may take few seconds to a minute. Please  be patient...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            Runnable task = () -> {
                try {
                    dbHelper.createDataBase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mUiHandler.post(this::callIntent);
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
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        boolean isLoggedIn = mSharedPreferences.getBoolean("IsLoggedIn", false);
        Intent mainIntent;
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
