package com.arulvakku.ui.network;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebServiceHandler {

    private static WebServiceHandler instance;
    private Context mContext;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    private WebServiceHandler(Context context) {
        mContext = context;
    }

    public static WebServiceHandler getInstance(final Context ctx) {
        if (instance == null) {
            instance = new WebServiceHandler(ctx);
        }
        return instance;
    }


    public String postWebServiceCall(String urlStr, RequestBody formBody) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(urlStr).post(formBody)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response mResponse = client.newCall(request).execute();
            String jsonString = mResponse.body().string();
            Log.d(WebServiceHandler.class.getSimpleName(), "" + jsonString);
            return jsonString;
        } catch (SocketTimeoutException e) {
            return "{\"response_code\":100,\"response_message\":\"Connection Timeout\"}";
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "{\"response_code\":100,\"response_message\":\"Internet is too slow. We couldn't connect to server.\"}";
        } catch (InterruptedIOException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"response_code\":100,\"response_message\":\"Couldn't connect to server. Please try again later.\"}";
        }
    }


    public String putWebServiceCall(String urlStr, RequestBody formBody) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(urlStr).put(formBody)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response mResponse = client.newCall(request).execute();
            String jsonString = mResponse.body().string();
            return jsonString;
        } catch (SocketTimeoutException e) {
            return "{\"response_code\":100,\"response_message\":\"Connection Timeout\"}";
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "{\"response_code\":100,\"response_message\":\"Internet is too slow. We couldn't connect to server.\"}";
        } catch (InterruptedIOException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"response_code\":100,\"response_message\":\"Couldn't connect to server. Please try again later.\"}";
        }
    }


    public String gettWebServiceCall(String urlStr) {
        RequestBody body = RequestBody.create(JSON, "");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .build();


        Request request = new Request.Builder().url(urlStr)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response mResponse = client.newCall(request).execute();
            String jsonString = mResponse.body().string();
            Log.d(WebServiceHandler.class.getSimpleName(), "" + jsonString);
            return jsonString;
        } catch (SocketTimeoutException e) {
            return "{\"response_code\":100,\"response_message\":\"Connection Timeout\"}";
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "{\"response_code\":100,\"response_message\":\"Internet is too slow. We couldn't connect to server.\"}";
        } catch (InterruptedIOException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"response_code\":100,\"response_message\":\"Couldn't connect to server. Please try again later.\"}";
        }
    }

    public String getWithRequestWebServiceCall(String urlStr) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .readTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .build();


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PageNo", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .url(urlStr)
                .post(body)
                .build();
        try {
            Response mResponse = client.newCall(request).execute();
            String jsonString = mResponse.body().string();
            return jsonString;
        } catch (SocketTimeoutException e) {
            return "{\"response_code\":100,\"response_message\":\"Connection Timeout\"}";
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "{\"response_code\":100,\"response_message\":\"Internet is too slow. We couldn't connect to server.\"}";
        } catch (InterruptedIOException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"response_code\":100,\"response_message\":\"Couldn't connect to server. Please try again later.\"}";
        }
    }


}
