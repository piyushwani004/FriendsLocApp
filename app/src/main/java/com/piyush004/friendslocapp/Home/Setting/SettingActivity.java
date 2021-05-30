package com.piyush004.friendslocapp.Home.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.Auth.LoginActivity;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private TextView textViewName;
    private ImageView logoutBtn;
    private AlertDialog.Builder alertDialogBuilder;
    private FirebaseAuth firebaseAuth;


    private String imgUrl, name;

    private DatabaseReference AppUser = FirebaseDatabase.getInstance().getReference().child("AppUsers")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        firebaseAuth = FirebaseAuth.getInstance();

        circleImageView = findViewById(R.id.SettingProfileImg);
        textViewName = findViewById(R.id.SettingNameText);

        logoutBtn = findViewById(R.id.logoutBtn);

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
}