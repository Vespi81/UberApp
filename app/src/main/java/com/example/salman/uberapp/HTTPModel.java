package com.example.salman.uberapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HTTPModel {
    private static String TAG = "SALMAN";

    public static JSONObject post(String url, List<NameValuePair> accessTokenPair) throws IOException, JSONException{
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(accessTokenPair));
        HttpResponse response = new DefaultHttpClient().execute(httpPost);
        InputStream in = response.getEntity().getContent();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
        }
        out.close();
        String resp = out.toString();
        JSONObject accessTokenObj = new JSONObject(resp);
        Log.i(TAG, accessTokenObj.toString());
        String refreshToken = accessTokenObj.getString("refresh_token");
        String accessToken = accessTokenObj.getString("access_token");
        Log.i(TAG, "Refresh Token: " + refreshToken);
        Log.i(TAG, "Access Token: " + accessToken);
        return accessTokenObj;
    }

    public static JSONObject get(String url) {
        JSONObject resp = null;
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", "Bearer " + MainActivity.mAccessTokenObj.getString("access_token"));
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream input = httpResponse.getEntity().getContent();
            OutputStream output = new ByteArrayOutputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = input.read(buffer)) > 0) {
                output.write(buffer, 0, bytesRead);
            }
            output.close();
            String response = output.toString();
            Log.i("SALMAN", "GetInfo: "+ response);
            resp = new JSONObject(response);
        }catch(Exception e){
            Log.e(TAG, "GetInfo: " + e);
        }
        return resp;
    }

    public static Bitmap getImage(String link){
        Bitmap bitmap = null;
        try{
            Log.i(TAG, "getImage begin");
            URL url = new URL(link);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setDoInput(true);
            connect.connect();
            InputStream input = connect.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
            Log.i(TAG, "getImage end");
        }catch(Exception e){
            Log.e(TAG, "GetInfo: " + e.getMessage());
        }
        return bitmap;
    }
}
