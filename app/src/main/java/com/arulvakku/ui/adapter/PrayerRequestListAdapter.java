package com.arulvakku.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;


public class PrayerRequestListAdapter extends RecyclerView.Adapter<PrayerRequestListAdapter.VersionViewHolder> {

    private Context context;
    private JSONArray jsonArray;
    private onItemSelectedListener onItemSelectedListener;

    public PrayerRequestListAdapter(Context context, JSONArray jsonArray, onItemSelectedListener onItemSelectedListener) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_prayer_request_row_item, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder holder, int position) {
        JSONObject jsonObject = jsonArray.optJSONObject(position);
        holder.txtPrayer.setText(jsonObject.optString("PrayerRequest"));
        holder.txtReply.setText(jsonObject.optString("Reply"));

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        TextView txtPrayer, txtReply;
        MaterialButton btnEdit,btnDelete;

        public VersionViewHolder(View itemView) {
            super(itemView);
            txtPrayer = itemView.findViewById(R.id.txt_prayer);
            txtReply = itemView.findViewById(R.id.txt_reply);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onEditPrayer(jsonArray.optJSONObject(getAdapterPosition()));
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onDeletePrayer(jsonArray.optJSONObject(getAdapterPosition()));
                }
            });
        }
    }

    public interface onItemSelectedListener {
        void onDeletePrayer(JSONObject bookModel);
        void onEditPrayer(JSONObject bookModel);
    }
}