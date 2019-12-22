package com.example.fan.its_trip.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/3/8 0008.
 */

public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{
        public String max;
        public String min;
    }
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
