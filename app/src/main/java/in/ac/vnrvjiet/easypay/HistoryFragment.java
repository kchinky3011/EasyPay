package in.ac.vnrvjiet.easypay;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private String uid;
    ProgressDialog progressDialog;
    private String phoneNumber;
    PersistentDeviceStorage persistentDeviceStorage;
    int i = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        persistentDeviceStorage = PersistentDeviceStorage.getInstance(getContext());
        phoneNumber = persistentDeviceStorage.getPhoneNumber();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        progressDialog = new ProgressDialog(getActivity());
        fetch();
        return view;
    }

    private void fetch() {
        progressDialog.setMessage("loading details");
        progressDialog.show();
        final ArrayList<String> keys = new ArrayList<>();
        final ArrayList<String> values = new ArrayList<>();


        final Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("users").child(phoneNumber).child("payments");


        FirebaseRecyclerOptions<PaymentObject> options =
                new FirebaseRecyclerOptions.Builder<PaymentObject>()
                        .setQuery(query, new SnapshotParser<PaymentObject>() {
                            @NonNull
                            @Override
                            public PaymentObject parseSnapshot(@NonNull DataSnapshot snapshot) {
                                String key = snapshot.getKey();
                                PaymentObject paymentObject = snapshot.getValue(PaymentObject.class);
                                paymentObject.setKey(key);
                                progressDialog.cancel();
                                return paymentObject;
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter<PaymentObject, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, PaymentObject paymentObject) {
                holder.setTxtTitle(paymentObject.getKey());
                holder.setTxtDesc(paymentObject.getType());
                holder.setTxtDate(paymentObject.getDate());
                holder.setAmount(paymentObject.getAmount());
            }

        };
        recyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDesc, date, amount;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.list_title);
            txtDesc = itemView.findViewById(R.id.list_desc);
            date = itemView.findViewById(R.id.datetime);
            amount = itemView.findViewById(R.id.amount);
        }

        public void setTxtTitle(String string) {
            txtTitle.setText(string);
        }

        public void setTxtDesc(String string) {
            txtDesc.setText(string);
        }

        public void setTxtDate(String string) {
            date.setText(string);
        }

        public void setAmount(String string) {
            amount.setText(string);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}