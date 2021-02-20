package com.arulvakku.app.radio.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;


import com.arulvakku.app.MyApplication;
import com.arulvakku.app.radio.PlaybackEvent;
import com.arulvakku.app.radio.SelectStationEvent;
import com.arulvakku.app.model.Station;
import com.arulvakku.app.radio.services.RadioPlayerService;



/**
 * Convert media session callbacks to events
 */
public class MediaSessionCallbackToEventAdapter extends MediaSessionCompat.Callback {

    @Override
    public void onPlay() {
        super.onPlay();
        MyApplication.sBus.post(PlaybackEvent.PLAY);
    }

    @Override
    public void onPause() {
        super.onPause();
        MyApplication.sBus.post(PlaybackEvent.PAUSE);
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.sBus.post(PlaybackEvent.STOP);
    }

    @Override
    public void onPlayFromMediaId(String mediaId, Bundle extras) {
        // TODO Resource
        Station station;
        if (extras.containsKey(RadioPlayerService.EXTRA_STATION)) {
            station = extras.getParcelable(RadioPlayerService.EXTRA_STATION);
        } else {
            station = new Station("Unknown Station", mediaId);
        }
        MyApplication.sBus.post(new SelectStationEvent(station));
    }

    @Override
    public boolean onMediaButtonEvent(final Intent mediaButtonIntent) {
        // superclass calls appropriate onPlay/onPause etc.
        return super.onMediaButtonEvent(mediaButtonIntent);
    }
}