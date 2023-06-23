package com.example.streamlinenavbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamlinenavbar.data.Feedback;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<Feedback> feedbackList;
    private OnDeleteClickListener deleteClickListener;

    public FeedbackAdapter(List<Feedback> feedbackList, OnDeleteClickListener deleteClickListener) {
        this.feedbackList = feedbackList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);
        holder.titleTextView.setText(feedback.getTitle());
        holder.summaryTextView.setText(feedback.getSummary());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(feedback.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String feedbackId);
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView summaryTextView;
        Button deleteButton;

        FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}


