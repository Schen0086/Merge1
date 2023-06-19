package com.example.streamlinenavbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.streamlinenavbar.model.UserDetail;
import com.example.streamlinenavbar.util.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailId = findViewById(R.id.editTextEmailSignIn);
        password = findViewById(R.id.editTextPasswordSignIn);
        btnSignIn = findViewById(R.id.signInButton);
        tvSignUp = findViewById(R.id.textViewSignUp);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();


        mAuthStateListener = firebaseAuth -> {
            FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
            if (mFirebaseUser != null) {
                moveToHomeActivity(mFirebaseUser);
            } else {
                CustomToast.createToast(Login.this, "Please Login",
                        false);
            }
        };

        btnSignIn.setOnClickListener(view -> {
            String email = emailId.getText().toString();
            String pwd = password.getText().toString();

            if (email.isEmpty()) {
                emailId.setError("Please provide an email");
                emailId.requestFocus();
            } else if (pwd.isEmpty()) {
                password.setError("Please provide a password");
                password.requestFocus();
            } else if (email.isEmpty() && pwd.isEmpty()) {
                Toast.makeText(Login.this, "Fields are empty"
                        , Toast.LENGTH_LONG).show();
            } else if (!(email.isEmpty() && pwd.isEmpty())) {
                firebaseAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(Login.this,
                                task -> {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Login Error. Please Try Again",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        moveToHomeActivity(task.getResult().getUser());
                                    }
                                });
            } else {
                Toast.makeText(Login.this, "Error Occurred !", Toast.LENGTH_LONG).show();
            }

        });

        tvSignUp.setOnClickListener(view -> {
            Intent intSignUp = new Intent(Login.this, com.example.streamlinenavbar.Register.class);
            intSignUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intSignUp);

        });


    }

    @Override
    protected void onStart() {
        super.onStart();
//        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void moveToHomeActivity(FirebaseUser mFirebaseUser) {

        firebaseDatabase.getReference().child(mFirebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDetail userDetail = snapshot.getValue(UserDetail.class);
                        String email = userDetail.getEmailId();
                        String age = userDetail.getAge();
                        String name = userDetail.getFirstName() + " " + userDetail.getLastName();
                        Intent i = new Intent(getApplicationContext(), HomePage.class);
                        CustomToast.createToast(getApplicationContext(), "Login Successful",
                                false);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("name", name);
                        i.putExtra("email", email);
                        i.putExtra("age", age);
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });

    }
}