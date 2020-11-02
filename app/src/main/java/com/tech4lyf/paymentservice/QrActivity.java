package com.tech4lyf.paymentservice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class QrActivity extends AppCompatActivity {

    ImageView imageView;
    String qrData="";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        imageView=(ImageView)findViewById(R.id.imgQR);
        String data = getIntent().getStringExtra("data");

        try {

            JSONObject obj = new JSONObject(data);

            Log.d("Test", obj.toString());

            qrData=obj.getString("image");
            Log.e("QR",qrData);

        } catch (Throwable t) {
            Log.e("Test", "Could not parse malformed JSON: \"" +data + "\"");
        }



        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();

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