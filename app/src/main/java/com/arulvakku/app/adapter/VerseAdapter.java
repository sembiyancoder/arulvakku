package com.arulvakku.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.model.Bookmark;
import com.arulvakku.app.model.Verses;
import com.arulvakku.app.utils.UtilSingleton;

import java.util.ArrayList;
import java.util.List;


public class VerseAdapter extends RecyclerView.Adapter<VerseAdapter.ViewHolder> implements Filterable {


    public List<Verses> versesList = new ArrayList<>();
    public List<Verses> bookListFiltered = new ArrayList<>();
    public List<Verses> selectedVersusList = new ArrayList<>();

    private Context context;
    private VerseListener verseListener;
    private UtilSingleton utilSingleton;
    List<Bookmark> bookmarkList;
    private String charString;


    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;



    public VerseAdapter(Context mContext, List<Verses> versesList, List<Bookmark> bookmarkList, VerseListener verseListener) {
        this.context = mContext;
        this.versesList = versesList;
        this.bookListFiltered = versesList;
        this.bookmarkList = bookmarkList;
        this.selectedVersusList = new ArrayList<>();
        this.verseListener = verseListener;
        utilSingleton = UtilSingleton.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_verse_row_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        Verses verse = bookListFiltered.get(pos);

        // to set the அதிகாரம்
        if (verse.getVerse_id().equalsIgnoreCase("0")) {
            holder.txtChapterNo.setText("அதிகாரம் " + verse.getChapter_id());
            holder.txtChapterNo.setVisibility(View.VISIBLE);
        } else {
            if(pos!=0){
                Verses temp = bookListFiltered.get(pos - 1);
                if(temp!=null && !temp.getVerse_id().equals("0") && verse.getVerse_id().equals("1")){
                    holder.txtChapterNo.setText("அதிகாரம் " + verse.getChapter_id());
                    holder.txtChapterNo.setVisibility(View.VISIBLE);
                }else{
                    holder.txtChapterNo.setVisibility(View.GONE);
                }
            }
        }

        // on select high light
        if (selectedVersusList.contains(bookListFiltered.get(pos)))
            holder.mParentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.list_item_selected_state));
        else
            holder.mParentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.list_item_normal_state));


        // setting the header and verse
        if (verse.getVerse_type().equalsIgnoreCase("T")) {
            holder.txtHeaderOne.setText(verse.getVerse());
            holder.txtVerse.setVisibility(View.GONE);
            holder.txtVerseNo.setVisibility(View.GONE);
            holder.txtHeaderOne.setVisibility(View.VISIBLE);
        } else {
            holder.txtVerse.setText(verse.getVerse());
            holder.txtVerseNo.setText(verse.getVerse_id().toString() +".");
            holder.txtHeaderOne.setVisibility(View.GONE);
            holder.txtVerse.setVisibility(View.VISIBLE);
            holder.txtVerseNo.setVisibility(View.VISIBLE);

            if(charString!=null && charString.length()>0){
                String ett = charString;
                String tvt = holder.txtVerse.getText().toString();

                int ofe = tvt.indexOf(ett, 0);
                Spannable WordtoSpan = new SpannableString(verse.getVerse());
                for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
                    ofe = tvt.indexOf(ett, ofs);
                    if (ofe == -1)
                        break;
                    else {
                        WordtoSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), ofe, ofe + ett.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        holder.txtVerse.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
                    }

                }
            }
        }


        //highlight on select
        if (utilSingleton.isSelected(verse.getFull_id())) {
            holder.txtVerse.setTextColor(Color.RED);
        } else {
            holder.txtVerse.setTextColor(Color.BLACK);
        }

        //bookmark selected
        if (UtilSingleton.getInstance().isBookMarked(bookmarkList, verse.getFull_id())) {
            holder.bookmarkImageView.setVisibility(View.VISIBLE);
        } else {
            holder.bookmarkImageView.setVisibility(View.GONE);
        }


        verseListener.updateToolbarTitle(verse.getChapter_id());


    }


    @Override
    public int getItemCount() {
        return bookListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                charString = charSequence.toString();
                if (charString.isEmpty()) {
                    bookListFiltered = versesList;
                } else {
                    List<Verses> filteredList = new ArrayList<>();
                    for (Verses row : versesList) {
                        if (row.getVerse().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    bookListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = bookListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                bookListFiltered = (ArrayList<Verses>) filterResults.values;
                if (bookListFiltered != null && bookListFiltered.size() == 0) {
                    if (verseListener != null) {
                        verseListener.onNoResultFound(true);
                    }
                } else {
                    verseListener.onNoResultFound(false);
                }
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtVerse, txtVerseNo, txtHeaderOne, txtChapterNo;
        LinearLayout headerLayout;
        LinearLayout verseRowItem;
        LinearLayout mParentLayout;
        ImageView bookmarkImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            txtVerse = itemView.findViewById(R.id.verse_item);
            txtChapterNo = itemView.findViewById(R.id.txt_chapter_number);
            txtVerseNo = itemView.findViewById(R.id.verse_no);
            headerLayout = itemView.findViewById(R.id.header_layout);
            txtHeaderOne = itemView.findViewById(R.id.first_header);
            verseRowItem = itemView.findViewById(R.id.verse_list_item);
            mParentLayout = itemView.findViewById(R.id.parent_layout);
            bookmarkImageView = itemView.findViewById(R.id.image_bookmark);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verseListener.onSelectedVerse(getAdapterPosition(), versesList.get(getAdapterPosition()));
                }
            });
        }
    }






    public interface VerseListener {
        void updateToolbarTitle(String verse_no);
        void onSelectedVerse(int pos, Verses verses);
        void onNoResultFound(boolean notFound);
    }

}
