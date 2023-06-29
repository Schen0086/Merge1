package com.example.streamlinenavbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SprintsTemplate extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<TaskAdapter> taskAdapterArrayList;
    RecyclerAdapter recyclerAdapter;
    FirebaseFirestore db;

    private String sprintTasks;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprints_template);

        recyclerView = findViewById(R.id.unassigned_tasks_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        taskAdapterArrayList = new ArrayList<TaskAdapter>();
        recyclerAdapter = new RecyclerAdapter(SprintsTemplate.this, taskAdapterArrayList);

        recyclerView.setAdapter(recyclerAdapter);

        setupRecyclerView(); // Add this line to set up the ItemTouchHelper


        EventChangeListener();

        sprintTasks = getIntent().getStringExtra("sprintTasks");

        TextView sprintTextView = findViewById(R.id.sprint_name);

        // Retrieve the sprint name from the intent
        String sprint = getIntent().getStringExtra("sprint");

        // Retrieve the userId from the intent
        String userId = getCurrentUserId();

        // Set the sprint name to the TextView
        sprintTextView.setText(sprint);
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
                    Intent intent1 = new Intent(SprintsTemplate.this, team.class);
                    intent1.putExtra("name", getIntent().getStringExtra("name"));
                    intent1.putExtra("email", getIntent().getStringExtra("email"));
                    intent1.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(HomePage.class);
                    startActivity(intent1);
                    finish();
                } else if (itemId == R.id.ic_home) {
                    Intent intent2 = new Intent(SprintsTemplate.this, HomePage.class);
                    intent2.putExtra("name", getIntent().getStringExtra("name"));
                    intent2.putExtra("email", getIntent().getStringExtra("email"));
                    intent2.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(HomePage.class);
                    startActivity(intent2);
                    finish();
                } else if (itemId == R.id.ic_more) {
                    Intent intent3 = new Intent(SprintsTemplate.this, More.class);
                    intent3.putExtra("name", getIntent().getStringExtra("name"));
                    intent3.putExtra("email", getIntent().getStringExtra("email"));
                    intent3.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(More.class);
                    startActivity(intent3);
                    finish();
                } else if (itemId == R.id.ic_profile) {
                    Intent intent4 = new Intent(SprintsTemplate.this, ProfilePage.class);
                    intent4.putExtra("name", getIntent().getStringExtra("name"));
                    intent4.putExtra("email", getIntent().getStringExtra("email"));
                    intent4.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(ProfilePage.class);
                    startActivityForResult(intent4, 1);
                }

                return false;
            }
        });

        Button addTasksBtn = findViewById(R.id.add_tasks_btn);
        addTasksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SprintsTemplate.this, SprintAddTasks.class);
                intent.putExtra("users", userId); // Pass the current user's ID
                intent.putExtra("sprint", sprint); // Pass the sprint name
                startActivity(intent);
            }
        });

    }

    private void EventChangeListener() {
        String userId = getCurrentUserId();
        db.collection("teams")
                .whereArrayContains("users", userId) // Filter teams where the current user is a member
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }

                        taskAdapterArrayList.clear(); // Clear the list before adding new data

                        for (DocumentSnapshot document : value.getDocuments()) {
                            List<String> sprintTasksList = (List<String>) document.get("sprintTasks");
                            if (sprintTasksList != null) {
                                for (String sprintTask : sprintTasksList) {
                                    String taskId = document.getId(); // Get the document ID as the taskId
                                    TaskAdapter taskAdapter = new TaskAdapter(sprintTask, taskId);
                                    taskAdapterArrayList.add(taskAdapter);
                                }
                            }
                        }

                        recyclerAdapter.notifyDataSetChanged();
                    }
                });
    }



    @Override
    public void onBackPressed() {
        String previousActivity = MyApp.getPreviousActivity();
        if (previousActivity != null) {
            try {
                Class<?> previousActivityClass = Class.forName(previousActivity);
                Intent intent = new Intent(SprintsTemplate.this, previousActivityClass);
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
        Intent intent = new Intent(SprintsTemplate.this, activityClass);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", getIntent().getStringExtra("email"));
        intent.putExtra("age", getIntent().getStringExtra("age"));
        startActivity(intent);
        finish();
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

    private void setupRecyclerView() {
        // Create an instance of the TaskItemTouchHelperCallback
        TaskItemTouchHelperCallback callback = new TaskItemTouchHelperCallback(recyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        // Attach the touch helper to the RecyclerView
        touchHelper.attachToRecyclerView(recyclerView);
    }

}