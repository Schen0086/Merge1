package com.example.streamlinenavbar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TeamsTemplate extends AppCompatActivity {

    private TextView teamNameTextView;
    private TextView teamCodeTextView;
    private ListView usersListView;

    private FirebaseFirestore db;
    private CollectionReference teamsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_template);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        teamsCollection = db.collection("teams");

        // Initialize views
        teamNameTextView = findViewById(R.id.text_team_name);
        teamCodeTextView = findViewById(R.id.text_team_code);
        usersListView = findViewById(R.id.list_users);

        // Retrieve the team name and code from the intent extras
        String teamName = getIntent().getStringExtra("teamName");
        String teamCode = getIntent().getStringExtra("teamCode");

        // Set the team name and code in the TextViews
        teamNameTextView.setText(teamName);
        teamCodeTextView.setText(teamCode);

        // Retrieve the users in the team from Firestore
        teamsCollection.document(teamCode).collection("users").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> users = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getString("userId");
                            users.add(userId);
                        }
                        displayUsers(users);
                    }
                });
    }

    private void displayUsers(List<String> users) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
        usersListView.setAdapter(adapter);
    }
}
