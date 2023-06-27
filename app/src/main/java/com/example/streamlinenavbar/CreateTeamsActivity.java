package com.example.streamlinenavbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreateTeamsActivity extends AppCompatActivity {

    private EditText teamNameEditText;
    private TextView teamCodeTextView;
    private Button createTeamButton;

    private FirebaseFirestore db;
    private CollectionReference teamsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_teams);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        teamsCollection = db.collection("teams");

        // Initialize views
        teamNameEditText = findViewById(R.id.edit_team_name);
        teamCodeTextView = findViewById(R.id.text_generated_code);
        createTeamButton = findViewById(R.id.btn_create_team);
        Button generateCodeButton = findViewById(R.id.btn_generate_code);

        // Set click listener for Generate Code button
        generateCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateTeamCode(); // Generate and display a random team code
            }
        });

        // Set click listener for Create Team button
        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTeam();
            }
        });

        // Set click listener for Cancel button
        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity and return to the previous screen
            }
        });

    }

    private void generateTeamCode() {
        // Generate a random 6-digit team code
        Random random = new Random();
        int teamCode = random.nextInt(900000) + 100000;
        teamCodeTextView.setText(String.valueOf(teamCode));
    }

    private void createTeam() {
        String teamName = teamNameEditText.getText().toString().trim().toUpperCase(); // Convert team name to uppercase
        String teamCode = teamCodeTextView.getText().toString();

        if (teamName.isEmpty()) {
            Toast.makeText(this, "Please enter a team name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user is already in a team
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Query Firestore to check if the user is already in a team
            teamsCollection.whereArrayContains("users", userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // User is already in a team
                                Toast.makeText(CreateTeamsActivity.this, "You are already in a team", Toast.LENGTH_SHORT).show();
                            } else {
                                // User is not in a team, continue with creating the team

                                // Check if the team name already exists in Firestore
                                teamsCollection.whereEqualTo("name", teamName).get()
                                        .addOnCompleteListener(nameTask -> {
                                            if (nameTask.isSuccessful()) {
                                                if (!nameTask.getResult().isEmpty()) {
                                                    // Team name already exists
                                                    Toast.makeText(CreateTeamsActivity.this, "This team name already exists", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Team name is unique, check if the team code already exists
                                                    teamsCollection.document(teamCode).get()
                                                            .addOnCompleteListener(codeTask -> {
                                                                if (codeTask.isSuccessful()) {
                                                                    if (codeTask.getResult().exists()) {
                                                                        // Team code already exists, generate a new one
                                                                        generateTeamCode();
                                                                    } else {
                                                                        // Team code is unique, create the team
                                                                        Map<String, Object> teamData = new HashMap<>();
                                                                        teamData.put("name", teamName);

                                                                        teamData.put("users", Collections.singletonList(userId));

                                                                        teamsCollection.document(teamCode).set(teamData)
                                                                                .addOnSuccessListener(aVoid -> {
                                                                                    Toast.makeText(CreateTeamsActivity.this, "Team created successfully", Toast.LENGTH_SHORT).show();
                                                                                    clearForm();

                                                                                    // Navigate to TeamsTemplate activity
                                                                                    Intent intent = new Intent(CreateTeamsActivity.this, TeamsTemplate.class);
                                                                                    intent.putExtra("teamName", teamName); // Pass the team name
                                                                                    intent.putExtra("teamCode", teamCode); // Pass the team code
                                                                                    startActivity(intent);
                                                                                })
                                                                                .addOnFailureListener(e -> {
                                                                                    Toast.makeText(CreateTeamsActivity.this, "Failed to create team", Toast.LENGTH_SHORT).show();
                                                                                });
                                                                    }
                                                                } else {
                                                                    Toast.makeText(CreateTeamsActivity.this, "Failed to check team code", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            } else {
                                                Toast.makeText(CreateTeamsActivity.this, "Failed to check team name", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(CreateTeamsActivity.this, "Failed to check user's teams", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(CreateTeamsActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }


    private void clearForm() {
        teamNameEditText.getText().clear();
        generateTeamCode();
    }
}
