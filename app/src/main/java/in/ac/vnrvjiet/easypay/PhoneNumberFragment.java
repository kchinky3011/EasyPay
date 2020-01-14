package in.ac.vnrvjiet.easypay;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneNumberFragment extends Fragment {

    TextView next;
    EditText phoneNumberEditText;
    private static final String TAG = "PhoneNumberFragment";

    Context context;

    public static PhoneNumberFragment getInstance() {
        return new PhoneNumberFragment();
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_number, container, false);
        context = view.getContext();
        next = view.findViewById(R.id.next);
        phoneNumberEditText = view.findViewById(R.id.phone_number_edit_text);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpOTPFragment();
            }
        });

        phoneNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    setUpOTPFragment();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    private void setUpOTPFragment() {
        String phoneNumber = phoneNumberEditText.getText().toString();
        if (phoneNumber.length() == 10) {
            if (Utils.hasActiveInternetConnection(context)) {
                OTPFragment otpFragment = OTPFragment.getInstance();
                Bundle otpBundle = new Bundle();
                Log.i(TAG, "setUpOTPFragment: phone number " + phoneNumber);
                otpBundle.putString("phoneNumber", phoneNumber);
                otpFragment.setArguments(otpBundle);
                if (getFragmentManager() != null) {
                    ActivityUtils.replaceFragmentInActivity(getFragmentManager(), otpFragment, R.id.phone_number_fragment_holder);
                }
            } else {
                Toast.makeText(getContext(), "No Internet\nPlease try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Enter valid phone number", Toast.LENGTH_LONG).show();
        }
    }
}
