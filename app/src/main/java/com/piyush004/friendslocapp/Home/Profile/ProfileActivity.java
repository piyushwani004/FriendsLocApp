package com.piyush004.friendslocapp.Home.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private ImageView imageViewProfile, imageViewEditName;
    private TextView textViewName, textViewMobile;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private Uri uri;
    private Toolbar toolbar;
    private static int SELECT_PHOTO = 1;
    private String Name;

    private DatabaseReference appuser = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        circleImageView = findViewById(R.id.profileImg);
        imageViewProfile = findViewById(R.id.addUserImageProfile);
        imageViewEditName = findViewById(R.id.EditNameImageProfile);

        textViewName = findViewById(R.id.UserNameText);
        textViewMobile = findViewById(R.id.UserPhoneText);

        imageViewProfile.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        });

        imageViewEditName.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.update_dialoge, null);
            final EditText editTextName = dialogLayout.findViewById(R.id.editTextUpdate);

            editTextName.setText(Name);

            builder.setTitle("Update Name");
            builder.setPositiveButton("SAVE", (dialogInterface, i) -> {
                String UpdateName = editTextName.getText().toString().trim();
                appuser.child("Name").setValue(UpdateName).addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Name update", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Network Problem...please try again later..." + e, Toast.LENGTH_SHORT).show());
            });

            builder.setNegativeButton("Closed", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.setView(dialogLayout);
            builder.show();
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {

            if (resultCode == RESULT_OK) {
                uri = data.getData();
                Picasso.get()
                        .load(uri)
                        .resize(500, 500)
                        .centerCrop().rotate(0)
                        .placeholder(R.drawable.person_placeholder)
                        .into(circleImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ProfileActivity.this, "Pick image from Gallery", Toast.LENGTH_SHORT).show();
                                uploadImage();
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(ProfileActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }
    }

    private void uploadImage() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if (uri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child(Name + System.currentTimeMillis() + ".img");
            ref.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();

                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            String ImgURL = uri.toString();
                            appuser.child("ImageURL").setValue(ImgURL).addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Data update", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Network Problem...please try again later..." + e, Toast.LENGTH_SHORT).show());
                        });

                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textViewMobile.setText(snapshot.child("Mobile").getValue(String.class));
                Name = snapshot.child("Name").getValue(String.class);
                Picasso.get().load(snapshot.child("ImageURL").getValue(String.class))
                        .resize(500, 500)
                        .centerCrop()
                        .rotate(0)
                        .placeholder(R.drawable.person_placeholder)
                        .into(circleImageView);
                textViewName.setText(Name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });

    }

}