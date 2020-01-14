package in.ac.vnrvjiet.easypay;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import android.widget.Spinner;
import android.widget.TextView;

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

public class TolltaxPaymentActivity extends AppCompatActivity implements View.OnClickListener {

    TextView vehicle, amount;
    String from = "", to = "";
    ArrayAdapter<CharSequence> fromadapter, toadapter;
    Spinner spinner, myspinner;
    int wheel = 0;
    Button fetch;
    FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    String uid, paiddate, veh;
    private ProgressDialog progressDialog;
    TextInputEditText vehicleno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tolltax_payment);
        spinner = findViewById(R.id.from);
        vehicle = findViewById(R.id.vehicle);
        amount = findViewById(R.id.amount);
        fetch = findViewById(R.id.pay);
        vehicleno = findViewById(R.id.tollveh);
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentFirebaseUser.getUid();

        Bundle b = getIntent().getExtras();
        String veh = b.getString("msg");
        wheel = b.getInt("wheel");
        vehicle.setText(veh);
        fromadapter = ArrayAdapter.createFromResource(this,
                R.array.from, android.R.layout.simple_spinner_item);
        fromadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(fromadapter);
        myspinner = findViewById(R.id.to);
        fetch.setOnClickListener(this);

        myspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                to = parent.getItemAtPosition(position).toString();
                if (from.equals("Hyderabad") && to.equals("Warangal") || from.equals("Warangal") && to.equals("Hyderabad")) {
                    if (wheel == 4) {
                        amount.setText("320");
                    } else {
                        amount.setText("600");

                    }
                }
                if (from.equals("Hyderabad") && to.equals("Khammam") || from.equals("Khammam") && to.equals("Hyderabad")) {
                    if (wheel == 4) {
                        amount.setText("456");
                    } else {
                        amount.setText("715.2");

                    }
                }
                if (from.equals("Khammam") && to.equals("Warangal") || from.equals("Warangal") && to.equals("Khammam")) {
                    if (wheel == 4) {
                        amount.setText("450");
                    } else {
                        amount.setText("550");

                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                from = parent.getItemAtPosition(position).toString();

                if (from.equals("Hyderabad")) {

                    myspinner.setAdapter(new ArrayAdapter<String>(TolltaxPaymentActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.to1)));

                } else if (from.equals("Khammam")) {
                    // else if(item=="")
                    myspinner.setAdapter(new ArrayAdapter<String>(TolltaxPaymentActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.to3)));

                } else if (from.equals("Warangal")) {
                    myspinner.setAdapter(new ArrayAdapter<String>(TolltaxPaymentActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.to2)));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if (vehicleno == null) {
            vehicleno.setError("Please Enter a Valid Vechile Number");
            vehicleno.requestFocus();
        } else {

            progressDialog.setMessage("Fetching Details");
            progressDialog.show();

            String phoneNumber = PersistentDeviceStorage.getInstance(TolltaxPaymentActivity.this).getPhoneNumber();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(phoneNumber).child("payments");

            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(vehicleno.getText().toString())) {

                        //Toast.makeText(Pollutiontwo.this,"vehicle no exists",Toast.LENGTH_SHORT).show();
                        dataSnapshot = dataSnapshot.child(vehicleno.getText().toString());

                        if (dataSnapshot.hasChild("Tolltax")) {
                            //Toast.makeText(Pollutiontwo.this,"child exists",Toast.LENGTH_SHORT).show();

                            Log.d("datasnapshot", dataSnapshot.getValue() + " ");
                            dataSnapshot = dataSnapshot.child("Tolltax");
                            paiddate = dataSnapshot.getValue().toString();
                            paiddate = paiddate.substring(11, paiddate.length() - 1);
                            //Toast.makeText(TolltaxPaymentActivity.this,paiddate.toString (),Toast.LENGTH_SHORT).show();


                            try {
                                if (validateuser(paiddate) < 12.0) {
                                    progressDialog.cancel();
                                    showAlertDialog();


                                } else {
                                    progressDialog.cancel();
                                    showAlertDialogPay();


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


    private void showAlertDialogPay() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Status");
        builder.setMessage("Proceed To Payment " + amount.getText().toString() + "Press Yes");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(TolltaxPaymentActivity.this, StartPaymentActivity.class);
                //intent.putExtra("phone", phone.getText().toString());
                intent.putExtra("amount", amount.getText().toString());
                intent.putExtra("vehicleno", vehicleno.getText().toString());
                intent.putExtra("msg", "Tolltax");
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
        try {
            builder.create().show();
        } catch (Exception e) {

        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private double validateuser(String paiddate) throws ParseException {

        double h = Double.parseDouble(paiddate.substring(0, 3));
        double m = Double.parseDouble(paiddate.substring(4, 6));
        double s = Double.parseDouble(paiddate.substring(7, 9));
        //Log.d("time",h+" "+m+" "+s);
        double diff = 0.0;
       /* SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now ();
        String time = df.format(now);*/

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String today = df.format(new Date());
        int len = today.length();
        // Log.d("time",today+" ");

        String now = today.substring(11);
        Log.d("h", h + " ");

        try {
            double h2 = Double.parseDouble(now.substring(0, 3));
            double m2 = Double.parseDouble(now.substring(4, 6));
            double s2 = Double.parseDouble(now.substring(7));

            /*double h3 = h + (m / 60) + (s / 3600);
            double h4 = h2 + (m2 / 60) + (s2 / 3600);
            */
            Log.d("time", h2 + "");


            diff = h2 - h;


            Log.d("diff", diff + "");

        } catch (Exception e) {
            e.printStackTrace();
        }


        return diff;
    }


}
