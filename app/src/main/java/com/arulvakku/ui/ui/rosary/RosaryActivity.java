package com.arulvakku.ui.ui.rosary;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.ui.adapter.RosaryAdapter;
import com.arulvakku.ui.adapter.RosaryTypeAdapter;

import org.w3c.dom.ls.LSException;

import java.util.ArrayList;
import java.util.List;

public class RosaryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private List<String> rosaryTypeList = new ArrayList<>();
    private List<String> magzhichiList = new ArrayList<>();
    private List<String> thuyarList = new ArrayList<>();
    private List<String> magimaiList = new ArrayList<>();
    private List<String> oliList = new ArrayList<>();
    private RosaryTypeAdapter rosaryTypeAdapter;
    private RosaryAdapter rosaryAdapter;
    private Spinner rosaryTypeSpinner;
    private RecyclerView recyclerView;

    private int show_item = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rosary);
        rosaryTypeSpinner = findViewById(R.id.spinner_rosary_type);
        rosaryTypeSpinner.setOnItemSelectedListener(this);
        recyclerView = findViewById(R.id.recycler_view);

        Intent intent = this.getIntent();
        if(intent!=null && intent.hasExtra("day")){
            show_item = intent.getIntExtra("day",0);
        }

        prepareRosarySpinner();
    }


    private void prepareRosarySpinner() {
        rosaryTypeList = new ArrayList<>();
        rosaryTypeList.add("மகிழ்ச்சி நிறை மறை உண்மைகள்");
        rosaryTypeList.add("துயர் மறை உண்மைகள்");
        rosaryTypeList.add("மகிமை நிறை மறை உண்மைகள்");
        rosaryTypeList.add("ஒளி நிறை மறை உண்மைகள்");
        setUserRoleAdapter();
    }

    private List<String> getMagzhichiList(){
        magzhichiList.clear();
        magzhichiList.add("கபிரியேல் தூதர் கன்னிமரியாவுக்கு மங்கள வார்த்தை சொன்னதை தியானித்து.\nதாழ்ச்சி என்னும் வரத்தைக் கேட்டுச் செபிப்போமாக.");
        magzhichiList.add("கன்னி மரியாள் எலிசபெத்தம்மாளைச் சந்தித்ததைத் தியானித்து.\nபிறரன்பு என்னும் வரத்தைக் கேட்டு செபிப்போமாக.");
        magzhichiList.add("இயேசு பிறந்ததைத் தியானித்து.\nஎளிமை என்னும் வரத்தைக் கேட்டு செபிப்போமாக.");
        magzhichiList.add("இயேசு கோயிலில் காணிக்கையாக ஒப்புக் கொடுத்ததை தியானித்து.\nஇறைவனின் திருவுளத்துக்குப் பணிந்து நடக்கும் வரத்தைக் கேட்டு செபிப்போமாக.");
        magzhichiList.add("காணாமற் போன இயேசுவைக் கண்டடைந்ததை தியானித்து.\n\nநாம் அவரை எந்நாளும் தேடும் வரத்தைக் கேட்டு செபிப்போமாக.");
        return magzhichiList;
    }

    private List<String> getThuyarList(){
        thuyarList.clear();
        thuyarList.add("இயேசு இரத்த வியர்வை சிந்தியதைத் தியானித்து.\nநம் பாவங்களுக்காக மனத்துயர் அடைய செபிப்போமாக!");
        thuyarList.add("இயேசு கற்றூணில் கட்டுண்டு அடிப்பட்டதைத் தியானித்து.\nபுலன்களை அடக்கி வாழும் வரம் கேட்போமாக!");
        thuyarList.add("இயேசு முள்முடி தரித்ததைத் தியானித்து.\n நம்மையே ஒடுக்கவும், நிந்தை தோல்விகளை மகிழ்வுடன் ஏற்கவும் செபிப்போமாக!");
        thuyarList.add("இயேசு சிலுவை சுமந்து சென்றதைத் தியானித்து.\nவாழ்க்கைச் சுமையை பொறுமையோடு ஏற்று வாழச் செபிப்போமாக!");
        thuyarList.add("இயேசு சிலுவையில் அறையப்பட்டு இறந்ததைத் தியானித்து.\nஇயேசுவை அன்பு செய்யவும், பிறரை மன்னிக்கவும் வரம் கேட்போமாக!");
        return  thuyarList;
    }

    private List<String> getMagimaiList(){
        magimaiList.clear();
        magimaiList.add("இயேசு உயிர்த்தெழுந்ததைத் தியானித்து.\nஉயிருள்ள விசுவாசத்துடன் வாழ செபிப்போமாக!");
        magimaiList.add("இயேசுவின் விண்ணேற்றத்தைத் தியானித்து.\nநம்பிக்கையுடன் விண்ணக வாழ்வைத் தேடும் வரம் கேட்போமாக!");
        magimaiList.add("தூய ஆவியாரின் வருகையைத் தியானித்து.\nாம் அனைவரும் ஆவியாரின் ஒளியையும் அன்பையும் பெற செபிப்போமாக!");
        magimaiList.add("இறையன்னையின் விண்ணேற்பைத் தியானித்து.\nநாமும் விண்ணக மகிமையில் பங்குபெற செபிப்போமாக !");
        magimaiList.add("இறையன்னை விண்ணக மண்ணக அரசியாக மணிமுடி சூட்டப் பெற்றதைத் தியானித்து.\nநம் அன்னையின் மீது ஆழ்ந்த பக்தி கொள்ள செபிப்போமாக !" );
        return magimaiList;
    }

    private List<String> getOliList(){
        oliList.clear();
        oliList.add("இயேசு யோர்தான ஆற்றில் திருமுழுக்கு பெற்றதை தியானிப்போமாக !");
        oliList.add("கானாவூர் திருமணத்தில் இயேசு தண்ணீரை திராட்சை இரசமாக மாற்றியதை தியானிப்போமாக !");
        oliList.add("இயேசு விண்ணரசைப் பறைசாற்றியதை தியானிப்போமாக !");
        oliList.add("இயேசு தாபோர் மலையில் உருமாற்றம் பெற்றதை தியானிப்போமாக !");
        oliList.add("இயேசு கடைசி இரா விருந்துண்டதையும் நற்கருணை ஏற்படுத்தியதையும் தியானிப்போமாக !");
        return  oliList;
    }

    private void setUserRoleAdapter() {
        rosaryTypeAdapter = new RosaryTypeAdapter(this, rosaryTypeList);
        rosaryTypeSpinner.setAdapter(rosaryTypeAdapter);
    }

    private void setRosaryAdapter(List<String> list) {
        rosaryAdapter = new RosaryAdapter(this, list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(rosaryAdapter);
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        show_item = position;
        getIItem(show_item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getIItem(int position){
        if(position==0){
            setRosaryAdapter(getMagzhichiList());
        }else if(position==1){
            setRosaryAdapter(getThuyarList());
        }else if(position==2){
            setRosaryAdapter(getMagimaiList());
        }else if(position==3){
            setRosaryAdapter(getOliList());
        }
    }
}
