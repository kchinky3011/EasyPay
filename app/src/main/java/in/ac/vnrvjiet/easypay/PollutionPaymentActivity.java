package in.ac.vnrvjiet.easypay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class PollutionPaymentActivity extends AppCompatActivity {

    TextView vehicle, amount;
    TextInputEditText vehicleno;
    Button fetch;
    FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    String uid, paiddate, veh;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pollution_payment);

        progressDialog = new ProgressDialog(this);
        //Spinner spinner = findViewById(R.id.spinner);
        vehicle = findViewById(R.id.vehicle);
        vehicleno = findViewById(R.id.vehicleno);
        amount = findViewById(R.id.amount);
        fetch = findViewById(R.id.fetch);


        database = FirebaseDatabase.getInstance();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Toast.makeText(this, "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();
        uid = currentFirebaseUser.getUid();


        Bundle b = getIntent().getExtras();
        veh = b.getString("act");
        int wheel = b.getInt("wheel");
        if (wheel == 2) {
            amount.setText("120");

        } else if (wheel == 4) {
            amount.setText("440");
        } else {
            amount.setText("1500");
        }
        vehicle.setText(veh);


        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vehicleno.getText() != null && vehicleno.getText().toString().length() == 0) {
                    vehicleno.setError("Please Enter a Valid Vechile Number");
                    vehicleno.requestFocus();
                } else if (vehicleno.getText().length() != 10) {
                    vehicleno.setError("Please Enter a Valid Vechile Number");
                    vehicleno.requestFocus();
                } else {
                    progressDialog.setMessage("Fetching Details");
                    progressDialog.show();
                    String phoneNumber = PersistentDeviceStorage.getInstance(PollutionPaymentActivity.this).getPhoneNumber();
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(phoneNumber).child("payments");

                    mDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(vehicleno.getText().toString())) {

                                dataSnapshot = dataSnapshot.child(vehicleno.getText().toString());

                                if (dataSnapshot.hasChild(veh)) {

                                    Log.d("datasnapshot", dataSnapshot.getValue() + " ");
                                    dataSnapshot = dataSnapshot.child(veh);
                                    paiddate = dataSnapshot.getValue().toString();
                                    paiddate = paiddate.substring(0, 11);
                                    paiddate = paiddate.substring(1);

                                    try {
                                        if (validateuser(paiddate) <= 6) {
                                            progressDialog.cancel();
                                            showAlertDialog();


                                        } else {


                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        Log.d("exception", e.toString());
                                    }
                                } else {
                                    showAlertDialogPay();
                                }

                            } else {
                                progressDialog.cancel();
                                showAlertDialogPay();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

    private void showAlertDialogPay() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Status");
        builder.setMessage("Proceed To Payment" + amount.getText().toString() + " Press Yes");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(PollutionPaymentActivity.this, StartPaymentActivity.class);
                //intent.putExtra("phone", phone.getText().toString());
                intent.putExtra("amount", amount.getText().toString());
                intent.putExtra("vehicleno", vehicleno.getText().toString());
                intent.putExtra("msg", veh);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressDialog.cancel();
            }
        });
        builder.create().show();
    }

    private void showAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Status");
        builder.setMessage("No Pendings. Press OK");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        builder.create().show();
    }

    private int validateuser(String paiddate) throws ParseException {

        int y1 = Integer.parseInt(paiddate.substring(0, 4));
        int m1 = Integer.parseInt(paiddate.substring(5, 7));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String today = df.format(new Date());
        int y2 = Integer.parseInt(today.substring(0, 4));
        int m2 = Integer.parseInt(today.substring(5, 7));
        m1 = y1 * 12 + m1;
        m2 = y2 * 12 + m2;
        int diff = m2 - m1;
        Log.d("diff", diff + "");


        return diff;

    }


}
