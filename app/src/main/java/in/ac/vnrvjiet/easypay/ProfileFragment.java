package in.ac.vnrvjiet.easypay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

public class ProfileFragment extends Fragment {

    Button signOut;

    private TextView nameTextView;
    private TextView phoneTextView;

    String name;
    String phoneNumber;

    PersistentDeviceStorage persistentDeviceStorage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        persistentDeviceStorage = PersistentDeviceStorage.getInstance(getContext());

        name = persistentDeviceStorage.getName();
        phoneNumber = persistentDeviceStorage.getPhoneNumber();
        signOut = (Button) view.findViewById(R.id.signOut);

        nameTextView = (TextView) view.findViewById(R.id.username);
        phoneTextView = (TextView) view.findViewById(R.id.phone);

        nameTextView.setText(name);
        phoneTextView.setText(phoneNumber);


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistentDeviceStorage.setEmail("null");
                persistentDeviceStorage.setPhoneNumber("null");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        return view;
    }
}
