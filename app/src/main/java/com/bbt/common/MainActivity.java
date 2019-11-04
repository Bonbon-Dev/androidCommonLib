package com.bbt.common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bbt.commonlib.operationutil.ProcessUtils;
import com.bbt.commonlib.operationutil.Utils;
import com.bbt.commonlib.toolutil.StrToNumberUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvOne = findViewById(R.id.tvOne);
    }
}
