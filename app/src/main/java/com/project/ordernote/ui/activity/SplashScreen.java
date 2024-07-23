package com.project.ordernote.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.project.ordernote.R;
import com.project.ordernote.utils.SessionManager;

public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 2000;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        Intent intent;
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                // Check if the user is logged in
                if (sessionManager.isLoggedIn()) {
                    // Redirect to DashboardScreen if the user is logged in
                    intent = new Intent(SplashScreen.this, Dashboard.class);
                } else {
                    // Redirect to LoginScreen if the user is not logged in
                    intent = new Intent(SplashScreen.this, LoginScreen.class);
                }

                SplashScreen.this.startActivity(intent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}