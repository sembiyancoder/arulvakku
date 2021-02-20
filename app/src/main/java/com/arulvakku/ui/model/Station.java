package com.arulvakku.ui.model;

import androidx.annotation.NonNull;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Stations")
public class Station extends Model implements Comparable<Station> {

    @Column(name = "Name")
    public String name;

    // TODO java.net.URL?
    @Column(name = "url")
    public String url;

    public Station() {
        super();
    }

    public Station(String name, String url) {
        super();
        this.name = name;
        this.url = url;
    }

    @Override
    public int compareTo(@NonNull Station another) {
        // compare lexicographically
        return name.compareToIgnoreCase(another.name);
    }

    @Override
    public String toString() {
        return name + "(" + url + ")";
    }

}
