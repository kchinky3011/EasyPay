package in.ac.vnrvjiet.easypay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

public class PollutionFragment extends Fragment {

    public PollutionFragment() {

    }

    CardView three, two;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pollution, container, false);
        three = view.findViewById(R.id.fourwheel);
        two = view.findViewById(R.id.twowheeler);
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PollutionPaymentActivity.class);
                i.putExtra("act", "Four Wheeler Pollution Tax");
                i.putExtra("wheel", 4);
                startActivity(i);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PollutionPaymentActivity.class);
                i.putExtra("act", "Two Wheeler Pollution Tax");
                i.putExtra("wheel", 2);
                startActivity(i);
            }
        });

        return view;
    }
}

