package com.example.streamlinenavbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfilePage extends AppCompatActivity {

    EditText mEditText;
    Button btnLogout;
    TextView userTextView, emailTextView, ageTextView;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        mEditText = findViewById(R.id.edit_about_me);

        btnLogout = findViewById(R.id.logout);
        userTextView = findViewById(R.id.loggedInName);
        emailTextView = findViewById(R.id.loggedInEmail);
        ageTextView = findViewById(R.id.loggedInAge);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            userRef = database.child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String firstName = dataSnapshot.child("firstName").getValue(String.class);
                        String lastName = dataSnapshot.child("lastName").getValue(String.class);
                        String email = dataSnapshot.child("emailId").getValue(String.class);
                        String age = dataSnapshot.child("age").getValue(String.class);

                        userTextView.setText("Name: " + firstName + " " + lastName);
                        emailTextView.setText("Email: " + email);
                        ageTextView.setText("Age: " + age);
                    } else {
                        Toast.makeText(ProfilePage.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProfilePage.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.ic_arrow) {
                    onBackPressed();
                } else if (itemId == R.id.ic_team) {
                    Intent intent1 = new Intent(ProfilePage.this, team.class);
                    navigateToActivity(team.class);
                    startActivity(intent1);
                    finish();
                } else if (itemId == R.id.ic_home) {
                    Intent intent2 = new Intent(ProfilePage.this, HomePage.class);
                    navigateToActivity(HomePage.class);
                    startActivity(intent2);
                    finish();
                } else if (itemId == R.id.ic_more) {
                    Intent intent3 = new Intent(ProfilePage.this, More.class);
                    navigateToActivity(More.class);
                    startActivity(intent3);
                    finish();
                } else if (itemId == R.id.ic_profile) {
                    Intent intent4 = new Intent(ProfilePage.this, ProfilePage.class);
                    navigateToActivity(ProfilePage.class);
                    startActivity(intent4);
                    finish();
                }

                return false;
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(ProfilePage.this, Register.class));
                finish();
            }
        });

// Call the method to create the "users" collection if it doesn't exist
        createUsersCollectionIfNotExist();
    }
    private void createUsersCollectionIfNotExist() {
        db.collection("users").document("dummy").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            // "users" collection doesn't exist, create it
                            Map<String, Object> dummyData = new HashMap<>();
                            dummyData.put("dummyField", "dummyValue");
                            db.collection("users").document("dummy").set(dummyData)
                                    .addOnSuccessListener(aVoid -> {
                                        // Collection created successfully
                                        db.collection("users").document("dummy").delete()
                                                .addOnSuccessListener(aVoid1 -> {
                                                    // Dummy document deleted successfully
                                                    Toast.makeText(ProfilePage.this, "Users collection created successfully", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Failed to delete dummy document
                                                    Toast.makeText(ProfilePage.this, "Failed to delete dummy document", Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        // Failed to create the collection
                                        Toast.makeText(ProfilePage.this, "Failed to create 'users' collection", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Failed to retrieve document, show error message
                        Toast.makeText(ProfilePage.this, "Failed to retrieve 'users' collection", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void save(View view) {
        String aboutMe = mEditText.getText().toString();

        // Get the current user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Check if the user is authenticated
        if (currentUser != null) {
            // Get the user's unique ID
            String userId = currentUser.getUid();

            // Create a reference to the document for the current user in the "users" collection
            DocumentReference userRef = db.collection("users").document(userId);

            // Create a map to store the "aboutMe" field value
            Map<String, Object> data = new HashMap<>();
            data.put("aboutMe", aboutMe);

            // Set the data for the current user
            userRef.set(data)
                    .addOnSuccessListener(aVoid -> {
                        // Clear the EditText after saving
                        mEditText.getText().clear();
                        Toast.makeText(ProfilePage.this, "About Me saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfilePage.this, "Failed to save About Me", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ProfilePage.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }


    public void load(View view) {
        // Get the current user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Check if the user is authenticated
        if (currentUser != null) {
            // Get the user's unique ID
            String userId = currentUser.getUid();

            // Create a reference to the document for the current user in the "users" collection
            DocumentReference userRef = db.collection("users").document(userId);

            // Retrieve the "aboutMe" field for the current user
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Get the "aboutMe" field value
                            String aboutMe = documentSnapshot.getString("aboutMe");

                            // Set the "aboutMe" value in the EditText
                            mEditText.setText(aboutMe);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfilePage.this, "Failed to load About Me", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ProfilePage.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        String previousActivity = MyApp.getPreviousActivity();
        if (previousActivity != null) {
            try {
                Class<?> previousActivityClass = Class.forName(previousActivity);
                Intent intent = new Intent(ProfilePage.this, previousActivityClass);
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
    private void navigateToActivity(Class destination) {
        Intent intent = new Intent(this, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

