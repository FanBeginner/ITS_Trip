package com.example.fan.its_trip.utils;

import android.widget.ImageView;

import com.example.fan.its_trip.App;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.db.City;
import com.example.fan.its_trip.db.County;
import com.example.fan.its_trip.db.Province;
import com.example.fan.its_trip.gson.Weather;
import com.example.fan.its_trip.http.HttpRequest;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/6 0006.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response){
        if(!response.isEmpty()){
            try {
                JSONArray allProvinces=new JSONArray(response);
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCityResponse(String responce,int provinceId){
        if(!responce.isEmpty()){
            try {
                JSONArray allCities=new JSONArray(responce);
                for(int i=0;i<allCities.length();i++){
                    JSONObject cityObject=allCities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountResponse(String responce,int cityId){
        if(!responce.isEmpty()){
            try {
                JSONArray allCounties=new JSONArray(responce);
                for(int i=0;i<allCounties.length();i++){
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    //将返回的JSON数据解析成Weather实体类
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
//    public static  void setWeatherIcon(ImageView imageView, String code){
//        switch (code){
//            case "100":
//                Picasso.with(App.appContext)
//                        .load(R.mipmap)
//                        .into(imageView);
//                break;
//        }
//        if(code.equals("晴"))
//            Picasso.with(App.appContext)
//                    .load(R.drawable.sunny)
//                    .into(imageView);
//        else if(code.equals("阴"))
//            Picasso.with(App.appContext)
//                    .load(R.drawable.overcast)
//                    .into(imageView);
//        else if(code.equals("多云"))
//            Picasso.with(App.appContext)
//                    .load(R.drawable.cloudy)
//                    .into(imageView);
//        else if(code.equals("阵雨"))
//            Picasso.with(App.appContext)
//                    .load(R.drawable.shower)
//                    .into(imageView);
//        else if(code.equals("小雨"))
//            Picasso.with(App.appContext)
//                    .load(R.drawable.light_rain)
//                    .into(imageView);
//        else if(code.equals("中雨"))
//            Picasso.with(App.appContext)
//                    .load(R.drawable.moderate_rain)
//                    .into(imageView);
//        else if(code.equals("大雨"))
//            Picasso.with(App.appContext)
//                    .load(R.drawable.heavy_rain)
//                    .into(imageView);
//        else if(code.equals("雷阵雨"))
//            Picasso.with(App.appContext)
//                    .load(R.drawable.thunder_shower)
//                    .into(imageView);
//    }

}
