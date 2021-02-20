package com.arulvakku.app.event.adapter;

import android.media.AudioManager;

import com.arulvakku.app.MyApplication;
import com.arulvakku.app.event.AudioFocusEvent;



/**
 * Convert audio focus callbacks to events
 */
public class AudioFocusCallbackToEventAdapter implements AudioManager.OnAudioFocusChangeListener {

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                MyApplication.sBus.post(AudioFocusEvent.AUDIOFOCUS_GAIN);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                MyApplication.sBus.post(AudioFocusEvent.AUDIOFOCUS_LOSS);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                MyApplication.sBus.post(AudioFocusEvent.AUDIOFOCUS_LOSS_TRANSIENT);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                MyApplication.sBus.post(AudioFocusEvent.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK);
                break;
        }
    }
}
