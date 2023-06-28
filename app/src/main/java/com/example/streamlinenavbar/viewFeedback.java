package com.example.streamlinenavbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamlinenavbar.data.Feedback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    @SuppressLint("MissingInflatedId")
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                onBackPressed();
                if (itemId == R.id.ic_arrow) {
                    onBackPressed();
                } else if (itemId == R.id.ic_team) {
                    Intent intent1 = new Intent(viewFeedback.this, team.class);
                    intent1.putExtra("name", getIntent().getStringExtra("name"));
                    intent1.putExtra("email", getIntent().getStringExtra("email"));
                    intent1.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(HomePage.class);
                    startActivity(intent1);
                    finish();
                } else if (itemId == R.id.ic_home) {
                    Intent intent2 = new Intent(viewFeedback.this, HomePage.class);
                    intent2.putExtra("name", getIntent().getStringExtra("name"));
                    intent2.putExtra("email", getIntent().getStringExtra("email"));
                    intent2.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(HomePage.class);
                    startActivity(intent2);
                    finish();
                } else if (itemId == R.id.ic_more) {
                    Intent intent3 = new Intent(viewFeedback.this, More.class);
                    intent3.putExtra("name", getIntent().getStringExtra("name"));
                    intent3.putExtra("email", getIntent().getStringExtra("email"));
                    intent3.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(More.class);
                    startActivity(intent3);
                    finish();
                } else if (itemId == R.id.ic_profile) {
                    Intent intent4 = new Intent(viewFeedback.this, ProfilePage.class);
                    intent4.putExtra("name", getIntent().getStringExtra("name"));
                    intent4.putExtra("email", getIntent().getStringExtra("email"));
                    intent4.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(ProfilePage.class);
                    startActivityForResult(intent4, 1);
                }

                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        String previousActivity = MyApp.getPreviousActivity();
        if (previousActivity != null) {
            try {
                Class<?> previousActivityClass = Class.forName(previousActivity);
                Intent intent = new Intent(viewFeedback.this, previousActivityClass);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("age", getIntent().getStringExtra("age"));
                startActivity(intent);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void navigateToActivity(Class<?> activityClass) {
        String previousActivity = getClass().getName();
        MyApp.setPreviousActivity(previousActivity);
        Intent intent = new Intent(viewFeedback.this, activityClass);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", getIntent().getStringExtra("email"));
        intent.putExtra("age", getIntent().getStringExtra("age"));
        startActivity(intent);
        finish();
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