package com.arulvakku.app.radio;

import com.arulvakku.app.model.Station;


public class SelectStationEvent {
    public final Station station;

    public SelectStationEvent(Station station) {
        this.station = station;
    }
}
