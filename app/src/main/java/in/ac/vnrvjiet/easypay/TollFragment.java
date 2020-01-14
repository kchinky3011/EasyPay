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

public class TollFragment extends Fragment {

    public TollFragment() {

    }

    CardView four, six;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toll, container, false);
        four = view.findViewById(R.id.fourwheel);
        six = view.findViewById(R.id.twowheeler);
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TolltaxPaymentActivity.class);
                i.putExtra("msg", "Four Wheeler Toll Tax");
                i.putExtra("wheel", 4);
                startActivity(i);
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TolltaxPaymentActivity.class);
                i.putExtra("msg", "Six Wheeler Toll Tax");
                i.putExtra("wheel", 6);

                startActivity(i);
            }
        });
        GridLayout mainGrid = (GridLayout) view.findViewById(R.id.mainGrid);
        //set Event
        //setSingleEvent (mainGrid);
        // setToggleEvent(mainGrid);

        return view;
    }
}

