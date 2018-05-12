package com.example.android.todolist;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.todolist.database.TaskEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private static final String DATE_FORMAT = "dd/MM/yyy";
    private final ItemClickListener mItemClickListener;
    private List<TaskEntry> mTaskEntries;
    private Context mContext;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public TaskAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskEntry taskEntry = mTaskEntries.get(position);
        String description = taskEntry.getDescription();
        int priority = taskEntry.getPriority();
        String updatedAt = mDateFormat.format(taskEntry.getUpdatedAt());

        holder.taskDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);
        holder.priorityView.setText("" + priority);

        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
        priorityCircle.setColor(getPriorityColor(priority));
    }

    @Override
    public int getItemCount() {
        if (mTaskEntries == null) {
            return 0;
        }

        return mTaskEntries.size();
    }

    public interface ItemClickListener { void onItemClickListener(int itemId); }

    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch (priority) {
            case 1:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default:
                break;
        }
        return priorityColor;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView taskDescriptionView;
        TextView updatedAtView;
        TextView priorityView;

        TaskViewHolder(View itemView) {
            super(itemView);

            taskDescriptionView = itemView.findViewById(R.id.taskDescription);
            updatedAtView = itemView.findViewById(R.id.taskUpdatedAt);
            priorityView = itemView.findViewById(R.id.priorityTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int elementId = mTaskEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
