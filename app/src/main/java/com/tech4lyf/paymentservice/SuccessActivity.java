package com.tech4lyf.paymentservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        stopService(new Intent(SuccessActivity.this,BackgroundService.class));

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
                System.exit(0);
            }
        }, 5000);
    }
}