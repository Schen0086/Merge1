package com.example.streamlinenavbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.streamlinenavbar.model.UserDetail;
import com.example.streamlinenavbar.util.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText emailId, firstName, lastName, age, password;
    Button btnSignUp;
    TextView tvSignIn;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailId = findViewById(R.id.editTextEmail);
        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        age = findViewById(R.id.editTextAge);
        password = findViewById(R.id.editTextPassword);
        btnSignUp = findViewById(R.id.signUpButton);
        tvSignIn = findViewById(R.id.textViewSignIn);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        btnSignUp.setOnClickListener(view -> {
            final String email = emailId.getText().toString();
            final String fName = firstName.getText().toString();
            final String lName = lastName.getText().toString();
            final String fAge = age.getText().toString();
            final String pwd = password.getText().toString();

            if (email.isEmpty()) {
                emailId.setError("Please provide email id");
                emailId.requestFocus();
            } else if (fName.isEmpty()) {
                firstName.setError("Please provide your first name");
                firstName.requestFocus();
            } else if (lName.isEmpty()) {
                lastName.setError("Please provide your last name");
                lastName.requestFocus();
            } else if (fAge.isEmpty()) {
                age.setError("Please provide your age");
                age.requestFocus();
            } else if (pwd.isEmpty()) {
                password.setError("Please provide password");
                password.requestFocus();
            } else if (!(email.isEmpty() && pwd.isEmpty())) {
                mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(Register.this, task -> {

                            if (!task.isSuccessful()) {
                                CustomToast.createToast(Register.this,
                                        "SignUp Unsuccessful, Please Try Again!"
                                                + task.getException().getMessage(), true);
                            } else {
                                UserDetail userDetail = new UserDetail(email, fName, lName, fAge);
                                String uid = task.getResult().getUser().getUid();
                                firebaseDatabase.getReference(uid).setValue(userDetail)
                                        .addOnSuccessListener(aVoid -> {
                                            Intent intent = new Intent(Register.this,
                                                    HomePage.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.putExtra("name", fName + " " + lName);
                                            intent.putExtra("email", email);
                                            intent.putExtra("age", fAge);
                                            startActivity(intent);
                                        });
                            }

                        });
            } else {
                CustomToast.createToast(Register.this, "Error Occurred !", true);
            }
        });

        tvSignIn.setOnClickListener(view -> {
            Intent i = new Intent(Register.this, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}