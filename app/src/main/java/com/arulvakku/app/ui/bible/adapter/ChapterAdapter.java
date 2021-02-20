package com.arulvakku.app.ui.bible.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.MyApplication;


public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {


    private Context context;
    private int chapterCount = 0;
    private onItemSelectedListener onItemSelectedListener;

    public ChapterAdapter(Context context, int chapterCount, onItemSelectedListener onItemSelectedListener) {
        this.context = context;
        this.chapterCount = chapterCount;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_chapter_row_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        int position = pos + 1;
        holder.txtChapterNo.setText("" + position);

        if (MyApplication.mCurrentChapterNo == position) {
            holder.txtChapterNo.setTextColor(Color.WHITE);
            holder.txtChapterNo.setBackground(context.getResources().getDrawable(R.drawable.bg_oval_selected_chapter));
        } else {
            holder.txtChapterNo.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.txtChapterNo.setBackground(context.getResources().getDrawable(R.drawable.bg_oval_chapters));
        }
    }


    @Override
    public int getItemCount() {
        return chapterCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtChapterNo;

        public ViewHolder(View itemView) {
            super(itemView);
            txtChapterNo = itemView.findViewById(R.id.txt_chapter_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onItemSelected(getAdapterPosition() + 1);
                }
            });
        }
    }

    public interface onItemSelectedListener {
        void onItemSelected(int position);
    }


}
