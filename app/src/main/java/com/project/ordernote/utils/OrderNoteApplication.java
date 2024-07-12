package com.project.ordernote.utils;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class OrderNoteApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
    }
}
