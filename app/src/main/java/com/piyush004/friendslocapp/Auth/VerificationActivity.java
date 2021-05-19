package com.piyush004.friendslocapp.Auth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.mukesh.OtpView;
import com.piyush004.friendslocapp.R;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = VerificationActivity.class.getSimpleName();

    private OtpView editTextOtp;
    private Button LoginButton;
    private FirebaseAuth mAuth;
    private View viewAuth;
    private TextView textViewNumber;

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

        editTextOtp = findViewById(R.id.EditTextEnterOTP);
        LoginButton = findViewById(R.id.ButtonSign);
        textViewNumber = findViewById(R.id.textViewNumber);

        Log.e(TAG, "length" + number.length());
        textViewNumber.setText(number.substring(0, 3) + " " + number.substring(3, 13));

        viewAuth = findViewById(R.id.ViewVerification);

        viewAuth.setOnClickListener(v -> UIUtil.hideKeyboard(VerificationActivity.this));

        //Get Otp To otp String Variable
        editTextOtp.setOtpCompletionListener(s -> {
            otp = s;
            Log.e(TAG, "OTP :" + otp);
        });

        LoginButton.setOnClickListener(v -> {

        });

    }
}