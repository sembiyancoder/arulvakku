package com.arulvakku.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arulvakku.R;
import com.arulvakku.app.model.PushNotification;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PushNotificationAdapter extends RecyclerView.Adapter<PushNotificationAdapter.TasksViewHolder> {

    private Context mCtx;
    private List<PushNotification> taskList;
    private onItemListener onItemListener;

    public PushNotificationAdapter(Context mCtx, List<PushNotification> taskList, onItemListener onItemListener) {
        this.mCtx = mCtx;
        this.taskList = taskList;
        this.onItemListener = onItemListener;
    }

    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.layout_push_notification_row_item, parent, false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder holder, int position) {
        PushNotification notification = taskList.get(position);
        holder.textTitle.setText(notification.getTitle());
        holder.textMessage.setText(notification.getMessage());
        holder.textTimestamp.setText(notification.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textTitle, textMessage, textTimestamp;
        MaterialButton imgDelete;

        public TasksViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textMessage = itemView.findViewById(R.id.text_message);
            textTimestamp = itemView.findViewById(R.id.text_date);
            imgDelete = itemView.findViewById(R.id.button_delete);
            imgDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onDeleteItem(taskList.get(getAdapterPosition()));
        }
    }

    public interface onItemListener {
        void onDeleteItem(PushNotification pushNotification);
    }
}