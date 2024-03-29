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
import com.arulvakku.ui.utils.UtilSingleton;
import com.google.android.material.button.MaterialButton;

import java.util.List;


public class BookmarkListAdapter extends RecyclerView.Adapter<BookmarkListAdapter.VersionViewHolder> {

    private Context context;
    private List<Bookmark> notesList;
    private onItemSelectedListener onItemSelectedListener;
    PostsDatabaseHelper postsDatabaseHelper;
    DBHelper dbHelper;

    public BookmarkListAdapter(Context context, List<Bookmark> notesList, onItemSelectedListener onItemSelectedListener) {
        this.context = context;
        this.notesList = notesList;
        this.onItemSelectedListener = onItemSelectedListener;
        postsDatabaseHelper = new PostsDatabaseHelper(context);
        dbHelper = DBHelper.getInstance(context);
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_bookmark_row_item, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int position) {
        Bookmark bookmark = notesList.get(position);
        versionViewHolder.txtTitle.setText("" + UtilSingleton.getInstance().getBookmarkAndVerse(bookmark.getVerse()));
        versionViewHolder.txtNotes.setText("" + dbHelper.getVerseDay(bookmark.getVerse()));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtNotes;
        MaterialButton btnDelete, btnShare;

        public VersionViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title_textview);
            txtNotes = itemView.findViewById(R.id.title_verse);
            btnDelete = itemView.findViewById(R.id.material_delete_button);
            btnShare = itemView.findViewById(R.id.material_share_button);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onDeleteItem(notesList.get(getAdapterPosition()));
                }
            });

            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onShareBookmark(notesList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface onItemSelectedListener {
        void onItemSelected(BookModel bookModel);
        void onDeleteItem(Bookmark bookModel);

        void onShareBookmark(Bookmark bookModel);
    }
}