package com.tech4lyf.paymentservice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QrActivity extends AppCompatActivity {

    ImageView imageView;
    String qrData="";
    Button btnClose;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

//        Intent serviceIntent = new Intent(BackgroundService.class.getName());
//        serviceIntent.putExtra("OID", );
//        this.startService(serviceIntent);
//        startService(new Intent(QrActivity.this, BackgroundService.class));

        btnClose=(Button)findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("org.qtproject.example.venduid");
                if (launchIntent != null) {
                    startActivity(launchIntent);

                    // System.exit(0);
                } else {
                    Toast.makeText(getApplicationContext(), "There is no package available in android", Toast.LENGTH_LONG).show();
                }
            }
        });

        Handler handler = new Handler();

        handler.postDelayed(new Runnable () {
            public void run() {
//                finish();
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("org.qtproject.example.venduid");
                if (launchIntent != null) {
                    startActivity(launchIntent);

                    // System.exit(0);
                } else {
                    Toast.makeText(getApplicationContext(), "There is no package available in android", Toast.LENGTH_LONG).show();
                }
            }
        }, 60000);


        imageView=(ImageView)findViewById(R.id.imgQR);
        String data = getIntent().getStringExtra("data");

        try {

            JSONObject obj = new JSONObject(data);

            Log.d("Test", obj.toString());

            qrData=obj.getString("image");
            String qrDataUPI=obj.getString("qrData");
            Log.e("QR",qrData);
            Log.e("QRData",qrDataUPI);

//            String OID="";
//
//            OID= StringUtils.substringBetween(qrDataUPI, "&tr=","&cu=");
//
//            OID=OID.substring(0,15);
//            Log.e("OID",OID);

//            Intent serviceIntent = new Intent(BackgroundService.class.getName());
//            serviceIntent.putExtra("OID",OID );
//            this.startService(serviceIntent);

        } catch (Throwable t) {
            Log.e("Test", "Could not parse malformed JSON: \"" +data + "\"");
        }



//        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();

        data = java.net.URLDecoder.decode(qrData);

        Bitmap bmp=StringToBitMap(qrData);
        imageView.setImageBitmap(bmp);


    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

// second solution is you can set the path inside decodeFile function
//viewImage.setImageBitmap(BitmapFactory.decodeFile("your iamge path"));
}