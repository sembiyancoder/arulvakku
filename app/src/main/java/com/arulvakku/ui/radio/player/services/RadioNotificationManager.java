package com.arulvakku.ui.radio.player.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;

import com.arulvakku.BuildConfig;
import com.arulvakku.R;
import com.arulvakku.ui.activity.RadioActivity;
import com.arulvakku.ui.event.PlaybackEvent;
import com.arulvakku.ui.event.adapter.BroadcastToEventAdapter;
import com.arulvakku.ui.model.Station;


/**
 * Manages a single {@link Notification}
 */
public class RadioNotificationManager {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "playback";
    public static final String ACTION_NOTIFICATION_DISMISS = BuildConfig.APPLICATION_ID + ".ACTION_NOTIFICATION_DISMISS";
    public static final String ACTION_NOTIFICATION_PLAY = BuildConfig.APPLICATION_ID + ".ACTION_NOTIFICATION_PLAY";
    public static final String ACTION_NOTIFICATION_PAUSE = BuildConfig.APPLICATION_ID + ".ACTION_NOTIFICATION_PAUSE";

    private NotificationManager mNotificationManager;
    private PendingIntent mMainIntent;
    private PendingIntent mPlayIntent;
    private PendingIntent mPauseIntent;
    private PendingIntent mDismissIntent;
    private Notification mNotification;

    private Context mContext;
    private MediaSessionCompat mMediaSession;
    private Station mStation = new Station("", "");
    private PlaybackEvent mPlaybackState = PlaybackEvent.PLAY;
    private String mExtraText;

    private String showMessage  = " ";


    RadioNotificationManager(Context context, MediaSessionCompat mediaSession) {
        mContext = context;
        mMediaSession = mediaSession;

        // init Intents
        mPlayIntent = createBroadcastIntent(ACTION_NOTIFICATION_PLAY);
        mPauseIntent = createBroadcastIntent(ACTION_NOTIFICATION_PAUSE);
        mDismissIntent = createBroadcastIntent(ACTION_NOTIFICATION_DISMISS);
        mMainIntent = PendingIntent.getActivity(mContext, 0,
                new Intent(mContext, RadioActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        showNotification();
    }

    private PendingIntent createBroadcastIntent(String action) {
        Intent intent = new Intent(mContext, BroadcastToEventAdapter.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(mContext,
                NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void showNotification() {
        String extra = mExtraText != null ? mExtraText : " ";
        showMessage = mExtraText;

        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(R.drawable.ic_radio)
                .setContentTitle("அருள்வாக்கு வானொலி")
                .setContentText(showMessage)
                .setContentIntent(mMainIntent)
                .setDeleteIntent(mDismissIntent)
                .setWhen(0)
                .setPriority(Notification.PRIORITY_HIGH)
                .setOnlyAlertOnce(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // use media style
            MediaSession.Token token = (MediaSession.Token) mMediaSession.getSessionToken().getToken();

            Notification.MediaStyle style = new Notification.MediaStyle()
                    .setMediaSession(token)
                    .setShowActionsInCompactView(0);

            if (mPlaybackState == PlaybackEvent.PLAY) {
                builder.addAction(R.drawable.ic_pause, mContext.getString(R.string.pause), mPauseIntent)
                        .setOngoing(true);
            } else {
                builder.addAction(R.drawable.ic_play, mContext.getString(R.string.play), mPlayIntent)
                        .setOngoing(false);
            }

            builder.setStyle(style)
                    .setCategory(Notification.CATEGORY_TRANSPORT)
                    .setShowWhen(false);
        } else {
            // use remote view
            RemoteViews notificationView = new RemoteViews(mContext.getPackageName(),
                    R.layout.notification_playback_controls);
            notificationView.setTextViewText(R.id.title, mStation.name);
            notificationView.setTextViewText(R.id.extra_info, showMessage);

            if (mPlaybackState == PlaybackEvent.PLAY) {
                notificationView.setImageViewResource(R.id.play_pause, R.drawable.ic_pause);
                notificationView.setOnClickPendingIntent(R.id.play_pause, mPauseIntent);
                builder.setOngoing(true);
            } else {
                notificationView.setImageViewResource(R.id.play_pause, R.drawable.ic_play);
                notificationView.setOnClickPendingIntent(R.id.play_pause, mPlayIntent);
                builder.setOngoing(false);
            }

            builder.setContent(notificationView);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // add notification channel
            NotificationChannel channel = mNotificationManager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                CharSequence channelName = mContext.getString(R.string.channel);
                int importance = NotificationManager.IMPORTANCE_LOW;
                channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
                mNotificationManager.createNotificationChannel(channel);
            }
            builder.setChannelId(CHANNEL_ID);
        }

        mNotification = builder.build();
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    void hideNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    void setStation(Station station) {
        mStation = station;
        showNotification();
    }

    void setPlaybackState(PlaybackEvent playbackState) {
        mPlaybackState = playbackState;
        showNotification();
    }

    void setExtraText(String extraText) {
        mExtraText = extraText;
        showNotification();
    }

    void clearExtraText() {
        mExtraText = null;
        showNotification();
    }

    int getNotificationId() {
        return NOTIFICATION_ID;
    }

    Notification getNotification() {
        return mNotification;
    }
}