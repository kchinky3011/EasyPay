package in.ac.vnrvjiet.easypay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InsurancePaymentActivity extends AppCompatActivity {

    EditText insucom;
    EditText amount;
    Button pay, fetch;
    FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    String uid, paiddate, com;
    EditText policyno;
    private ProgressDialog progressDialog;
    String phoneNumber;
    PersistentDeviceStorage persistentDeviceStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_payment);

        progressDialog = new ProgressDialog(this);
        persistentDeviceStorage = PersistentDeviceStorage.getInstance(this);

        amount = findViewById(R.id.amount);
        policyno = findViewById(R.id.policyno);
        pay = findViewById(R.id.pay);
        fetch = findViewById(R.id.fetch);
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Toast.makeText(this, "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();
        uid = currentFirebaseUser.getUid();


        Bundle bundle = getIntent().getExtras();

        int venName = bundle.getInt("Ins");
        insucom = findViewById(R.id.inscom);
        amount = findViewById(R.id.amount);
        if (venName == 1) {
            insucom.setText("Reliance General Insurance");
            amount.setText("5000");
        } else if (venName == 2) {
            insucom.setText("TATA AIG");
            amount.setText("4100");
        } else if (venName == 3) {
            insucom.setText("Orienntal Insurance");
            amount.setText("4700");
        } else if (venName == 4) {
            insucom.setText("ICICI  General Insurance");
            amount.setText("5100");
        } else if (venName == 5) {
            insucom.setText("HDFC ERGO");
            amount.setText("4900");
        } else if (venName == 6) {
            insucom.setText("BAJAJ Allianz");
            amount.setText("6200");
        }

        insucom.setFocusable(false);
        amount.setFocusable(false);
        com = insucom.getText().toString();


        Log.d("value", venName + "");

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((policyno.length() != 9)) {
                    policyno.setError("Policy Number is must 9 digits");
                    policyno.requestFocus();
                } else {
                    progressDialog.setMessage("Fetching Details");
                    progressDialog.show();
                    phoneNumber = persistentDeviceStorage.getPhoneNumber();
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(phoneNumber).child("payments");

                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(policyno.getText().toString())) {

                                dataSnapshot = dataSnapshot.child(policyno.getText().toString());

                                if (dataSnapshot.hasChild(com)) {

                                    Log.d("datasnapshot", dataSnapshot.getValue() + " ");
                                    dataSnapshot = dataSnapshot.child(com);
                                    paiddate = dataSnapshot.getValue().toString();
                                    paiddate = paiddate.substring(0, 11);
                                    paiddate = paiddate.substring(1);
                                    //Toast.makeText(Pollutiontwo.this,paiddate.toString (),Toast.LENGTH_SHORT).show();


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
        builder.setMessage("Proceed To Payment. Press Yes");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(InsurancePaymentActivity.this, StartPaymentActivity.class);
                //intent.putExtra("phone", phone.getText().toString());
                intent.putExtra("amount", amount.getText().toString());
                intent.putExtra("vehicleno", policyno.getText().toString());
                intent.putExtra("msg", com);
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

