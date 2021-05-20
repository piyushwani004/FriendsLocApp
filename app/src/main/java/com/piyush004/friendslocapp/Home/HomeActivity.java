package com.piyush004.friendslocapp.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.piyush004.friendslocapp.Auth.LoginActivity;
import com.piyush004.friendslocapp.Home.Fragment.ChatFragment;
import com.piyush004.friendslocapp.Home.Fragment.ContactFragment;
import com.piyush004.friendslocapp.Home.Fragment.FriendFragment;
import com.piyush004.friendslocapp.Home.Fragment.MapFragment;
import com.piyush004.friendslocapp.Home.Fragment.RequestFragment;
import com.piyush004.friendslocapp.Home.Profile.ProfileActivity;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;
    private CircleImageView headerProfileUpdateImg;
    private ImageView SettingImg;
    private AlertDialog.Builder alertDialogBuilder;
    private FirebaseAuth firebaseAuth;
    private String imgUrl;

    private DatabaseReference appuser = FirebaseDatabase.getInstance().getReference().child("AppUsers")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseAuth = FirebaseAuth.getInstance();
        headerProfileUpdateImg = findViewById(R.id.HomeHeaderImg);
        SettingImg = findViewById(R.id.SettingImg);

        bottomNavigationView = findViewById(R.id.BotnavViewHome);
        bottomNavigationView.setBackground(null);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> {
            bottomNavigationView.getMenu().getItem(2).setChecked(true);
            pushFragment(new MapFragment());
        });

        appuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                imgUrl = snapshot.child("ImageURL").getValue(String.class);

                if (imgUrl == null) {
                    Picasso.get()
                            .load(R.drawable.person_placeholder)
                            .into(headerProfileUpdateImg);
                } else {
                    Picasso.get()
                            .load(imgUrl)
                            .resize(500, 500)
                            .centerCrop().rotate(0)
                            .placeholder(R.drawable.person_placeholder)
                            .into(headerProfileUpdateImg);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        headerProfileUpdateImg.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        SettingImg.setOnClickListener(v -> {
            /*Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/

            alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
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

        setUpNavView();
    }

    private void setUpNavView() {
        if (bottomNavigationView != null) {
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(2));
            menu.getItem(2).setChecked(false);
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                selectFragment(item);
                return false;
            });
        }
    }

    protected void selectFragment(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {

            case R.id.contact:
                pushFragment(new ContactFragment());
                break;

            case R.id.friendList:
                pushFragment(new FriendFragment());
                break;

            case R.id.request:
                pushFragment(new RequestFragment());
                break;

            case R.id.chat:
                pushFragment(new ChatFragment());
                break;

        }
    }

    protected void pushFragment(Fragment fragment) {

        if (fragment == null) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragmentTransaction != null) {
                fragmentTransaction.replace(R.id.fragment_container_home, fragment).commit();
            }
        }

    }


}