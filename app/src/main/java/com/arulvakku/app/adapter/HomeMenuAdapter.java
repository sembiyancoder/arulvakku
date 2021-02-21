package com.arulvakku.app.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;


public class HomeMenuAdapter extends RecyclerView.Adapter<HomeMenuAdapter.MyViewHolder> {

    private Context mContext;
    private onItemSelectedListener onItemSelectedListener;
    private TypedArray mImageTypedArray, mNameTypedArray;

    public HomeMenuAdapter(Context context, TypedArray imageTypedArray, TypedArray nameTypedArray, onItemSelectedListener onItemSelectedListener) {
        mImageTypedArray = imageTypedArray;
        mNameTypedArray = nameTypedArray;
        mContext = context;
        this.onItemSelectedListener = onItemSelectedListener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.side_menu_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.mMenuNameTextView.setText(mNameTypedArray.getResourceId(position, 0));
        holder.mMenuImageView.setImageResource(mImageTypedArray.getResourceId(position, 0));
    }

    @Override
    public int getItemCount() {
        return mNameTypedArray.length();
    }

    public interface onItemSelectedListener {
        void onItemSelected(String bookModel);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mMenuNameTextView;
        ImageView mMenuImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mMenuNameTextView = itemView.findViewById(R.id.textView9);
            mMenuImageView = itemView.findViewById(R.id.imageView6);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onItemSelected(mNameTypedArray.getString(getAdapterPosition()));
                }
            });
        }
    }
}
