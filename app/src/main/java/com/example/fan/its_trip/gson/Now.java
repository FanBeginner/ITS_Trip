package com.example.fan.its_trip.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/3/8 0008.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
