package com.example.streamlinenavbar;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamlinenavbar.data.Feedback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class viewFeedback extends AppCompatActivity implements FeedbackAdapter.OnDeleteClickListener {

    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private List<Feedback> feedbackList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedback);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(feedbackList, this);
        recyclerView.setAdapter(adapter);

        fetchFeedbackData();
    }

    private void fetchFeedbackData() {
        firestore.collection("feedbacks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            feedbackList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String feedbackId = document.getId();
                                String feedbackTitle = document.getString("title");
                                String feedbackSummary = document.getString("summary");
                                Feedback feedback = new Feedback(feedbackId, feedbackTitle, feedbackSummary);
                                feedbackList.add(feedback);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            // Failed to fetch feedback data
                            // Handle the error
                        }
                    }
                });
    }

    @Override
    public void onDeleteClick(String feedbackId) {
        firestore.collection("feedbacks").document(feedbackId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(viewFeedback.this, "Feedback deleted", Toast.LENGTH_SHORT).show();
                            fetchFeedbackData(); // Refresh the feedback list after deletion
                        } else {
                            Toast.makeText(viewFeedback.this, "Failed to delete feedback", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}