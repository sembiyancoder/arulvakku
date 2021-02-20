package com.arulvakku.ui.event;

import com.arulvakku.ui.model.Station;


public class SelectStationEvent {
    public final Station station;

    public SelectStationEvent(Station station) {
        this.station = station;
    }
}
