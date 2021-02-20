package com.arulvakku.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.database.PostsDatabaseHelper;
import com.arulvakku.app.model.BookModel;

import java.util.ArrayList;
import java.util.List;


public class BooksListAdapter extends RecyclerView.Adapter<BooksListAdapter.VersionViewHolder> implements Filterable {

    private Context context;
    private List<BookModel> bookList;
    private List<BookModel> bookListFiltered;
    private onItemSelectedListener onItemSelectedListener;
    PostsDatabaseHelper postsDatabaseHelper;

    public BooksListAdapter(Context context, List<BookModel> bookList, onItemSelectedListener onItemSelectedListener) {
        this.context = context;
        this.bookList = bookList;
        this.bookListFiltered = bookList;
        this.onItemSelectedListener = onItemSelectedListener;
        postsDatabaseHelper = new PostsDatabaseHelper(context);
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chapter_row_item, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int position) {
        BookModel bookModel = bookListFiltered.get(position);
        int count = position + 1;
        versionViewHolder.mChapterName.setText(count + ". " + bookModel.getChapter_name().trim());
        versionViewHolder.mChapterCount.setText("" + bookModel.getChapter_count() + " அதி.");

        int notesCount = postsDatabaseHelper.getNotesCount(Integer.parseInt(bookModel.getChapter_id()));
        int bookmarkCount = postsDatabaseHelper.getBookmarkCount(Integer.parseInt(bookModel.getChapter_id()));

        versionViewHolder.materialButton.setText("" + notesCount + " Notes");
        versionViewHolder.bookmarkButton.setText("" + bookmarkCount + " Bookmarks");

        if (notesCount > 0) {
            versionViewHolder.materialButton.setVisibility(View.VISIBLE);
        } else {
            versionViewHolder.materialButton.setVisibility(View.GONE);
        }

        if (bookmarkCount > 0) {
            versionViewHolder.bookmarkButton.setVisibility(View.VISIBLE);
        } else {
            versionViewHolder.bookmarkButton.setVisibility(View.GONE);
        }
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
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    bookListFiltered = bookList;
                } else {
                    List<BookModel> filteredList = new ArrayList<>();
                    for (BookModel row : bookList) {
                        if (row.getChapter_name().toLowerCase().contains(charString.toLowerCase())) {
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
                bookListFiltered = (ArrayList<BookModel>) filterResults.values;
                if (bookListFiltered != null && bookListFiltered.size() == 0) {
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onNoResultFound(true);
                    }
                } else {
                    onItemSelectedListener.onNoResultFound(false);
                }
                notifyDataSetChanged();
            }
        };
    }


    class VersionViewHolder extends RecyclerView.ViewHolder {
        TextView mChapterName;
        TextView mChapterCount;
        TextView materialButton, bookmarkButton;


        public VersionViewHolder(View itemView) {
            super(itemView);
            mChapterName = itemView.findViewById(R.id.listitem_name);
            mChapterCount = itemView.findViewById(R.id.chapter_count);
            materialButton = itemView.findViewById(R.id.material_text_button);
            bookmarkButton = itemView.findViewById(R.id.bookmark_text_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onItemSelected(bookListFiltered.get(getAdapterPosition()));
                }
            });

            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onNotesSelected(bookListFiltered.get(getAdapterPosition()));
                }
            });

            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onBookmarkSelected(bookListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface onItemSelectedListener {
        void onNoResultFound(boolean notFound);
        void onItemSelected(BookModel bookModel);

        void onNotesSelected(BookModel bookModel);

        void onBookmarkSelected(BookModel bookModel);
    }
}