package com.arulvakku.app.ui.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.model.Notification;
import com.arulvakku.app.utils.TimeAgo;

import java.util.List;


public class LocalNotificationListAdapter extends RecyclerView.Adapter<LocalNotificationListAdapter.VersionViewHolder> {

    private Context context;
    private List<Notification> mNotificationList;
    private onItemSelectedListener onItemSelectedListener;

    public LocalNotificationListAdapter(Context context, List<Notification> mNotificationList, onItemSelectedListener onItemSelectedListener) {
        this.context = context;
        this.mNotificationList = mNotificationList;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_notification_row_item, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder holder, int position) {
        Notification notification = mNotificationList.get(position);
        holder.mTitleTextView.setText(notification.getmTitle());
        holder.mMessageTextView.setText(notification.getmMessage());
        String timeAgo = TimeAgo.getTimeAgo(notification.getmTime());
        holder.mDateTimeTextView.setText(timeAgo);

    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTextView, mMessageTextView, mDateTimeTextView;
        ImageView mDeleteImageView;

        public VersionViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.txt_title);
            mMessageTextView = itemView.findViewById(R.id.txt_message);
            mDeleteImageView = itemView.findViewById(R.id.img_delete);
            mDateTimeTextView = itemView.findViewById(R.id.txt_date_time);
            mDeleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onDeleteMessage(mNotificationList.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }

    public interface onItemSelectedListener {
        void onDeleteMessage(Notification notification, int position);
    }
}