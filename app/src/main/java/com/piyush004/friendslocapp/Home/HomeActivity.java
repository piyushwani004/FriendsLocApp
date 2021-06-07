package com.piyush004.friendslocapp.Home;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.piyush004.friendslocapp.Home.Fragments.Chat.ChatFragment;
import com.piyush004.friendslocapp.Home.Fragments.Contact.ContactFragment;
import com.piyush004.friendslocapp.Home.Fragments.FriendList.FriendFragment;
import com.piyush004.friendslocapp.Home.Fragments.Map.MapsFragment;
import com.piyush004.friendslocapp.Home.Fragments.Request.RequestFragment;
import com.piyush004.friendslocapp.Home.Profile.ProfileActivity;
import com.piyush004.friendslocapp.Home.Setting.SettingActivity;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;
    private CircleImageView headerProfileUpdateImg;
    private TextView AdminHomeToolbarHeader;
    private ImageView SettingImg;
    private String imgUrl, name;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference appuser = FirebaseDatabase.getInstance().getReference().child("AppUsers")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        requestStoragePermission();
        headerProfileUpdateImg = findViewById(R.id.HomeHeaderImg);
        SettingImg = findViewById(R.id.SettingImg);
        AdminHomeToolbarHeader = findViewById(R.id.AdminHomeToolbarHeader);

        bottomNavigationView = findViewById(R.id.BotnavViewHome);
        bottomNavigationView.setBackground(null);
        pushFragment(new MapsFragment());
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> {
            bottomNavigationView.getMenu().getItem(2).setChecked(true);
            pushFragment(new MapsFragment());
        });

        appuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                imgUrl = snapshot.child("ImageURL").getValue(String.class);
                name = snapshot.child("Name").getValue(String.class);

                if (name != null)
                    AdminHomeToolbarHeader.setText(name);
                else
                    AdminHomeToolbarHeader.setText(" ");

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

            Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });

        setUpNavView();

    }

    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // showSettingsDialog();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
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
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_container_home, fragment).commit();
            }
        }

    }
}