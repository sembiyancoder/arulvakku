package com.arulvakku.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;

import java.util.List;


public class HomeMenuAdapter extends RecyclerView.Adapter<HomeMenuAdapter.MyViewHolder> {

    private List<String> menuListItem;
    private Context context;
    private onItemSelectedListener onItemSelectedListener;

    int[] myImageList = new int[]{
            R.drawable.ic_menu_bible,
            R.drawable.ic_menu_radio,
            R.drawable.ic_menu_rosary,
            R.drawable.ic_menu_wayofcross,
            R.drawable.ic_menu_request,
            R.drawable.ic_menu_calendar,
            R.drawable.ic_menu_feedback,
            R.drawable.ic_menu_donate,
            R.drawable.ic_menu_contact
    };

    public HomeMenuAdapter(Context context, List<String> menuListItem, onItemSelectedListener onItemSelectedListener) {
        this.menuListItem = menuListItem;
        this.context = context;
        this.onItemSelectedListener = onItemSelectedListener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.side_menu_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String name = menuListItem.get(position);
        holder.txtName.setText(name);
        holder.imgeIcon.setImageResource(myImageList[position]);
    }

    @Override
    public int getItemCount() {
        return menuListItem.size();
    }

    public interface onItemSelectedListener {
        void onItemSelected(String bookModel);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ImageView imgeIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name);
            imgeIcon = itemView.findViewById(R.id.appImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onItemSelected(menuListItem.get(getAdapterPosition()));
                }
            });
        }
    }
}
