package com.arulvakku.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.database.PostsDatabaseHelper;
import com.arulvakku.app.model.BookModel;
import com.arulvakku.app.model.Notes;
import com.google.android.material.button.MaterialButton;

import java.util.List;


public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.VersionViewHolder> {

    private Context context;
    private List<Notes> notesList;
    private onItemSelectedListener onItemSelectedListener;
    PostsDatabaseHelper postsDatabaseHelper;

    public NotesListAdapter(Context context, List<Notes> notesList, onItemSelectedListener onItemSelectedListener) {
        this.context = context;
        this.notesList = notesList;
        this.onItemSelectedListener = onItemSelectedListener;
        postsDatabaseHelper = new PostsDatabaseHelper(context);
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_notes_row_item, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int position) {
        Notes bookModel = notesList.get(position);
        versionViewHolder.txtTitle.setText(bookModel.getTitle());
        versionViewHolder.txtNotes.setText(bookModel.getNotes());
        versionViewHolder.txtSelectedVerse.setText(bookModel.getVerse());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtNotes, txtSelectedVerse;
        private MaterialButton btnDelete,btnShare;

        public VersionViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title_textview);
            txtNotes = itemView.findViewById(R.id.title_verse);
            txtSelectedVerse = itemView.findViewById(R.id.title_selected_verse);
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
        void onDeleteItem(Notes bookModel);
        void onShareBookmark(Notes bookModel);
    }
}