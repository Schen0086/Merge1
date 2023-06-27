package com.example.streamlinenavbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TeamsTemplate extends AppCompatActivity {

    private TextView teamNameTextView;
    private TextView teamCodeTextView;
    private ListView usersListView;

    private FirebaseFirestore db;
    private CollectionReference teamsCollection;

    private Button leaveTeamButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_template);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        db = FirebaseFirestore.getInstance();
        teamsCollection = db.collection("teams");

        teamNameTextView = findViewById(R.id.text_team_name);
        teamCodeTextView = findViewById(R.id.text_team_code);
        usersListView = findViewById(R.id.list_users);

        String teamName = getIntent().getStringExtra("teamName");
        String teamCode = getIntent().getStringExtra("teamCode");

        teamNameTextView.setText(teamName);
        teamCodeTextView.setText(teamCode);

        teamsCollection.document(teamCode).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot teamDocument = task.getResult();
                        if (teamDocument.exists()) {
                            List<String> users = new ArrayList<>();
                            List<String> userIds = (List<String>) teamDocument.get("users");
                            if (userIds != null) {
                                users.addAll(userIds);
                            }
                            displayUsers(users);
                        }
                    }
                });

        leaveTeamButton = findViewById(R.id.leaveTeambtn);
        leaveTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveTeam();
            }
        });

        Button btn = findViewById(R.id.sendFeedBackbtn);
        Button btn2 = findViewById(R.id.viewFeedBackbtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(TeamsTemplate.this, sendFeedback.class);
                intent1.putExtra("name", getIntent().getStringExtra("name"));
                intent1.putExtra("email", getIntent().getStringExtra("email"));
                intent1.putExtra("age", getIntent().getStringExtra("age"));
                startActivity(intent1);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(TeamsTemplate.this, viewFeedback.class);
                intent1.putExtra("name", getIntent().getStringExtra("name"));
                intent1.putExtra("email", getIntent().getStringExtra("email"));
                intent1.putExtra("age", getIntent().getStringExtra("age"));
                startActivity(intent1);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.ic_arrow) {
                    onBackPressed();
                } else if (itemId == R.id.ic_team) {
                    Intent intent1 = new Intent(TeamsTemplate.this, team.class);
                    intent1.putExtra("name", getIntent().getStringExtra("name"));
                    intent1.putExtra("email", getIntent().getStringExtra("email"));
                    intent1.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(team.class);
                    startActivity(intent1);
                    finish();
                } else if (itemId == R.id.ic_home) {
                    Intent intent2 = new Intent(TeamsTemplate.this, HomePage.class);
                    intent2.putExtra("name", getIntent().getStringExtra("name"));
                    intent2.putExtra("email", getIntent().getStringExtra("email"));
                    intent2.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(HomePage.class);
                    startActivity(intent2);
                    finish();
                } else if (itemId == R.id.ic_more) {
                    Intent intent3 = new Intent(TeamsTemplate.this, More.class);
                    intent3.putExtra("name", getIntent().getStringExtra("name"));
                    intent3.putExtra("email", getIntent().getStringExtra("email"));
                    intent3.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(More.class);
                    startActivity(intent3);
                    finish();
                } else if (itemId == R.id.ic_profile) {
                    Intent intent4 = new Intent(TeamsTemplate.this, ProfilePage.class);
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
                Intent intent = new Intent(TeamsTemplate.this, previousActivityClass);
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
        Intent intent = new Intent(TeamsTemplate.this, activityClass);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", getIntent().getStringExtra("email"));
        intent.putExtra("age", getIntent().getStringExtra("age"));
        startActivity(intent);
        finish();
    }

    private void displayUsers(List<String> users) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
        usersListView.setAdapter(adapter);
    }

    private void leaveTeam() {
        String teamCode = teamCodeTextView.getText().toString();
        String currentUserId = getCurrentUserId();

        teamsCollection.document(teamCode).update("users", FieldValue.arrayRemove(currentUserId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TeamsTemplate.this, "You left the team", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeamsTemplate.this, "Failed to leave the team", Toast.LENGTH_SHORT).show();
                    }
                });
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
