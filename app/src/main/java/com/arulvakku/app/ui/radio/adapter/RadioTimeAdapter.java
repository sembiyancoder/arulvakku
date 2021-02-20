package com.arulvakku.app.ui.radio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.model.RadioModel;

import java.util.List;


public class RadioTimeAdapter extends RecyclerView.Adapter<RadioTimeAdapter.MyViewHolder> {

    private List<RadioModel> menuListItem;
    private Context context;
    private onItemSelectedListener onItemSelectedListener;

    public RadioTimeAdapter(Context context, List<RadioModel> menuListItem, onItemSelectedListener onItemSelectedListener) {
        this.menuListItem = menuListItem;
        this.context = context;
        this.onItemSelectedListener = onItemSelectedListener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_menu_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        RadioModel name = menuListItem.get(position);
        holder.txtTitle.setText(name.getTitle());
        holder.txtTime.setText(name.getTime());
    }

    @Override
    public int getItemCount() {
        return menuListItem.size();
    }

    public interface onItemSelectedListener {
        void onItemSelected(RadioModel radioModel);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle,txtTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtTime = itemView.findViewById(R.id.txt_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onItemSelected(menuListItem.get(getAdapterPosition()));
                }
            });

        }
    }
}
