package com.example.fan.its_trip.gson;

/**
 * Created by Administrator on 2018/3/8 0008.
 */

public class AQI {
    public AQICity city;
    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
