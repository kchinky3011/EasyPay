package in.ac.vnrvjiet.easypay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PhoneNumberVerification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verification);
        setUpFragmentView();
    }

    private void setUpFragmentView() {
        PhoneNumberFragment phoneNumberFragment = (PhoneNumberFragment) getSupportFragmentManager().findFragmentById(R.id.phone_number_fragment_holder);
        if (phoneNumberFragment == null) {
            phoneNumberFragment = PhoneNumberFragment.getInstance();
            ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(), phoneNumberFragment, R.id.phone_number_fragment_holder);
        }
    }
}
