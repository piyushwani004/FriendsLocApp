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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;
import com.piyush004.friendslocapp.Database.AuthSteps;
import com.piyush004.friendslocapp.Database.DatabaseHandler;
import com.piyush004.friendslocapp.R;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

//Auth No.1 Class (Step 1)
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button NextButton;
    private EditText editTextMobile;
    private CountryCodePicker ccp;
    private View viewAuth;
    private DatabaseHandler db = new DatabaseHandler(this);
    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextMobile = findViewById(R.id.editTextMobile);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(editTextMobile);
        NextButton = findViewById(R.id.ButtonGetOTP);
        viewAuth = findViewById(R.id.viewLogin);

        viewAuth.setOnClickListener(v -> UIUtil.hideKeyboard(LoginActivity.this));

        NextButton.setOnClickListener(v -> {

            //String Variable for Storing  User Mobile Number...
            mobile = ccp.getFullNumberWithPlus().replace(" ", "");

            Log.e(TAG, "mobile" + mobile);
            Log.e(TAG, "mobile len" + mobile.length());
            Log.e(TAG, "mobile num" + ccp.getFullNumber());
            Log.e(TAG, "mobile for" + ccp.getFormattedFullNumber());

            if (mobile.isEmpty()) {
                editTextMobile.setError("Enter Mobile Number");
                editTextMobile.requestFocus();
            } else if (mobile.length() < 13) {
                editTextMobile.setError("Enter 10-Digit Mobile Number");
                editTextMobile.requestFocus();
            } else if (mobile.length() > 13) {
                editTextMobile.setError("Enter 10-Digit Mobile Number");
                editTextMobile.requestFocus();
            } else if (!(mobile.isEmpty() && mobile.length() != 13)) {
                db.addAllData(new AuthSteps(mobile, "false", "false", "false"));
                db.updateStepOne("true", mobile);
                Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }


        });


    }
}