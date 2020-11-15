package com.tech4lyf.paymentservice;

import android.app.Service;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    String OID;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
//                Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
                JSONParser jsonParser=new JSONParser();
                JSONObject jsonObject=jsonParser.getJSONFromUrl("https://clients.tech4lyf.com/quicup/txnstatus.php?orderid="+OID);


                try {
                    String test=jsonObject.getString("body");
                    Log.e("Response",test);
//                    Toast.makeText(context, test, Toast.LENGTH_SHORT).show();

                    try {
                       JSONObject jsonObject1 = new JSONObject(test);
//                       Log.e("JSONRESP",jsonObject1.getString("resultInfo"));

                       JSONObject jsonObject2=new JSONObject(jsonObject1.getString("resultInfo"));
                        Log.e("JSONRESPONSE",jsonObject2.getString("resultStatus"));
                        String resp=jsonObject2.getString("resultStatus");

                        Log.e("TXNSTATUS",resp);
                        if(resp.equals("TXN_SUCCESS"))
                        {
                            Intent actSuccess=new Intent(context,SuccessActivity.class);
                            actSuccess.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            actSuccess.putExtra("cmd", "{\n" +
                                    "\t\t\"channel\":\"wx_pub_qr\",\n" +
                                    "\t\t\"order_no\":\"" +MainActivity.orderno + "\",\n" +
                                    "\t\t\"id\":\"" +000001 + "\",\n" +
                                    "\t\t\"paid\":true,\n" +
                                    "\t\t\"amount\":\"1\",\n" +
                                    "\t\t\"cmd\":\"webhook\"\n" +
                                    "\t}");
                            startActivity(actSuccess);
//
//                            BackgroundService.this.stopSelf();

//                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.tech4lyf.success");
//                            launchIntent.putExtra("status","SUCCESS");
//                            launchIntent.putExtra("orderno",MainActivity.orderno);
//                            launchIntent.putExtra("id",MainActivity.id);
//                            launchIntent.putExtra("amount",String.valueOf(MainActivity.amt));

//                            launchIntent.putExtra("cmd", "{\n" +
//                                    "\t\t\"channel\":\"wx_pub_qr\",\n" +
//                                    "\t\t\"order_no\":\"" +MainActivity.orderno + "\",\n" +
//                                    "\t\t\"id\":\"" +MainActivity.id + "\",\n" +
//                                    "\t\t\"paid\":true,\n" +
//                                    "\t\t\"amount\":\"1\",\n" +
//                                    "\t\t\"cmd\":\"webhook\"\n" +
//                                    "\t}");
//
//                            if (launchIntent != null) {
//                                startActivity(launchIntent);
//
//                                System.exit(0);
//                            } else {
//                                Toast.makeText(context, "There is no package available in android", Toast.LENGTH_LONG).show();
//                            }

                        }

                        else if(resp.equals("TXN_FAILURE"))
                        {
                            Intent actSuccess=new Intent(context,FailedActivity.class);
                            actSuccess.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            actSuccess.putExtra("cmd", "{\n" +
                                    "\t\t\"channel\":\"wx_pub_qr\",\n" +
                                    "\t\t\"order_no\":\"" +MainActivity.orderno + "\",\n" +
                                    "\t\t\"id\":\"" +MainActivity.id + "\",\n" +
                                    "\t\t\"paid\":false,\n" +
                                    "\t\t\"amount\":\"1\",\n" +
                                    "\t\t\"cmd\":\"webhook\"\n" +
                                    "\t}");
                            startActivity(actSuccess);
                        }

                    }catch (JSONException err){
                    Log.d("Error", err.toString());
            }

                    } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(runnable, 2000);
            }
        };

        handler.postDelayed(runnable, 10000);


    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            OID = intent.getStringExtra("OID");

        }
        return START_STICKY;
    }
}