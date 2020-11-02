package com.tech4lyf.paymentservice;

import android.util.Log;

import com.paytm.pg.merchant.PaytmChecksum;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PaytmDQR {

    public void generate()
    {
        JSONObject paytmParams = new JSONObject();

        JSONObject body = new JSONObject();
        try {
            body.put("mid", "YOUR_MID_HERE");
            body.put("orderId", "OREDRID98765");
            body.put("amount", "1303.00");
            body.put("businessType", "UPI_QR_CODE");
            body.put("posId", "S12_123");
        }
        catch (Exception ex)
        {
            Log.e("PayError",ex.toString());
        }
        /*
         * Generate checksum by parameters we have in body
         * You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
         * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
         */

        String checksum = null;
        try {
            checksum = PaytmChecksum.generateSignature(body.toString(), "YOUR_MERCHANT_KEY");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject head = new JSONObject();
        try {
            head.put("clientId", "C11");
            head.put("version", "v1");
            head.put("signature", checksum);

            paytmParams.put("body", body);
            paytmParams.put("head", head);
        }
        catch (Exception ex)
        {
            Log.e("PayError",ex.toString());
        }

        String post_data = paytmParams.toString();

        /* for Staging */
        URL url = null;
        try {
            url = new URL("https://securegw-stage.paytm.in/paymentservices/qr/create");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        /* for Production */
// URL url = new URL("https://securegw.paytm.in/paymentservices/qr/create");

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
            requestWriter.writeBytes(post_data);
            requestWriter.close();
            String responseData = "";
            InputStream is = connection.getInputStream();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
            if ((responseData = responseReader.readLine()) != null) {
                System.out.append("Response: " + responseData);
            }
            responseReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }


    }
}
