package in.ac.vnrvjiet.easypay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101;

    private PersistentDeviceStorage persistentDeviceStorage;

    public Button signUpActivity;

    List<AuthUI.IdpConfig> providers = Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        persistentDeviceStorage = PersistentDeviceStorage.getInstance(this);

        signUpActivity = findViewById(R.id.google_signin);

        signUpActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGoogleSignIn();
            }
        });
    }

    private void startGoogleSignIn() {
        if (Utils.hasActiveInternetConnection(SignUpActivity.this)) {
            try {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No internet, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            assert response != null;
            System.out.println(response.toString());
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();
                    String name = user.getDisplayName();
                    persistentDeviceStorage.setEmail(email);
                    persistentDeviceStorage.setName(name);
                    gotoPhoneVerificationScreen();
                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gotoPhoneVerificationScreen() {
        Intent phoneNumberVerificationIntent = new Intent(this, PhoneNumberVerification.class);
        phoneNumberVerificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(phoneNumberVerificationIntent);
    }
}
