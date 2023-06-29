package com.example.streamlinenavbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<TaskAdapter> taskAdapterArrayList;
    private FirebaseFirestore db;

    public RecyclerAdapter(Context context, ArrayList<TaskAdapter> taskAdapterArrayList) {
        this.context = context;
        this.taskAdapterArrayList = taskAdapterArrayList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_unassigned, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        TaskAdapter taskAdapter = taskAdapterArrayList.get(position);
        holder.sprintTasks.setText(taskAdapter.getSprintTasks());

        // Retrieve the user ID
        String userId = getCurrentUserId();

        // Set up delete button click listener
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the task ID from the adapter
                String taskId = taskAdapter.getTaskId();

                // Ensure userId is not null
                if (userId != null) {
                    // Delete the task from Firestore
                    deleteTaskFromFirestore(taskId, userId);
                } else {
                    // Handle the case when userId is null
                    Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return taskAdapterArrayList.size();
    }

    private void deleteTaskFromFirestore(String taskId, String userId) {
        db.collection("teams")
                .whereArrayContains("users", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String teamId = documentSnapshot.getId();

                        // Delete the task from Firestore
                        db.collection("teams")
                                .document(teamId)
                                .update("sprintTasks", FieldValue.arrayRemove(taskId))
                                .addOnSuccessListener(aVoid -> {
                                    // Task deleted successfully
                                    Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Error occurred while deleting the task
                                    Toast.makeText(context, "Failed to delete task", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Handle the case when the user's team document is not found
                        Toast.makeText(context, "User's team not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Error occurred while querying the teams collection
                    Toast.makeText(context, "Failed to query teams", Toast.LENGTH_SHORT).show();
                });
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sprintTasks;
        ImageButton btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sprintTasks = itemView.findViewById(R.id.task_unassigned);
            btnDelete = itemView.findViewById(R.id.delete_button);
        }
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            return userId;
        } else {
            // Handle the case when there is no authenticated user
            return null;
        }
    }
}
