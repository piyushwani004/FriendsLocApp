package com.piyush004.friendslocapp.Auth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;
import com.piyush004.friendslocapp.R;

//Auth No.1 Class (Step 1)
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button NextButton;
    private EditText editTextMobile;
    private CountryCodePicker ccp;
    private View viewAuth;

    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextMobile = (EditText) findViewById(R.id.editTextMobile);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(editTextMobile);
        NextButton = findViewById(R.id.ButtonGetOTP);
        viewAuth = findViewById(R.id.viewLogin);

        NextButton.setOnClickListener(v -> {

            //String Variable for Storing  User Mobile Number...
            mobile = ccp.getFullNumberWithPlus().replace(" ", "");

            Log.e(TAG, "mobile" + mobile);
            Log.e(TAG, "mobile len" + mobile.length());
            Log.e(TAG, "mobile num" + ccp.getFullNumber());
            Log.e(TAG, "mobile for" + ccp.getFormattedFullNumber());


        });


    }
}