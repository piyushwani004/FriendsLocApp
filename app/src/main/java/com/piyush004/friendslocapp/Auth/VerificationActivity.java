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

package com.piyush004.friendslocapp.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mukesh.OtpView;
import com.piyush004.friendslocapp.Database.DatabaseHandler;
import com.piyush004.friendslocapp.R;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.concurrent.TimeUnit;

//Auth No.2 Class (Step 2)
public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = VerificationActivity.class.getSimpleName();

    private OtpView editTextOtp;
    private Button LoginButton;
    private FirebaseAuth mAuth;
    private View viewAuth;
    private TextView textViewNumber;
    private ProgressBar ringProgressBar;
    private DatabaseHandler db = new DatabaseHandler(this);
    private String otp, number;
    private String phonenumber;
    private String otpid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //Get Mobile Number From Login Activity...
        phonenumber = getIntent().getStringExtra("mobile");
        number = phonenumber;
        mAuth = FirebaseAuth.getInstance();

        ringProgressBar = findViewById(R.id.ringProgressBar);
        editTextOtp = findViewById(R.id.EditTextEnterOTP);
        LoginButton = findViewById(R.id.ButtonSign);
        textViewNumber = findViewById(R.id.textViewNumber);

        Log.e(TAG, "length" + number.length());

        String concat = number.substring(0, 3) + " " + number.substring(3, 13);
        textViewNumber.setText(concat);

        viewAuth = findViewById(R.id.ViewVerification);

        viewAuth.setOnClickListener(v -> UIUtil.hideKeyboard(VerificationActivity.this));

        //Get Otp To otp String Variable
        editTextOtp.setOtpCompletionListener(s -> {
            otp = s;
            Log.e(TAG, "OTP :" + otp);
        });

        LoginButton.setOnClickListener(v -> {
            ringProgressBar.setVisibility(View.VISIBLE);
            String code = editTextOtp.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                editTextOtp.setError("Enter code...");
                editTextOtp.requestFocus();
                return;
            }
            verifyCode(code);
        });

    }

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpid, code);
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            ringProgressBar.setVisibility(View.GONE);
            Toast toast = Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        ringProgressBar.setVisibility(View.GONE);
                        DatabaseReference addMemberData = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(mAuth.getCurrentUser().getUid());
                        addMemberData.child("Mobile").setValue(phonenumber);
                        addMemberData.child("ID").setValue(mAuth.getCurrentUser().getUid()).addOnSuccessListener(aVoid -> {
                            db.updateStepTwo("true", phonenumber);
                            Intent intent = new Intent(VerificationActivity.this, ProfileUpdate.class);
                            startActivity(intent);
                            finish();
                        });

                    } else {

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(VerificationActivity.this, "Incorrect OTP entered", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(VerificationActivity.this, "Unable to verify please retry later", Toast.LENGTH_LONG).show();
                        }
                        ringProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void initiateotp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,        // Phone number to verify
                60,
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        otpid = s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        String code = phoneAuthCredential.getSmsCode();
                        if (code != null) {
                            editTextOtp.setText(code);
                        }
                        signInWithPhoneAuthCredential(phoneAuthCredential);

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("Project", "Message" + e.getMessage());
                        ringProgressBar.setVisibility(View.GONE);
                    }

                });        // OnVerificationStateChangedCallbacks

    }


    @Override
    protected void onStart() {
        super.onStart();
        initiateotp();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringProgressBar != null) {
            ringProgressBar.setVisibility(View.GONE);
        }
    }
}