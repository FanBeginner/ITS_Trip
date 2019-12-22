package com.example.fan.its_trip.http;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fan.its_trip.App;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2017/6/2.
 */

public class HttpRequest {

    private static final RequestQueue mQueue = Volley.newRequestQueue(App.appContext);
    public static SharedPreferences sp = App.appContext.getSharedPreferences("setting", MODE_PRIVATE);
    public static void request(String action, JSONObject jso, Response.Listener<JSONObject> ok, Response.ErrorListener er) {
        //SharedPreferences sp = App.appContext.getSharedPreferences("setting", Context.MODE_PRIVATE);
        String ip = sp.getString("ipUrl", "192.168.1.106");
        String port = sp.getString("ipPort", "8080");
        String urls = "http://" + ip + ":" + port + "/api/v2/" + action;
        if (ok == null) {
            ok = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {

                }
            };
        }
        if (er == null) {
            er = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            };
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urls, jso, ok, er);
        mQueue.add(jsonObjectRequest);
    }

    public static void setUserName(String UserName){
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("UserName",UserName);
        editor.apply();
    }
    public static String getUserName() {
       //SharedPreferences sp = App.appContext.getSharedPreferences("setting", Context.MODE_PRIVATE);
        return sp.getString("UserName", "user1");
    }
    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//年月日 时分秒
        return format.format(new Date().getTime());
    }
    public static void setRole(String role){
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("Role",role);
        editor.apply();
    }
    public static String getRole(){
        return sp.getString("Role","");
    }
}


