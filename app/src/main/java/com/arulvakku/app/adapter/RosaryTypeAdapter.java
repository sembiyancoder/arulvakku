package com.arulvakku.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arulvakku.R;

import java.util.List;

public class RosaryTypeAdapter  extends BaseAdapter {

    private Context context;
    private List<String> rosaryTypeList;
    private LayoutInflater inflter;

    public RosaryTypeAdapter(Context context, List<String> rosaryTypeList) {
        this.context = context;
        this.rosaryTypeList = rosaryTypeList;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return rosaryTypeList.size();
    }


    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.layout_rosary_row_item, null);
        String name = rosaryTypeList.get(position);
        final TextView names = view.findViewById(R.id.txt_rosary_name);
        final TextView days = view.findViewById(R.id.txt_rosary_days);
        names.setText(name);

        if(position==0){
            days.setText("திங்கள், சனி");
        }else if(position==1){
            days.setText("செவ்வாய், வெள்ளி");
        }else if(position==2){
            days.setText("புதன், ஞாயிறு");
        }else{
            days.setText("வியாழக் கிழமை");
        }
        return view;
    }
}

