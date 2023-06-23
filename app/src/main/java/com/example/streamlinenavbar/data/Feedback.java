package com.example.streamlinenavbar.data;

public class Feedback {
    private String id;
    private String title;
    private String summary;

    public Feedback() {
        // Empty constructor required for Firestore
    }

    public Feedback(String id, String title, String summary) {
        this.id = id;
        this.title = title;
        this.summary = summary;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    // Add the getText() method to retrieve the feedback text
    public String getText() {
        return title + ": " + summary;
    }
}






