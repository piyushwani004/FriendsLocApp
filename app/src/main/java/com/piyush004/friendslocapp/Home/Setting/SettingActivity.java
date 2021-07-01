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

package com.piyush004.friendslocapp.Home.Setting;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.Auth.LoginActivity;
import com.piyush004.friendslocapp.Database.AuthSteps;
import com.piyush004.friendslocapp.Database.DatabaseHandler;
import com.piyush004.friendslocapp.Home.Feedback.FeedbackActivity;
import com.piyush004.friendslocapp.Home.Fragments.Map.Services.Constants;
import com.piyush004.friendslocapp.Home.Fragments.Map.Services.LocationService;
import com.piyush004.friendslocapp.Home.Profile.ProfileActivity;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = SettingActivity.class.getSimpleName();
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private CircleImageView circleImageView;
    private TextView textViewName;
    private ImageView logoutBtn, editProfileBtn, termConditionBtn, privacyBtn, FeedbackBtn;
    private AlertDialog.Builder alertDialogBuilder;
    private FirebaseAuth firebaseAuth;
    private SwitchMaterial switchMaterial;
    private DatabaseHandler db = new DatabaseHandler(this);
    public static final String PRIVACY_POLICY = "file:///android_asset/Privacy.html";
    public static final String TERMS_OF_SERVICE = "file:///android_asset/Terms.html";

    private String imgUrl;
    private String name, mobile;

    private DatabaseReference AppUser = FirebaseDatabase.getInstance().getReference().child("AppUsers")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        firebaseAuth = FirebaseAuth.getInstance();
        mobile = firebaseAuth.getCurrentUser().getPhoneNumber();

        circleImageView = findViewById(R.id.SettingProfileImg);
        textViewName = findViewById(R.id.SettingNameText);

        editProfileBtn = findViewById(R.id.EditProBtn);
        termConditionBtn = findViewById(R.id.termOfServiceBtn);
        privacyBtn = findViewById(R.id.PrivacyPolicyBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        FeedbackBtn = findViewById(R.id.FeedbackBtn);

        switchMaterial = findViewById(R.id.settingSwitch);

        AppUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                imgUrl = snapshot.child("ImageURL").getValue(String.class);
                name = snapshot.child("Name").getValue(String.class);

                if (name != null)
                    textViewName.setText(name);
                else
                    textViewName.setText(" ");

                if (imgUrl == null) {
                    Picasso.get()
                            .load(R.drawable.person_placeholder)
                            .into(circleImageView);
                } else {
                    Picasso.get()
                            .load(imgUrl)
                            .resize(500, 500)
                            .centerCrop().rotate(0)
                            .placeholder(R.drawable.person_placeholder)
                            .into(circleImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            db.updateShareLocation(String.valueOf(isChecked), mobile);
            if (isChecked) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SettingActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    startLocationService();
                }

            } else {
                stopLocationService();
            }

        });

        editProfileBtn.setOnClickListener(v -> {

            Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();

        });

        FeedbackBtn.setOnClickListener(v -> {

            Intent intent = new Intent(SettingActivity.this, FeedbackActivity.class);
            startActivity(intent);
            finish();

        });

        termConditionBtn.setOnClickListener(v -> show_dialog(TERMS_OF_SERVICE));

        privacyBtn.setOnClickListener(v -> show_dialog(PRIVACY_POLICY));

        logoutBtn.setOnClickListener(v -> {

            alertDialogBuilder = new AlertDialog.Builder(SettingActivity.this);
            alertDialogBuilder.setTitle("Logout...");
            alertDialogBuilder.setMessage("Do You Want To Logout ?");
            alertDialogBuilder.setPositiveButton("yes",
                    (arg0, arg1) -> {

                        db.updateStepOne("false", mobile);
                        db.updateStepTwo("false", mobile);
                        db.updateStepThree("false", mobile);

                        firebaseAuth.signOut();
                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    });

            alertDialogBuilder.setNegativeButton("No",
                    (dialog, which) -> {
                        dialog.cancel();
                        dialog.dismiss();
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        });


    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {

                if (LocationService.class.getName().equals(serviceInfo.service.getClassName())) {
                    if (serviceInfo.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }


    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Log.e(TAG, "startLocationService: Location Service Start...");
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
        } else {
            Log.e(TAG, "startLocationService: else");
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Log.e(TAG, "startLocationService: Location Service Stop...");
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
        } else {
            Log.e(TAG, "startLocationService: else");
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else
                Toast.makeText(this, "Permission denied!!!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            status("Online");
        }
        AuthSteps authSteps = db.getAllSteps(firebaseAuth.getCurrentUser().getPhoneNumber());
        switchMaterial.setChecked(Boolean.parseBoolean(authSteps.getShare_location()));
        Log.e(TAG, "onStart: " + authSteps.getShare_location());

    }

    public void show_dialog(String url) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        WebView webView = new WebView(this);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        builder.setView(webView);
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth.getCurrentUser() != null) {
            status("Offline");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (firebaseAuth.getCurrentUser() != null) {
            status("Offline");
        }
    }


    private void status(String s) {
        DatabaseReference status = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Status", s);
        status.updateChildren(hashMap);
    }

}