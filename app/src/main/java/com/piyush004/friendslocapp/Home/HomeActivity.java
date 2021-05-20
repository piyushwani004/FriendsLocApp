package com.piyush004.friendslocapp.Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.PhoneNumber;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.piyush004.friendslocapp.Auth.LoginActivity;
import com.piyush004.friendslocapp.Home.Profile.ProfileActivity;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
        Contacts.initialize(this);
        setContentView(R.layout.activity_home);
        firebaseAuth = FirebaseAuth.getInstance();
        headerProfileUpdateImg = findViewById(R.id.HomeHeaderImg);
        SettingImg = findViewById(R.id.SettingImg);

        bottomNavigationView = findViewById(R.id.BotnavViewHome);
        bottomNavigationView.setBackground(null);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v -> {
            bottomNavigationView.getMenu().getItem(2).setChecked(true);
            //pushFragment(new Home());
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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> matchNumbers=new ArrayList<>();

                List<String>  phoneContact= getAllPhoneContact();

                DatabaseReference user = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userRef = user.child("AppUsers");

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String mobile = ds.child("Mobile").getValue(String.class).trim().replaceAll(" ","");

                            if(phoneContact.contains(mobile) ){
                                matchNumbers.add(mobile);
                            }



                        }

                        for(int i=0;i<matchNumbers.size();i++){
                            Log.e("Mached Numbers",matchNumbers.get(i));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }




    private List<String> getAllPhoneContact() {
        List<String> userContact=new ArrayList<>();

        List<Contact> contacts = Contacts.getQuery().find();
        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            List<PhoneNumber> numbers = c.getPhoneNumbers();
            for (int j = 0; j < numbers.size(); j++) {
                userContact.add(numbers.get(j).getNumber().trim().replaceAll(" ",""));
            }
        }

        return userContact;

    }









   /* private void setUpNavView() {
        if (bottomNavigationView != null) {
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(2));
            menu.getItem(2).setChecked(false);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    selectFragment(item);
                    return false;
                }
            });
        }
    }

    protected void selectFragment(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {

            case R.id.music:
                pushFragment(new Music());
                break;

            case R.id.shop:
                pushFragment(new Shop());
                break;

            case R.id.kids:
                pushFragment(new kids());
                break;

            case R.id.news:
                pushFragment(new News());
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
                fragmentTransaction.replace(R.id.fragment_container_user_home, fragment).commit();
            }
        }

    }*/



}