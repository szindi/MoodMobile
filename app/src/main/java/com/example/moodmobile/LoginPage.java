package com.example.moodmobile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * This activity allows users to login with a valid username. and the device IMEI code should match
 * the code of device that user used to created the account, for security reason.
 */
public class LoginPage extends AppCompatActivity {
    private EditText username;

    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        final ArrayList<Account> accountList = new ArrayList<>();

        /* ask for permissions at the login page*/

        if (ContextCompat.checkSelfPermission(LoginPage.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginPage.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(LoginPage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginPage.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }


        Button loginButton = (Button) findViewById(R.id.loginBot);
        username = (EditText) findViewById(R.id.username);

        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                setResult(RESULT_OK);
                //TODO Unused Variable
                //Account newUser = new Account(username.getText().toString());

                ElasticsearchAccountController.GetUser getUser = new ElasticsearchAccountController.GetUser();
                getUser.execute(username.getText().toString());


                try {
                    accountList.clear();
                    accountList.addAll(getUser.get());
                } catch (Exception e) {
                    Log.i("Error", "Failed to get the tweets out of asyc object");
                }
                //Context context = getApplicationContext();
                //Toast.makeText(context, accountList.get(0).getUsername(), Toast.LENGTH_SHORT).show();
                if (accountList.size() == 0) {

                    Context context = getApplicationContext();
                    Toast.makeText(context, "username does not exist!", Toast.LENGTH_SHORT).show();
                }

                else {
                    String myAndroidDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                    if (accountList.get(0).getIMEI() != null && accountList.get(0).getIMEI().equals(myAndroidDeviceId)) {
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Log in successfully!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        Intent mainIntent = new Intent(LoginPage.this, MainPageActivity.class);
                        mainIntent.putExtra("username", accountList.get(0).getUsername());

                        startActivity(mainIntent);
                        finish();
                    }else{
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Device doesn't match!", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });
    }


    /**
     * this is the onClickListener for "createAccount" button, it goes to the Create Account Activity
     */
    public void createAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }



}