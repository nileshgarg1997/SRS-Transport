package com.myapp.srstransport;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Checkout.preload(getApplicationContext());

        doPayment();
    }

    public void doPayment() {


        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_E6qFx4LxDtfyYL");

        /**
         * Set your logo here
         */
//        checkout.setImage(R.drawable.app_icon);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "SRS Transport");
            options.put("description", "Testing Transaction");
            options.put("image", "https://firebasestorage.googleapis.com/v0/b/srs-transport.appspot.com/o/app_icon.jpeg?alt=media&token=648ecdb9-5403-409b-b65c-eecfee0bf2c4");
//            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", "100000");//pass amount in currency subunits
//            options.put("prefill.email", "gaurav.kumar@example.com");
//            options.put("prefill.contact", "8700936587");
            JSONObject prefill=new JSONObject();
            prefill.put("contact","8700936587");
            prefill.put("email","nileshgarg97.ng@gmail.com");
            options.put("prefill",prefill);

            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Error: "+s, Toast.LENGTH_SHORT).show();
        finish();

    }
}