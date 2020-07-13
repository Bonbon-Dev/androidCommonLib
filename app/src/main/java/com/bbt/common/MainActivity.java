package com.bbt.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvOne = findViewById(R.id.tvOne);
        TextView tvTwo = findViewById(R.id.tvTwo);
        TextView tv3 = findViewById(R.id.tv3);
        TextView tv4 = findViewById(R.id.tv4);


        TelephonyManager telMag = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        String simOperator = telMag.getSimOperator();
        tvOne.setText("getSimOperator===="+simOperator);
        tvTwo.setText("getNetworkOperator==="+telMag.getNetworkOperator());
        tv3.setText("getSimOperatorName===="+telMag.getSimOperatorName()+"");
        tv4.setText("getNetworkOperatorName===="+telMag.getNetworkOperatorName());
    }
}
