package com.example.streamlinenavbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SprintAddTasks extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprint_add_tasks);

        // Retrieve the user ID from the intent
        userId = getIntent().getStringExtra("users");

        Button addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText enterTaskEditText = findViewById(R.id.EnterTask);
                String task = enterTaskEditText.getText().toString();

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Find the document in the "teams" collection based on the current user's ID
                db.collection("teams")
                        .whereArrayContains("users", userId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    Log.d("SprintAddTasks", "Query result: " + queryDocumentSnapshots.size() + " documents");

                                    // Get the document ID
                                    String teamCode = queryDocumentSnapshots.getDocuments().get(0).getId();
                                    Log.d("SprintAddTasks", "Team document ID: " + teamCode);

                                    // Update the "sprintTasks" field in the document
                                    db.collection("teams")
                                            .document(teamCode)
                                            .update("sprintTasks", FieldValue.arrayUnion(task))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("SprintAddTasks", "Task added successfully");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("SprintAddTasks", "Error adding task: " + e.getMessage());
                                                }
                                            });
                                } else {
                                    Log.d("SprintAddTasks", "Query result is empty");
                                }
                            }
                        });

                // Navigate back to the SprintsTemplate activity
                Intent intent = new Intent(SprintAddTasks.this, SprintsTemplate.class);
                intent.putExtra("sprint", getIntent().getStringExtra("sprint")); // Pass the sprint name back
                startActivity(intent);
                finish();

            }
        });


    }
}



