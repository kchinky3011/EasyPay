package in.ac.vnrvjiet.easypay;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OTPFragment extends Fragment {

    private static OTPFragment otpFragment;
    FirebaseDatabase firebaseDatabase;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String verificationId;
    private String phoneNumber;
    PersistentDeviceStorage persistentDeviceStorage;
    private OTP otp;
    TextView countDownTimerTextView, resendCode, verficationStatus;
    CountDownTimer countDownTimer;
    private PhoneAuthProvider.ForceResendingToken token;
    private static final String TAG = "OTPFragment";

    public static OTPFragment getInstance() {
        if (otpFragment == null)
            otpFragment = new OTPFragment();
        return otpFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        phoneNumber = "+91" + getArguments().getString("phoneNumber");
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        persistentDeviceStorage = PersistentDeviceStorage.getInstance(getContext());
        countDownTimerTextView = view.findViewById(R.id.count_down_timer);
        otp = view.findViewById(R.id.input_phone);
        resendCode = view.findViewById(R.id.resend_code);
        verficationStatus = view.findViewById(R.id.verification_status);
        otp.requestFocus();

        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 6) {
                    if (verificationId != null) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, s.toString());
                        // [END verify_with_code]
                        signInWithPhoneAuthCredential(credential);
                    }
                }
            }
        });

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDownTimerTextView.setText(String.valueOf(millisUntilFinished / 1000));
                resendCode.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {
                resendCode.setVisibility(View.VISIBLE);
            }
        };


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: in if");
                otp.setText(phoneAuthCredential.getSmsCode());
                if (phoneAuthCredential.getSmsCode() == null) {
                    Log.i(TAG, "onVerificationCompleted: sms code is null");
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
                verficationStatus.setText("Verification Success");
                countDownTimer.cancel();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: ");
                verficationStatus.setText(String.valueOf("Verification Failed"));
                e.printStackTrace();
                countDownTimer.cancel();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d(TAG, "onCodeSent: ");
                verificationId = s;
                verficationStatus.setText(String.valueOf("Code Sent to " + phoneNumber));
                token = forceResendingToken;
                countDownTimer.start();
            }
        };

        requestOTP(phoneNumber);

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestOTP(phoneNumber);
            }
        });

        return view;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.i(TAG, "signInWithPhoneAuthCredential: ");
        if (getActivity() != null) {
            mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "OTP Verification success", Toast.LENGTH_LONG).show();
                        persistentDeviceStorage.setPhoneNumber(phoneNumber);
                        String email = persistentDeviceStorage.getEmail();
                        String name = persistentDeviceStorage.getName();

                        UserModel userModel = new UserModel(email, name);

                        firebaseDatabase.getReference().child("users").child(phoneNumber).setValue(userModel).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                persistentDeviceStorage.setPhoneNumber("null");
                                persistentDeviceStorage.setEmail("null");
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
                            }
                        });

                        Intent signUpScreenIntent = new Intent(getContext(), MainActivity.class);
                        signUpScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(signUpScreenIntent);
                    } else {
                        Toast.makeText(getContext(), "OTP Verification failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void requestOTP(String s) {
        Log.i(TAG, "requestOTP: " + s);
        if (getActivity() != null) {
            phoneNumber = s;
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    s,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    getActivity(),               // Activity (for callback binding)
                    mCallbacks);
        }
    }


}
