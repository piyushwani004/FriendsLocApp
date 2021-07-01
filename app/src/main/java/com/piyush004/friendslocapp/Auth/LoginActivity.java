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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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
    private TextView textViewPolicy, textViewTerm;
    public static final String PRIVACY_POLICY = "file:///android_asset/Privacy.html";
    public static final String TERMS_OF_SERVICE = "file:///android_asset/Terms.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextMobile = findViewById(R.id.editTextMobile);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(editTextMobile);
        NextButton = findViewById(R.id.ButtonGetOTP);
        viewAuth = findViewById(R.id.viewLogin);
        textViewPolicy = findViewById(R.id.PrivacyPolicyText);
        textViewTerm = findViewById(R.id.termOfServiceText);

        viewAuth.setOnClickListener(v -> UIUtil.hideKeyboard(LoginActivity.this));

        textViewPolicy.setOnClickListener(v -> show_dialog(PRIVACY_POLICY));

        textViewTerm.setOnClickListener(v -> show_dialog(TERMS_OF_SERVICE));

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
                db.addAllData(new AuthSteps(mobile, "false", "false", "false", "true"));
                db.updateStepOne("true", mobile);
                Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }


        });


    }

    public void show_dialog(String url) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        WebView webView = new WebView(this);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        builder.setView(webView);
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}