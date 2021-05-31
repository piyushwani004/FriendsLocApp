package com.piyush004.friendslocapp.Home.Setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.Auth.LoginActivity;
import com.piyush004.friendslocapp.Home.Profile.ProfileActivity;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = SettingActivity.class.getSimpleName();
    private CircleImageView circleImageView;
    private TextView textViewName;
    private ImageView logoutBtn, editProfileBtn, termConditionBtn, privacyBtn;
    private AlertDialog.Builder alertDialogBuilder;
    private FirebaseAuth firebaseAuth;
    private SwitchMaterial switchMaterial;


    private String imgUrl;
    private String name;

    private DatabaseReference AppUser = FirebaseDatabase.getInstance().getReference().child("AppUsers")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        firebaseAuth = FirebaseAuth.getInstance();

        circleImageView = findViewById(R.id.SettingProfileImg);
        textViewName = findViewById(R.id.SettingNameText);

        editProfileBtn = findViewById(R.id.EditProBtn);
        termConditionBtn = findViewById(R.id.termOfServiceBtn);
        privacyBtn = findViewById(R.id.PrivacyPolicyBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

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

            SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("LocationStatus", String.valueOf(isChecked));
            myEdit.apply();

            Log.e(TAG, "setOnCheckedChangeListener : " + isChecked);

        });


        editProfileBtn.setOnClickListener(v -> {

            Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();

        });

        termConditionBtn.setOnClickListener(v -> {

        });

        privacyBtn.setOnClickListener(v -> {

        });

        logoutBtn.setOnClickListener(v -> {

            alertDialogBuilder = new AlertDialog.Builder(SettingActivity.this);
            alertDialogBuilder.setTitle("Logout...");
            alertDialogBuilder.setMessage("Do You Want To Logout ?");
            alertDialogBuilder.setPositiveButton("yes",
                    (arg0, arg1) -> {
                        firebaseAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sh = getSharedPreferences("Settings", MODE_PRIVATE);
        String status = sh.getString("LocationStatus", "");
        switchMaterial.setChecked(Boolean.parseBoolean(status));

        Log.e(TAG, "onStart: " + status);

    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("LocationStatus", String.valueOf(switchMaterial.isChecked()));
        myEdit.apply();

        Log.e(TAG, "onStop: " + switchMaterial.isChecked());

    }
}