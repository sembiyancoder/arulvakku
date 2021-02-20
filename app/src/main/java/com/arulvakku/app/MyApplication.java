package com.arulvakku.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;

import androidx.multidex.MultiDex;

import com.activeandroid.ActiveAndroid;
import com.arulvakku.BuildConfig;
import com.arulvakku.app.radio.EventLogger;
import com.arulvakku.app.radio.PlaybackEvent;
import com.arulvakku.app.model.RadioDatabase;
import com.arulvakku.app.radio.services.RadioPlayerService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Vivek E on 12/04/17.
 */

public class MyApplication extends Application {

    public static final String MIXPANEL_TOKEN = "a2887a817985e32a07633df7c7e0b5f8";
    public static Bus sBus = new Bus(ThreadEnforcer.MAIN);
    public static RadioDatabase sDatabase;

    public static int mCurrentChapterNo = 0;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        sBus.register(this);
        setupLogger();
        setupDatabase();


    }

    protected void setupLogger() {
        sBus.register(new EventLogger());
    }

    protected void setupDatabase() {
        SharedPreferences prefs = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        ActiveAndroid.initialize(this);
        sDatabase = new RadioDatabase(prefs);
        sBus.register(sDatabase);
    }

    @Subscribe
    public void handlePlaybackEvent(PlaybackEvent event) {
        switch (event) {
            case PLAY:
                if (sDatabase.selectedStation == null) {
                    throw new IllegalStateException("Select a Station before playing");
                }
                // start service
                Intent serviceIntent = new Intent(this, RadioPlayerService.class);
                startService(serviceIntent);
                break;
            case STOP:
                // stop service
                Intent intent = new Intent(getApplicationContext(), RadioPlayerService.class);
                stopService(intent);
                break;
        }
    }

}