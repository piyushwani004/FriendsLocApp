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

package com.piyush004.friendslocapp.Home.Feedback;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.piyush004.friendslocapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class FeedbackActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RatingBar ratingbar;
    private TextInputEditText textInputEditText;
    private Button button;
    private SimpleDateFormat simpleDateFormat;
    private String feedback, userid, rating, date, mobile;
    private FirebaseAuth firebaseAuth;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        firebaseAuth = FirebaseAuth.getInstance();
        userid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        mobile = firebaseAuth.getCurrentUser().getPhoneNumber();
        Log.e("Project", " " + mobile);
        Date data = new Date();
        simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        date = simpleDateFormat.format(data);

        toolbar = findViewById(R.id.toolbarUserFeedback);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        ratingbar = findViewById(R.id.ratingBar);
        textInputEditText = findViewById(R.id.edit_text_feedback);
        button = findViewById(R.id.Buttonuserfeedback);

        final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Feedback");
        Log.e("Project ", "Feedback");
        button.setOnClickListener(v -> {
            feedback = textInputEditText.getText().toString().trim();
            if (feedback.isEmpty()) {
                textInputEditText.setError("Enter feedback...");
                textInputEditText.requestFocus();
            } else if (!feedback.isEmpty()) {

                HashMap<String, Object> hashMap = new HashMap<>();
                rating = String.valueOf(ratingbar.getRating());
                hashMap.put("id", userid);
                hashMap.put("feedback", feedback);
                hashMap.put("Rating", rating);
                hashMap.put("date", date);
                hashMap.put("mobile", mobile);

                String key = df.push().getKey();
                assert key != null;
                df.child(key).setValue(hashMap).addOnSuccessListener(aVoid -> {
                    showAlertDialog(R.layout.thank_you_dialoge);
                    ratingbar.setRating(0.0f);
                    textInputEditText.setText("");
                });
            }

        });


    }

    private void showAlertDialog(int layout) {
        dialogBuilder = new AlertDialog.Builder(FeedbackActivity.this);
        View layoutView = getLayoutInflater().inflate(layout, null);
        Button dialogButton = layoutView.findViewById(R.id.btnDialog);
        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        dialogButton.setOnClickListener(view -> {
            finish();
            alertDialog.dismiss();
        });
    }

}