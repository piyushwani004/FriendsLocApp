package com.piyush004.friendslocapp.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.piyush004.friendslocapp.Home.HomeActivity;
import com.piyush004.friendslocapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

//Auth No.3 Class (Step 3)
public class ProfileUpdate extends AppCompatActivity {

    private CircleImageView circleImageView;
    private ImageView imageViewEditName, imageViewUploadImage;
    private TextView textViewName, textViewMobile;
    private FirebaseAuth mAuth;
    private Button buttonFinished;
    private Uri uri;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private HashMap<String, Object> hashMap;

    private static int SELECT_PHOTO = 2;
    private String Name, Mobile, phonenumber, imgUrl;

    private DatabaseReference appuser = FirebaseDatabase.getInstance().getReference().child("AppUsers")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        mAuth = FirebaseAuth.getInstance();
        phonenumber = mAuth.getCurrentUser().getPhoneNumber();

        circleImageView = findViewById(R.id.profileImg);
        textViewName = findViewById(R.id.UserNameText);
        textViewMobile = findViewById(R.id.UserPhoneText);

        imageViewEditName = findViewById(R.id.EditNameImageProfile);
        imageViewUploadImage = findViewById(R.id.upload_image);

        buttonFinished = findViewById(R.id.ButtonFinished);

        hashMap = new HashMap<>();

        appuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Name = snapshot.child("Name").getValue(String.class);
                Mobile = snapshot.child("Mobile").getValue(String.class);
                imgUrl = snapshot.child("ImageURL").getValue(String.class);
                uri=Uri.parse(imgUrl);

                if (Name == null) {
                    textViewName.setText("User Name");
                } else {
                    textViewName.setText(Name);
                }

                if (Mobile == null) {
                    textViewName.setText("User Mobile");
                } else {
                    textViewMobile.setText(Mobile);
                }

                if (imgUrl == null) {
                    Picasso.get()
                            .load(R.drawable.person_placeholder)
                            .into(circleImageView);
                } else {
                    Picasso.get()
                            .load(imgUrl)
                            .placeholder(R.drawable.person_placeholder)
                            .into(circleImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        buttonFinished.setOnClickListener(v -> {

            if (textViewName.getText().toString().equals("User Name")) {
                Toast.makeText(this, "Enter User Name...", Toast.LENGTH_LONG).show();
            } else if (uri == null) {
                Toast.makeText(this, "Select Profile Image...", Toast.LENGTH_LONG).show();
            } else if (!(textViewName.getText().toString().equals("User Name") && uri == null)) {

                Intent intent = new Intent(ProfileUpdate.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                Toast.makeText(ProfileUpdate.this, "Welcome User ", Toast.LENGTH_LONG).show();

            }

        });

        imageViewEditName.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUpdate.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.upload_name_dialoge, null);
            final EditText editTextName = dialogLayout.findViewById(R.id.editUserName);

            editTextName.setText(Name);

            builder.setTitle("Update Name");
            builder.setPositiveButton("SAVE", (dialogInterface, i) -> {
                String UpdateName = editTextName.getText().toString().trim();
                appuser.child("Name").setValue(UpdateName).addOnSuccessListener(aVoid -> Toast.makeText(ProfileUpdate.this,
                        "Name Update", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(ProfileUpdate.this,
                                "Network Problem...please try again later..." + e, Toast.LENGTH_SHORT).show());
            });
            builder.setNegativeButton("Close", (dialog, which) -> {

            });

            builder.setView(dialogLayout);
            builder.show();
        });

        imageViewUploadImage.setOnClickListener(v -> {

            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);

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
                                Toast.makeText(ProfileUpdate.this, "Pick image from Gallery", Toast.LENGTH_SHORT).show();
                                uploadImage();
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(ProfileUpdate.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }
    }

    private void uploadImage() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if (uri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(ProfileUpdate.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("Profile").child(mAuth.getCurrentUser().getUid()).child(Name + System.currentTimeMillis() + ".img");
            ref.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();

                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            String ImgURL = uri.toString();
                            appuser.child("ImageURL").setValue(ImgURL).addOnSuccessListener(aVoid -> Toast.makeText(ProfileUpdate.this, "Image update", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(ProfileUpdate.this, "Network Problem...please try again later..." + e, Toast.LENGTH_SHORT).show());
                        });

                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileUpdate.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }


}