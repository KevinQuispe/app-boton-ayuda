package com.pcquispe.appbotonayuda.http;

import android.content.Context;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.pcquispe.appbotonayuda.util.Config;
import com.pcquispe.appbotonayuda.util.TestNet;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

public class Api {
    public static AsyncHttpClient http = new AsyncHttpClient();

    public static void get(Context context, String url, TextHttpResponseHandler responseHandler) {
        http.get(context, Config.URL_SERVER + url, null, "application/json", responseHandler);
    }
    public static void post(Context context, String url, Object data, TextHttpResponseHandler responseHandler) {
        try {
            StringEntity entity = new StringEntity(new Gson().toJson(data));
            http.post(context, Config.URL_SERVER + url, entity, "application/json", responseHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void put(Context context, String url, Object data, TextHttpResponseHandler responseHandler) {
        try {
            StringEntity entity = new StringEntity(new Gson().toJson(data));
            http.put(context, Config.URL_SERVER + url, entity, "application/json", responseHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void delete(Context context, String url, Object data, TextHttpResponseHandler responseHandler) {
        if (TestNet.test(context)){
            StringEntity entity = null;
            try {
                entity = new StringEntity(new Gson().toJson(data));
                http.delete(context, Config.URL_SERVER + url, entity, "application/json", responseHandler);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
