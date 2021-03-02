package com.arulvakku.app.radio.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.arulvakku.app.MyApplication;
import com.arulvakku.app.radio.DismissNotificationEvent;
import com.arulvakku.app.radio.HeadphoneDisconnectEvent;
import com.arulvakku.app.radio.PlaybackEvent;
import com.arulvakku.app.radio.services.RadioNotificationManager;


/**
 * Convert broadcasts to events
 */
public class BroadcastToEventAdapter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(BroadcastToEventAdapter.class.getSimpleName(), "onReceive: " + intent.getAction());

        switch (intent.getAction()) {
            case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                MyApplication.sBus.post(new HeadphoneDisconnectEvent());
                break;

            case RadioNotificationManager.ACTION_NOTIFICATION_DISMISS:
                MyApplication.sBus.post(new DismissNotificationEvent());
                break;

            case RadioNotificationManager.ACTION_NOTIFICATION_PLAY:
                MyApplication.sBus.post(PlaybackEvent.PLAY);
                break;

            case RadioNotificationManager.ACTION_NOTIFICATION_PAUSE:
                MyApplication.sBus.post(PlaybackEvent.PAUSE);
                break;
        }
    }

}