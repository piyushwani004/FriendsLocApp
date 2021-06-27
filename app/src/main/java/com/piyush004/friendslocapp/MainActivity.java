/*
 * <!--
 *   ~ /*******************************************************
 *   ~  * Copyright (C) 2021-2031 {Piyush Wani and  Mayur Sapkale} <{piyushwani04@gmail.com}>
 *   ~  *
 *   ~  * This file is part of {FriendLocatorApp}.
 *   ~  *
 *   ~  * {FriendLocatorApp} can not be copied and/or distributed without the express
 *   ~  * permission of {Piyush Wani and  Mayur Sapkale}
 *   ~  ******************************************************
 *   -->
 */

package com.piyush004.friendslocapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.piyush004.friendslocapp.Database.AuthSteps;
import com.piyush004.friendslocapp.Database.DatabaseHandler;
import com.piyush004.friendslocapp.Auth.LoginActivity;
import com.piyush004.friendslocapp.Home.HomeActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SPLASH_SCREEN_TIME_OUT = 2000;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(() -> {

            if (firebaseAuth.getCurrentUser() != null) {

                AuthSteps authSteps = db.getAllSteps(firebaseAuth.getCurrentUser().getPhoneNumber());
                Log.e(TAG, "onCreate: " + authSteps.toString());
                if (authSteps.getStep_one().equals("false") || authSteps.getStep_two().equals("false") || authSteps.getStep_three().equals("false")) {

                    firebaseAuth.signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                } else if (authSteps.getStep_one().equals("true") && authSteps.getStep_two().equals("true") && authSteps.getStep_three().equals("true")) {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                }

            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
            finish();

        }, SPLASH_SCREEN_TIME_OUT);

    }
}