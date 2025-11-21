package com.example.unitconversion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<HistoryItem> historyList;
    private OnDeleteListener onDeleteListener;

    // Interface for delete callback
    public interface OnDeleteListener {
        void onDelete(int historyId);
    }

    // Constructor
    public HistoryAdapter(Context context, List<HistoryItem> historyList, OnDeleteListener onDeleteListener) {
        this.context = context;
        this.historyList = historyList;
        this.onDeleteListener = onDeleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);

        // Bind data to views (adjust IDs based on your item_history.xml layout)
        holder.tvType.setText(item.getType());
        holder.tvInput.setText(item.getInputValue() + " " + item.getInputUnit());
        holder.tvOutput.setText(item.getOutputValue() + " " + item.getOutputUnit());
        holder.tvDate.setText(item.getDate());

        // Set delete button click listener
        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteListener != null) {
                onDeleteListener.onDelete(item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    // Method to update the data list (called from History activity)
    public void updateData(List<HistoryItem> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvInput, tvOutput, tvDate;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views (adjust IDs based on your item_history.xml layout)
            tvType = itemView.findViewById(R.id.tvType);
            tvInput = itemView.findViewById(R.id.tvInput);
            tvOutput = itemView.findViewById(R.id.tvOutput);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}