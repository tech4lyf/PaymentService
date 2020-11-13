package com.tech4lyf.paymentservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    Button btnQR,btnStart,btnCas;
    private OkHttpClient client;

    public static String amount,orderno,id;
    public static float amt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();
        btnQR=(Button)findViewById(R.id.btnQR);
        btnStart=(Button)findViewById(R.id.btnStart);

        amount="";
        amt=0;


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            Thread.sleep(2000);
            start();
//            Thread.sleep(2000);
  //          finish();
            Thread.sleep(2000);
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("org.qtproject.example.venduid");
            if (launchIntent != null) {
                startActivity(launchIntent);

               // System.exit(0);
            } else {
                Toast.makeText(getApplicationContext(), "There is no package available in android", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(MainActivity.this, "Exception: "+ex.toString(), Toast.LENGTH_SHORT).show();
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(MainActivity.this, "Starting Service", Toast.LENGTH_SHORT).show();
                try {
                    start();
                }
                catch (Exception ex)
                {
                    Toast.makeText(MainActivity.this, "Exception: "+ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONParser jsonParser=new JSONParser();
                JSONObject jsonObject=jsonParser.getJSONFromUrl("https://clients.tech4lyf.com/quicup/?amount="+amt);
                Log.e("HelloPrice","https://clients.tech4lyf.com/quicup/?amount="+amt);

                try {
                    String test=jsonObject.getString("body");
                    Log.e("Response",test);

                    Intent actQr=new Intent(MainActivity.this,QrActivity.class);
                    actQr.putExtra("data",test);
                    startActivity(actQr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            webSocket.send("state");

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
                Log.e("Response",txt);
//                output.setText(output.getText().toString() + "\n\n" + txt);
//                Toast.makeText(MainActivity.this, txt, Toast.LENGTH_SHORT).show();

                if(txt.contains("VendWaitForCredit"))
                {
                    Toast.makeText(MainActivity.this, "Open QR"+amt, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,PayActivity.class);
                    intent.putExtra("amt",String.valueOf(amt));
                    startActivity(intent);
                }

                if(txt.contains("amount"))
                {
                    String json=txt.substring(12);
                    Log.e("Yuvi",json);
                    try {
                        JSONObject jsonObject = new JSONObject(json);

                        Log.e("JSON Amount",jsonObject.getString("amount"));

                        amount=jsonObject.getString("amount");
                        orderno=jsonObject.getString("order_no");
                        id=jsonObject.getString("id");
                        amt=Float.parseFloat(amount);

                        amt=amt/100;
                        Log.e("HelloPrice",""+amt);
                    }catch (JSONException err){
                        Log.d("Error", err.toString());
                    }

                        }
                    }
                });
            }
        }