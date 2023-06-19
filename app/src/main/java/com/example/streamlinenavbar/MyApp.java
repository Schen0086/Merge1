package com.example.streamlinenavbar;

import android.app.Application;

public class MyApp extends Application {
    private static String previousActivity;

    public static String getPreviousActivity() {
        return previousActivity;
    }

    public static void setPreviousActivity(String activityName) {
        previousActivity = activityName;
    }
}
