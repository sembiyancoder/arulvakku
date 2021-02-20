package com.arulvakku.app.adapter;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.model.SearchWord;
import com.arulvakku.app.utils.UtilSingleton;

import java.util.List;


public class SearchResultRecyclerAdapter extends RecyclerView.Adapter<SearchResultRecyclerAdapter.VersionViewHolder> {
    List<SearchWord> versionModels;
    OnItemClickListener clickListener;
    String value;

    public SearchResultRecyclerAdapter(List<SearchWord> versionModels, String value) {
        this.versionModels = versionModels;
        this.value = value;

    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_search_item, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
        String ett = value;
        String tvt = versionModels.get(i).getVerses().toString();
        int ofe = tvt.indexOf(ett, 0);
        Spannable WordtoSpan = new SpannableString(versionModels.get(i).getVerses().toString());

        versionViewHolder.title.setText(versionModels.get(i).getVerses());
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(ett, ofs);
            if (ofe == -1)
                break;
            else {
                WordtoSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), ofe, ofe + ett.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                versionViewHolder.title.setText(WordtoSpan, TextView.BufferType.SPANNABLE);

            }

        }

        String book = versionModels.get(i).getId().substring(0, 2);
        String chapter = versionModels.get(i).getId().substring(2, 5);
        String verse = versionModels.get(i).getId().substring(5, 8);
        versionViewHolder.referenceID.setText(UtilSingleton.getInstance().bookName[Integer.parseInt(book) - 1] + " " + +Integer.parseInt(chapter) + ":" + Integer.parseInt(verse));
    }

    @Override
    public int getItemCount() {
        return versionModels.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView referenceID;

        public VersionViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listitem_name);
            referenceID = (TextView) itemView.findViewById(R.id.referenceID);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getPosition());
        }
    }

}
