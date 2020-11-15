package com.tech4lyf.paymentservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class PayActivity extends AppCompatActivity {

    OkHttpClient client;
    ImageButton imgBtnCash,imgBtnScan;
    Button btnClose;
    String amount;
    float amt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        client = new OkHttpClient();
        imgBtnCash=(ImageButton)findViewById(R.id.imgBtnCash);
        imgBtnScan=(ImageButton)findViewById(R.id.imgBtnScan);

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

        Intent intent=getIntent();
        if(intent.getExtras()!=null) {
            Toast.makeText(this, "Tested Amt : " + intent.getStringExtra("amt"), Toast.LENGTH_SHORT).show();
            amt=Float.parseFloat(intent.getStringExtra("amt"));

            amt=amt/100;

            Toast.makeText(this, "Tested Amt1 : " + amt, Toast.LENGTH_SHORT).show();
        }

        SharedPreferences prefs = getSharedPreferences("QUICUPTMP", MODE_PRIVATE);
        Float amt1 = prefs.getFloat("amt", 0f);//"No name defined" is the default value.
        Toast.makeText(this, "SPAmt"+amt1, Toast.LENGTH_SHORT).show();
//        int idName = prefs.getInt("idName", 0); //0 is the default value.
//
//        amount="";
//        amt=MainActivity.amt;
        start();


        imgBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONParser jsonParser=new JSONParser();
                JSONObject jsonObject=jsonParser.getJSONFromUrl("https://clients.tech4lyf.com/quicup/?amount="+amt);

                try {
                    String test=jsonObject.getString("body");
                    Log.e("Response",test);

                    String OID="";

                    OID= StringUtils.substringBetween(test, "&tr=","&cu=");

                    OID=OID.substring(0,15);
                    Log.e("OID",OID);

                    Intent i = new Intent(getApplicationContext(),BackgroundService.class);
                    i.putExtra("OID", OID);
                    startService(i);

                    JSONObject obj = new JSONObject(test);

                    Log.d("Test", obj.toString());

//                    String temp=obj.getString("amount");

                    Intent actQr=new Intent(PayActivity.this,QrActivity.class);
                    actQr.putExtra("data",test);
                    startActivity(actQr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        imgBtnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PayActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {


        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output("Receiving : " + text);
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
        }
    }



    private void start() {
        Request request = new Request.Builder().url("ws://localhost:1234" +
                "").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }



    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                output.setText(output.getText().toString() + "\n\n" + txt);
//              Toast.makeText(PayActivity.this, txt, Toast.LENGTH_SHORT).show();



                if(txt.contains("VendWaitForCredit"))
                {
//                    Toast.makeText(PayActivity.this, "Open QR", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(PayActivity.this,PayActivity.class);
//                    startActivity(intent);
                }
            }
        });
    }


}