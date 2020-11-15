package com.tech4lyf.paymentservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class FailedActivity extends AppCompatActivity {

    OkHttpClient client;
    String cmd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed);
        stopService(new Intent(FailedActivity.this,BackgroundService.class));
        client = new OkHttpClient();

        Intent intent=getIntent();
        if(intent.getExtras()!=null)
        {
            cmd=intent.getStringExtra("cmd");
        }
        start();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
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
        }, 5000);
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            webSocket.send(cmd);
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




            }
        });
    }


}


