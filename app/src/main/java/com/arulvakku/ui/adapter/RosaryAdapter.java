package com.arulvakku.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;

import java.util.List;

public class RosaryAdapter extends RecyclerView.Adapter<RosaryAdapter.MyViewHolder> {

    private List<String> rosaryList;

    public RosaryAdapter(Context context,List<String> moviesList) {
        this.rosaryList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rosary_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        int row_item = position + 1;
        String name = rosaryList.get(position);
        holder.title.setText(row_item+". "+name);
    }

    @Override
    public int getItemCount() {
        return rosaryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txt_rosary_name);
        }
    }
}