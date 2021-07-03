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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.arulvakku.R;
import com.arulvakku.app.database.DBHelper;
import com.arulvakku.app.ui.home.HomeActivity;
import com.arulvakku.app.utils.Constants;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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
    private String mNotificationType = "";

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        mContext = this;
        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), 0);
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();
        getFCMToken();
    }

    private void getFCMToken() {
        if (UtilSingleton.getInstance().isNetworkAvailable(this)) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashScreenActivity.this, new com.google.android.gms.tasks.OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();
                    Log.d("T:", newToken);
                    if (newToken != null && !newToken.isEmpty()) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        String existing_fcm = mSharedPreferences.getString(Constants.FCM_TOKEN, "");
                        if (!existing_fcm.equalsIgnoreCase(newToken)) {
                            editor.putString(Constants.FCM_TOKEN, newToken);
                            editor.commit();
                        } else {
                            editor.putString(Constants.FCM_TOKEN, newToken);
                            editor.commit();
                            editor.putBoolean(Constants.FCM_TOKEN_UPDATED, false);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("type")) {
            mNotificationType = intent.getStringExtra("type");
        }
        checkForUpdate();
    }

    private void checkForUpdate() {
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

        Intent mainIntent = new Intent(mContext, HomeActivity.class);
        mainIntent.putExtra("type", mNotificationType);
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
