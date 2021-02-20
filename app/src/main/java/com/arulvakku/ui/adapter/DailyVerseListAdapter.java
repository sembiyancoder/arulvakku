package com.arulvakku.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.ui.database.DBHelper;
import com.arulvakku.ui.database.PostsDatabaseHelper;
import com.arulvakku.ui.model.BookModel;
import com.arulvakku.ui.model.Bookmark;
import com.arulvakku.ui.model.DailyVerse;
import com.arulvakku.ui.utils.UtilSingleton;
import com.google.android.material.button.MaterialButton;

import java.util.List;


public class DailyVerseListAdapter extends RecyclerView.Adapter<DailyVerseListAdapter.VersionViewHolder> {

    private Context context;
    private List<DailyVerse> dailyVerseList;
    PostsDatabaseHelper postsDatabaseHelper;
    DBHelper dbHelper;
    private onItemSelectedListener onItemSelectedListener;

    public DailyVerseListAdapter(Context context, List<DailyVerse> dailyVerseList, onItemSelectedListener onItemSelectedListener) {
        this.context = context;
        this.dailyVerseList = dailyVerseList;
        postsDatabaseHelper = new PostsDatabaseHelper(context);
        this.onItemSelectedListener = onItemSelectedListener;
        dbHelper = DBHelper.getInstance(context);
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_daily_verse_row_item, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder holder, int position) {
        DailyVerse dailyVerse = dailyVerseList.get(position);

        holder.textDate.setText("" + UtilSingleton.getInstance().getFormattedDate(dailyVerse.getmDate()));
        holder.textVerseNo.setText("" + dailyVerse.getmVerseInfo());
        String verse = dbHelper.getVerseDay(dailyVerse.getnVerseFullNo());

        if (verse != null && !verse.isEmpty()) {
            holder.textVerse.setText(verse);
        }
    }

    @Override
    public int getItemCount() {
        return dailyVerseList.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textVerseNo, textVerse;
        MaterialButton btnShare;

        public VersionViewHolder(View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_date);
            textVerseNo = itemView.findViewById(R.id.text_verse_info);
            textVerse = itemView.findViewById(R.id.text_verse);
            btnShare = itemView.findViewById(R.id.button_share);

            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onShareVerse(dailyVerseList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface onItemSelectedListener {
        void onShareVerse(DailyVerse verse);
    }
}