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

public class InsuranceFragment extends Fragment {

    public InsuranceFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate (R.layout.fragment_insurance, container, false);

        GridLayout mainGrid = (GridLayout) view.findViewById (R.id.mainGrid);
        setSingleEvent (mainGrid);
        return view;

    }

    private void setSingleEvent(GridLayout mainGrid) {
        for (int i =0;i<mainGrid.getChildCount();i++){

            CardView cardView = (CardView)mainGrid.getChildAt(i);
            final int finalI = i;

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finalI ==0){
                        Intent intent = new Intent (getActivity (),InsurancePaymentActivity.class);
                        Bundle b=new Bundle();
                        b.putInt ("Ins",1);
                        intent.putExtras (b);
                        startActivity (intent);
                    }
                    else if (finalI==1){
                        Intent intent = new Intent (getActivity (),InsurancePaymentActivity.class);
                        Bundle b1=new Bundle();
                        b1.putInt ("Ins",2);
                        intent.putExtras (b1);
                        startActivity (intent);
                    }
                    else if (finalI==2){
                        Intent intent = new Intent (getActivity (),InsurancePaymentActivity.class);
                        Bundle b2=new Bundle();
                        b2.putInt ("Ins",3);
                        intent.putExtras (b2);
                        startActivity (intent);
                    }
                    else if (finalI==3){
                        Intent intent = new Intent (getActivity (),InsurancePaymentActivity.class);
                        Bundle b3=new Bundle();
                        b3.putInt ("Ins",4);
                        intent.putExtras (b3);


                        startActivity (intent);
                    }
                    else if (finalI==4){
                        Intent intent = new Intent (getActivity (),InsurancePaymentActivity.class);
                        Bundle b4=new Bundle();
                        b4.putInt ("Ins",5);
                        intent.putExtras (b4);
                        startActivity (intent);
                    }
                    else if (finalI==5){
                        Intent intent = new Intent (getActivity (),InsurancePaymentActivity.class);
                        Bundle b5=new Bundle();
                        b5.putInt ("Ins",6);
                        intent.putExtras (b5);
                        startActivity (intent);
                    }
                }

            });
        }

    }

    private void setToggleEvent(GridLayout mainGrid){

    }

}


