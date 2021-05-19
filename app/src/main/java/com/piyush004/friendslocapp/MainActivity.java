package com.piyush004.friendslocapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.piyush004.friendslocapp.Auth.LoginActivity;
import com.piyush004.friendslocapp.Home.HomeActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN_TIME_OUT = 2000;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(() -> {

            if (firebaseAuth.getCurrentUser() != null) {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
            finish();


        }, SPLASH_SCREEN_TIME_OUT);

    }
}