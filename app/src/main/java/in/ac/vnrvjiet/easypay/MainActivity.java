package in.ac.vnrvjiet.easypay;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    PersistentDeviceStorage persistentDeviceStorage;
    private static final String TAG = "MainActivity";

    Button getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        getStarted = findViewById(R.id.getStarted);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"null".equalsIgnoreCase(persistentDeviceStorage.getEmail())) {
                    if (!"null".equalsIgnoreCase(persistentDeviceStorage.getPhoneNumber())) {
                        Intent launchActivityIntent = new Intent(MainActivity.this, LaunchActivity.class);
                        launchActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launchActivityIntent);
                    } else {
                        gotoPhoneVerificationScreen();
                    }
                } else {
                    gotoSignUpScreen();
                }
            }
        });
        persistentDeviceStorage = PersistentDeviceStorage.getInstance(this);
    }

    private void checkSignUp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!"null".equalsIgnoreCase(persistentDeviceStorage.getEmail())) {
                    if ("null".equalsIgnoreCase(persistentDeviceStorage.getPhoneNumber())) {
                        gotoPhoneVerificationScreen();
                    }
                } else {
                    gotoSignUpScreen();
                }
            }
        }, 800);
    }

    private void gotoPhoneVerificationScreen() {
        Log.i(TAG, "gotoPhoneVerificationScreen: ");
        Intent phoneNumberVerificationIntent = new Intent(this, PhoneNumberVerification.class);
        phoneNumberVerificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(phoneNumberVerificationIntent);
    }

    private void gotoSignUpScreen() {
        Log.i(TAG, "gotoSignUpScreen: ");
        Intent signUpScreenIntent = new Intent(this, SignUpActivity.class);
        signUpScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signUpScreenIntent);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        checkSignUp();
    }
}
