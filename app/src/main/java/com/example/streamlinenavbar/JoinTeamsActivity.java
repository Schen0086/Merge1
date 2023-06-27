package com.example.streamlinenavbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class JoinTeamsActivity extends AppCompatActivity {

    private EditText teamCodeEditText;
    private Button joinTeamButton;

    private FirebaseFirestore db;
    private CollectionReference teamsCollection;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_teams);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        teamsCollection = db.collection("teams");

        // Initialize views
        teamCodeEditText = findViewById(R.id.edit_team_code);
        joinTeamButton = findViewById(R.id.btn_join_team);

        // Set click listener for Join Team button
        joinTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinTeam();
            }
        });

        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity and return to the previous screen
            }
        });
    }

    private void joinTeam() {
        String teamCode = teamCodeEditText.getText().toString();

        if (teamCode.isEmpty()) {
            Toast.makeText(this, "Please enter a team code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the team with the given code exists in Firestore
        teamsCollection.document(teamCode).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            // Team exists, join the team
                            String teamName = task.getResult().getString("name");

                            // Get the team document reference
                            DocumentReference teamRef = teamsCollection.document(teamCode);

                            // Add the current user's ID to the team's "users" array field
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                teamRef.get().addOnCompleteListener(userTask -> {
                                    if (userTask.isSuccessful()) {
                                        // Retrieve the current users array
                                        List<String> users = (List<String>) userTask.getResult().get("users");

                                        if (users != null && users.contains(userId)) {
                                            // User is already in the team
                                            Intent intent = new Intent(JoinTeamsActivity.this, TeamsTemplate.class);
                                            intent.putExtra("teamName", teamName); // Pass the team name
                                            intent.putExtra("teamCode", teamCode); // Pass the team code
                                            startActivity(intent);
                                        } else {
                                            // User is not in the team, add the user ID to the "users" array
                                            teamRef.update("users", FieldValue.arrayUnion(userId))
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(JoinTeamsActivity.this, "Joined team successfully", Toast.LENGTH_SHORT).show();

                                                        // Navigate to TeamsTemplate activity
                                                        Intent intent = new Intent(JoinTeamsActivity.this, TeamsTemplate.class);
                                                        intent.putExtra("teamName", teamName); // Pass the team name
                                                        intent.putExtra("teamCode", teamCode); // Pass the team code
                                                        startActivity(intent);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(JoinTeamsActivity.this, "Failed to join team", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(JoinTeamsActivity.this, "Failed to retrieve team information", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(JoinTeamsActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Team does not exist
                            Toast.makeText(JoinTeamsActivity.this, "Invalid team code", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(JoinTeamsActivity.this, "Failed to check team code", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
