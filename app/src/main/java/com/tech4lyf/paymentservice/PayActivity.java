package com.tech4lyf.paymentservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
    String amount;
    float amt;

    ProgressDialog progressDialog;
    Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        client = new OkHttpClient();
        imgBtnCash = (ImageButton) findViewById(R.id.imgBtnCash);
        imgBtnScan = (ImageButton) findViewById(R.id.imgBtnScan);

        progressDialog = new ProgressDialog(this);

        btnClose = (Button) findViewById(R.id.btnClose);

        amount = "";

//        amt=MainActivity.amt;
        Intent intent = getIntent();
        intent.getExtras();
        if (intent != null)
        {

            String a=intent.getStringExtra("amt");
            amt=Float.parseFloat(a);


        }
        start();

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

        imgBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        new DownloadJSONFileAsync().execute();


            }
        });

        imgBtnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PayActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("org.qtproject.example.venduid");
                if (launchIntent != null) {
                    startActivity(launchIntent);

                    // System.exit(0);
                } else {
                    Toast.makeText(getApplicationContext(), "There is no package available in android", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    public class DownloadJSONFileAsync extends AsyncTask<String, Void, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            JSONParser jsonParser=new JSONParser();
            JSONObject jsonObject=jsonParser.getJSONFromUrl("https://clients.tech4lyf.com/quicup/?amount="+amt);

            amt=0;
            MainActivity.amt=0;
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
                Log.e("QRData",test);
                actQr.putExtra("data",test);
                startActivity(actQr);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void unused) {


        }


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
                    Toast.makeText(PayActivity.this, "Open QR"+amt, Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(PayActivity.this,PayActivity.class);
//                    startActivity(intent);
                }
            }
        });
    } 


}