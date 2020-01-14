package in.ac.vnrvjiet.easypay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartPaymentActivity extends AppCompatActivity {

    PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
    //declare paymentParam object
    PayUmoneySdkInitializer.PaymentParam paymentParam = null;
    String status = "null", date;
    FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;

    String TAG = "mainActivity", txnid = "txt12346", amount = "20", phone = "9573985291",
            prodname = "PollutionTax", firstname = "RTA", email = "vsappdevelop@gamil.com",
            merchantId = "5884494", merchantkey = "WnWkb6be";  //   first test key only


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpayment);

        Intent intent = getIntent();
        //phone = intent.getExtras().getString("phone");
        amount = intent.getExtras().getString("amount");

        database = FirebaseDatabase.getInstance();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        startpay();
    }

    public void startpay() {

        builder.setAmount(amount)                          // Payment amount
                .setTxnId(txnid)                     // Transaction ID
                .setPhone(phone)                   // User Phone number
                .setProductName(prodname)                   // Product Name or description
                .setFirstName(firstname)                              // User First name
                .setEmail(email)              // User Email ID
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")     // Success URL (surl)
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")     //Failure URL (furl)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(true)                              // Integration environment - true (Debug)/ false(Production)
                .setKey(merchantkey)                        // Merchant key
                .setMerchantId(merchantId);


        try {
            paymentParam = builder.build();
            // generateHashFromServer(paymentParam );
            getHashkey();

        } catch (Exception e) {
            Log.e(TAG, " error s " + e.toString());
        }

    }

    public void getHashkey() {
        ServiceWrapper service = new ServiceWrapper(null);
        Call<String> call = service.newHashCall(merchantkey, txnid, amount, prodname,
                firstname, email);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "hash res " + response.body());
                String merchantHash = response.body();
                if (merchantHash.isEmpty() || merchantHash.equals("")) {
                    Toast.makeText(StartPaymentActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "hash empty");
                } else {
                    // mPaymentParams.setMerchantHash(merchantHash);
                    paymentParam.setMerchantHash(merchantHash);
                    // Invoke the following function to open the checkout page.
                    // PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, StartPaymentActivity.this,-1, true);
                    PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, StartPaymentActivity.this, R.style.AppTheme_default, false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "hash error " + t.toString());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("StartPaymentActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            Log.i(TAG, "onActivityResult: " + String.valueOf(transactionResponse != null && transactionResponse.getPayuResponse() != null));
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    System.out.println("success payment");
                } else {
                    System.out.println("Failure");
                    //Failure Transaction
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
                //Toast.makeText(StartPaymentActivity.this,payuResponse,Toast.LENGTH_SHORT).show();
                //Log.d("log", "tran "+payuResponse+"---"+ merchantResponse);
                JsonParser parser = new JsonParser();
                JsonObject payujson = (JsonObject) parser.parse(payuResponse);
                Log.d("log", payujson.get("result").toString());
                JsonObject statusjson = (JsonObject) parser.parse(payujson.get("result").toString());
                status = statusjson.get("status").toString();
                Log.i(TAG, "onActivityResult: " + status);
                date = statusjson.get("addedon").toString();

                status = status.substring(1, status.length() - 1);

                Log.d("status", status);


            }

            if (status.equals("success")) {
                String vehicleno, msg;
                Intent intent = getIntent();
                vehicleno = intent.getExtras().getString("vehicleno");
                msg = intent.getExtras().getString("msg");

                Log.d("vehicleno", vehicleno);

                String phoneNumber = PersistentDeviceStorage.getInstance(StartPaymentActivity.this).getPhoneNumber();
                PaymentObject model = new PaymentObject(msg,amount,date);

                mDatabaseReference = database.getReference("users").child(phoneNumber).child("payments").child(vehicleno);
                mDatabaseReference.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(StartPaymentActivity.this, "Profile Updated Seccessfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            } else {
                Toast.makeText(StartPaymentActivity.this, "transaction failed", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if(resultCode == RESULT_CANCELED){
            Toast.makeText(StartPaymentActivity.this, "transaction cancelled", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
