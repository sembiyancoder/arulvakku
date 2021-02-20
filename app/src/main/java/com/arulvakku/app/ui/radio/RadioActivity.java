package com.arulvakku.app.ui.radio;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.adapter.RadioTimeAdapter;
import com.arulvakku.app.adapter.StationAdapter;
import com.arulvakku.app.MyApplication;
import com.arulvakku.app.event.BufferEvent;
import com.arulvakku.app.event.DatabaseEvent;
import com.arulvakku.app.event.PlaybackEvent;
import com.arulvakku.app.event.SelectStationEvent;
import com.arulvakku.app.model.RadioModel;
import com.arulvakku.app.model.Station;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RadioActivity extends AppCompatActivity implements RadioTimeAdapter.onItemSelectedListener, StationAdapter.StationClickListener {


    private ImageButton btn;

    private RecyclerView recyclerView;
    private RadioTimeAdapter adapter;
    private StationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = findViewById(R.id.recycler_view);
        setAdapter();

        mAdapter = new StationAdapter(this);
        mAdapter.showFavorites(false);

        RecyclerView favoritesList = (RecyclerView) findViewById(R.id.favorites_list);
        favoritesList.setLayoutManager(new LinearLayoutManager(this));
        favoritesList.setAdapter(mAdapter);
        favoritesList.setHasFixedSize(true);

        List<Station> stations = new ArrayList<>(MyApplication.sDatabase.getStations());

        if (!stations.get(0).equals(MyApplication.sDatabase.selectedStation)) {
            // play
            MyApplication.sBus.post(new SelectStationEvent(stations.get(0)));
            MyApplication.sBus.post(PlaybackEvent.PLAY);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        MyApplication.sBus.register(this);
        // Playback state and stations may have changed while paused
        refreshList();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyApplication.sBus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void refreshList() {
        // shallow copy and sort
        List<Station> stations = new ArrayList<>(MyApplication.sDatabase.getStations());
        Collections.sort(stations);
        mAdapter.setStations(stations);
        mAdapter.setSelection(MyApplication.sDatabase.selectedStation);
    }


    private void setAdapter() {
        adapter = new RadioTimeAdapter(this, getList(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private List<RadioModel> getList(){

        List<RadioModel> radioModelList = new ArrayList<>();

        RadioModel radioModel = new RadioModel();
        radioModel.setTime("12.00 AM && 12.00 PM");
        radioModel.setTitle("மூவேளை செபம், திருப்பலி (if recorded previous day)");
        radioModelList.add(radioModel);

        radioModel = new RadioModel();
        radioModel.setTime("03.00 AM && 03.00 PM");
        radioModel.setTitle("இறைஇரக்க செபமாலை");
        radioModelList.add(radioModel);

        radioModel = new RadioModel();
        radioModel.setTime("05.30 AM && 05.30 PM");
        radioModel.setTitle("மூவேளை செபம்");
        radioModelList.add(radioModel);

        radioModel = new RadioModel();
        radioModel.setTime("06.00 AM && 06.00 PM");
        radioModel.setTitle("திருப்பலி (வாய்ப்புள்ள போது நேரடி ஒலிபரப்பு) ");
        radioModelList.add(radioModel);

        radioModel = new RadioModel();
        radioModel.setTime("06.45 AM && 06.45 PM");
        radioModel.setTitle("இன்றைய வாசகங்கள், இன்றைய புனிதர், சிந்திக்க சில நிமிடங்கள் ");
        radioModelList.add(radioModel);

        radioModel = new RadioModel();
        radioModel.setTime("07.30 AM && 07.30 PM");
        radioModel.setTitle("கத்தோலிக்க நற்சிந்தனை / ஆறுதலின் நேரம்");

        radioModel = new RadioModel();
        radioModel.setTime("08.30 AM && 08.00 PM");
        radioModel.setTitle("வத்திக்கான் வானொலி தமிழ் சேவை மறுஒலிபரப்பு");
        radioModelList.add(radioModel);

        radioModel = new RadioModel();
        radioModel.setTime("09.00 AM && 9.00 PM");
        radioModel.setTitle("செபமாலை");
        radioModelList.add(radioModel);

        radioModel = new RadioModel();
        radioModel.setTime("10.00 AM && 10.00 PM");
        radioModel.setTitle("திருப்பலி (if recorded during morning mass)");
        radioModelList.add(radioModel);


        return  radioModelList;
    }


    @Subscribe
    public void handleDatabaseEvent(DatabaseEvent event) {
        switch (event.operation) {
            case CREATE_STATION:
                List<Station> stations = new ArrayList<>(MyApplication.sDatabase.getStations());
                Collections.sort(stations);
                int positionToCreate = stations.indexOf(event.station);
                mAdapter.insertStation(event.station, positionToCreate);
                if (mAdapter.getSelection() == null) {
                    // the new station may already be playing
                    mAdapter.setSelection(MyApplication.sDatabase.selectedStation);
                }
                break;

            case DELETE_STATION:
                if (event.station.equals(mAdapter.getSelection())) {
                    mAdapter.clearSelection();
                }
                mAdapter.deleteStation(event.station);
                break;

            case UPDATE_STATION:
                mAdapter.updateStation(event.station);
                break;
        }

    }

    @Subscribe
    public void handlePlaybackEvent(PlaybackEvent event) {
        if (event == PlaybackEvent.STOP) {
            mAdapter.clearSelection();
        }
        mAdapter.updateStation(MyApplication.sDatabase.selectedStation);
    }

    @Subscribe
    public void handleBufferEvent(BufferEvent event) {
        mAdapter.updateStation(MyApplication.sDatabase.selectedStation);
    }

    @Subscribe
    public void handleSelectStationEvent(SelectStationEvent event) {
        mAdapter.setSelection(event.station);

    }


    @Override
    public void onClick(Station station) {
        if (!station.equals(MyApplication.sDatabase.selectedStation)) {
            // play
            MyApplication.sBus.post(new SelectStationEvent(station));
            MyApplication.sBus.post(PlaybackEvent.PLAY);
        }
    }

    @Override
    public boolean onLongClick(Station station) {
        return true;
    }

    @Override
    public void onFavoriteChanged(Station station, boolean favorite) {
        if (favorite) {
            MyApplication.sBus.post(new DatabaseEvent(DatabaseEvent.Operation.CREATE_STATION, station));
        } else {
            MyApplication.sBus.post(new DatabaseEvent(DatabaseEvent.Operation.DELETE_STATION, station));
        }
    }

    @Override
    public void onItemSelected(RadioModel radioModel) {

    }
}



