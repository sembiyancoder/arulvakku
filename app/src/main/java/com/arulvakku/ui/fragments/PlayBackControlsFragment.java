package com.arulvakku.ui.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.arulvakku.R;
import com.arulvakku.ui.app.MyApplication;
import com.arulvakku.ui.event.PlaybackEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Subscribe;



public class PlayBackControlsFragment extends Fragment implements View.OnClickListener {
    private Drawable mPlayDrawable;
    private Drawable mPauseDrawable;
    private FloatingActionButton mFloatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);

        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(this);

        mPlayDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_play);
        mPauseDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_pause);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.sBus.register(this);

        // Playback state may have changed while paused
        refreshUi();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyApplication.sBus.unregister(this);
    }

    private void refreshUi() {
        handlePlaybackEvent(MyApplication.sDatabase.playbackState);
    }

    @Override
    public void onClick(View v) {
        if (MyApplication.sDatabase.playbackState == PlaybackEvent.PLAY) {
            MyApplication.sBus.post(PlaybackEvent.PAUSE);
        } else {
            MyApplication.sBus.post(PlaybackEvent.PLAY);
        }
    }

    @Subscribe
    public void handlePlaybackEvent(PlaybackEvent event) {
        switch (event) {
            case PLAY:
                mFloatingActionButton.setImageDrawable(mPauseDrawable);
                mFloatingActionButton.show();
                break;
            case PAUSE:
                mFloatingActionButton.setImageDrawable(mPlayDrawable);
                mFloatingActionButton.show();
                break;
            case STOP:
                mFloatingActionButton.setImageDrawable(mPlayDrawable);
                mFloatingActionButton.hide();
                break;
        }
    }

}
