package com.arulvakku.app.radio.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.arulvakku.BuildConfig;
import com.arulvakku.app.MyApplication;
import com.arulvakku.app.radio.AdjustVolumeEvent;
import com.arulvakku.app.radio.AudioFocusEvent;
import com.arulvakku.app.radio.DismissNotificationEvent;
import com.arulvakku.app.radio.HeadphoneDisconnectEvent;
import com.arulvakku.app.radio.MetadataEvent;
import com.arulvakku.app.radio.PlaybackEvent;
import com.arulvakku.app.radio.PlayerErrorEvent;
import com.arulvakku.app.radio.SelectStationEvent;
import com.arulvakku.app.radio.adapter.AudioFocusCallbackToEventAdapter;
import com.arulvakku.app.radio.adapter.BroadcastToEventAdapter;
import com.arulvakku.app.radio.adapter.MediaSessionCallbackToEventAdapter;
import com.arulvakku.app.model.Station;
import com.squareup.otto.Subscribe;

/**
 * Service which controls a {@link RadioNotificationManager} and a {@link RadioPlayerManager} via various events:
 * <ul>
 * <li>AudioFocus gain and loss</li>
 * <li>WifiLock</li>
 * <li>Media Buttons</li>
 * <li>Transport Controls (play, pause, play from media uri, etc.)</li>
 * <li>Headphone disconnects</li>
 * <li>Notification callbacks (dismiss, play, pause)</li>
 * </ul>
 */
public class RadioPlayerService extends Service {
    public static final String TAG = RadioPlayerService.class.getSimpleName();
    public static final String EXTRA_STATION = Station.class.getSimpleName();
    private static final float DUCK_VOLUME = 0.15f;
    private float mLastVolume;
    private boolean mResumeAfterGain;
    private boolean mAdjustVolumeAfterGain;
    private boolean mSkipNextEvent;

    private MediaSessionCompat mMediaSession;
    private WifiManager.WifiLock mWifiLock;
    private BroadcastToEventAdapter mReceiver;
    private AudioFocusCallbackToEventAdapter mAudioFocusCallback;

    private RadioNotificationManager mRadioNotificationManager;
    private RadioPlayerManager mRadioPlayerManager;
    private AudioManager mAudioManager;
    private MetadataRetriever mMetadataRetriever;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (mMediaSession == null) {
            initWifiLock();
            initAudioManager();
            initMediaSession();
            initBroadCastReceiver();

            mRadioNotificationManager = new RadioNotificationManager(this, mMediaSession);
            mRadioPlayerManager = new RadioPlayerManager(this);
            mMetadataRetriever = new MetadataRetriever();

            MyApplication.sBus.register(this);

            initPlayback();
        }

        if (intent == null) {
            // service was recreated by system
            mRadioPlayerManager.restart();
        }

        // Let system restart the service after crash, kill caused by low memory, etc.
        return START_STICKY;
    }

    private void initPlayback() {
        Station station = MyApplication.sDatabase.selectedStation;
        if (station == null) {
            // This should never happen
            Log.e(TAG, "Service started without station");
            stopSelf();
            return;
        }
        Log.i(TAG, "Service started with station: " + station.name);
        handleSelectStationEvent(new SelectStationEvent(station));

        PlaybackEvent state = MyApplication.sDatabase.playbackState;
        handlePlaybackEvent(state);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.sBus.unregister(this);
        unregisterReceiver(mReceiver);
        mAudioManager.abandonAudioFocus(mAudioFocusCallback);

        mRadioNotificationManager.hideNotification();
        mRadioPlayerManager.stop();
        mMetadataRetriever.stop();
        mWifiLock.release();
        mMediaSession.release();

        Log.i(TAG, "Service destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "Task removed");
        MyApplication.sBus.post(PlaybackEvent.STOP);
    }

    private void initBroadCastReceiver() {
        mReceiver = new BroadcastToEventAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        filter.addAction(RadioNotificationManager.ACTION_NOTIFICATION_DISMISS);
        filter.addAction(RadioNotificationManager.ACTION_NOTIFICATION_PLAY);
        filter.addAction(RadioNotificationManager.ACTION_NOTIFICATION_PAUSE);

        registerReceiver(mReceiver, filter);
    }

    private void initWifiLock() {
        mWifiLock = ((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, BuildConfig.APPLICATION_ID);
        mWifiLock.acquire();
    }

    private void initAudioManager() {
        // request AudioFocus + callbacks
        mAudioFocusCallback = new AudioFocusCallbackToEventAdapter();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mLastVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private void initMediaSession() {
        // receive all kinds of playback events
        ComponentName eventReceiver = new ComponentName(this, RadioPlayerService.class);
        PendingIntent buttonReceiverIntent = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(Intent.ACTION_MEDIA_BUTTON),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mMediaSession = new MediaSessionCompat(this, BuildConfig.APPLICATION_ID, eventReceiver, buttonReceiverIntent);
        mMediaSession.setActive(true);

        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        PlaybackStateCompat state = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID)
                .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1, SystemClock.elapsedRealtime())
                .build();
        mMediaSession.setPlaybackState(state);

        mMediaSession.setCallback(new MediaSessionCallbackToEventAdapter());
    }

    @Subscribe
    public void handlePlaybackEvent(PlaybackEvent event) {
        // TODO refactor to something better
        if (mSkipNextEvent) {
            // this event pauses playback, just after focus loss
            mSkipNextEvent = false;
        } else {
            // external event (e.g. user input while during focus loss)
            mResumeAfterGain = false; // overwrite autoresume after focus loss
        }

        switch (event) {
            case PLAY:
                int result = mAudioManager.requestAudioFocus(mAudioFocusCallback, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    Log.w(TAG, "Could not get audio focus");
                    break;
                }
                mRadioPlayerManager.play();
                mRadioNotificationManager.setPlaybackState(PlaybackEvent.PLAY);
                startForeground(mRadioNotificationManager.getNotificationId(), mRadioNotificationManager.getNotification());
                mMetadataRetriever.start();
                break;
            case PAUSE:
                mAudioManager.abandonAudioFocus(mAudioFocusCallback);
                mRadioPlayerManager.pause();
                stopForeground(false);
                mRadioNotificationManager.setPlaybackState(PlaybackEvent.PAUSE);
                mRadioNotificationManager.clearExtraText();
                mMetadataRetriever.stop();
                break;
            case STOP:
                //stopSelf();
                // service will be stopped in application for now
                // player stop and notification dismiss is handled in onDestroy
                break;
        }
    }

    @Subscribe
    public void handleSelectStationEvent(SelectStationEvent event) {
        mRadioPlayerManager.switchStation(event.station);
        mRadioNotificationManager.setStation(event.station);
        mRadioNotificationManager.clearExtraText();
        mMetadataRetriever.switchStation(event.station);
    }

    @Subscribe
    public void handleHeadphoneDisconnectEvent(HeadphoneDisconnectEvent event) {
        // pause playback when user accidentally disconnects headphones
        MyApplication.sBus.post(PlaybackEvent.PAUSE);
    }

    @Subscribe
    public void handleDismissNotificationEvent(DismissNotificationEvent event) {
        // same as stop
        MyApplication.sBus.post(PlaybackEvent.STOP);
    }

    @Subscribe
    public void handlePlayerErrorEvent(PlayerErrorEvent event) {
        // stop playback on error
        MyApplication.sBus.post(PlaybackEvent.STOP);
    }

    @Subscribe
    public void handleAdjustVolumeEvent(AdjustVolumeEvent event) {
        // TODO refactor to something better
        if (mSkipNextEvent) {
            // this event sets the duck volume, just after focus loss
            mSkipNextEvent = false;
        } else {
            // external event (e.g. user input while during focus loss)
            mAdjustVolumeAfterGain = false; // overwrite duck volume
        }

        mRadioPlayerManager.setVolume(event.volume);
    }

    @Subscribe
    public void handleAudioFocusEvent(AudioFocusEvent event) {
        switch (event) {
            case AUDIOFOCUS_GAIN:
                if (mResumeAfterGain) {
                    // resume playback
                    MyApplication.sBus.post(PlaybackEvent.PLAY);
                } else if (mAdjustVolumeAfterGain) {
                    // raise volume
                    MyApplication.sBus.post(new AdjustVolumeEvent(mLastVolume));
                }
                break;

            case AUDIOFOCUS_LOSS:
                MyApplication.sBus.post(PlaybackEvent.PAUSE);
                break;

            case AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time
                PlaybackEvent currentPlaybackState = MyApplication.sDatabase.playbackState;
                mResumeAfterGain = currentPlaybackState == PlaybackEvent.PLAY;
                mSkipNextEvent = true;
                MyApplication.sBus.post(PlaybackEvent.PAUSE);
                break;

            case AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time but can play at low volume
                mLastVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mAdjustVolumeAfterGain = true;
                mSkipNextEvent = true;
                MyApplication.sBus.post(new AdjustVolumeEvent(Math.min(DUCK_VOLUME, mLastVolume)));
                break;
        }
    }

    @Subscribe
    public void handleMetadataEvent(MetadataEvent event) {
        if (event.getSongTitle() != null) {
            mRadioNotificationManager.setExtraText(event.getSongTitle());
        }
    }

}