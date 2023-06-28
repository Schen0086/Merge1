package com.example.streamlinenavbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.streamlinenavbar.data.Feedback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class sendFeedback extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextSummary;
    private Button buttonSubmit;
    private Button buttonCancel;
    private CollectionReference feedbackRef;

    private static final int MAX_TITLE_CHARACTERS = 20;
    private static final int MAX_SUMMARY_CHARACTERS = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);

        editTextTitle = findViewById(R.id.editTextTextMultiLine);
        editTextSummary = findViewById(R.id.edit_text_feedback);
        buttonSubmit = findViewById(R.id.button3);
        buttonCancel = findViewById(R.id.button2);

        feedbackRef = FirebaseFirestore.getInstance().collection("feedbacks");

        setupCharacterLimits();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitFeedback();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToTeamPage();
            }
        });
    }

    private void setupCharacterLimits() {
        editTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > MAX_TITLE_CHARACTERS) {
                    editTextTitle.setText(charSequence.subSequence(0, MAX_TITLE_CHARACTERS));
                    editTextTitle.setSelection(MAX_TITLE_CHARACTERS);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editTextSummary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > MAX_SUMMARY_CHARACTERS) {
                    editTextSummary.setText(charSequence.subSequence(0, MAX_SUMMARY_CHARACTERS));
                    editTextSummary.setSelection(MAX_SUMMARY_CHARACTERS);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void submitFeedback() {
        String title = editTextTitle.getText().toString().trim();
        String summary = editTextSummary.getText().toString().trim();

        if (title.isEmpty() || summary.isEmpty()) {
            Toast.makeText(this, "Please enter title and summary", Toast.LENGTH_SHORT).show();
            return;
        }

        // Capitalize the first letter of the title
        title = capitalizeFirstLetter(title);

        // Capitalize the first letter of the summary
        summary = capitalizeFirstLetter(summary);

        // Generate a unique ID for the feedback
        String feedbackId = feedbackRef.document().getId();

        Feedback feedback = new Feedback(feedbackId, title, summary);

        feedbackRef.document(feedbackId).set(feedback)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(sendFeedback.this, "Feedback submitted", Toast.LENGTH_SHORT).show();
                            navigateToTeamPage();
                        } else {
                            Toast.makeText(sendFeedback.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String capitalizeFirstLetter(String text) {
        if (text.length() > 1) {
            return Character.toUpperCase(text.charAt(0)) + text.substring(1);
        } else if (text.length() == 1) {
            return Character.toUpperCase(text.charAt(0)) + "";
        } else {
            return text;
        }
    }

    private void navigateToTeamPage() {
        Intent intent = new Intent(sendFeedback.this, TeamsTemplate.class);
        startActivity(intent);
        finish();
    }
}

