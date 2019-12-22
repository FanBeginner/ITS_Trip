package com.example.fan.its_trip;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePal;

/**
 * Created by Administrator on 2017/6/2.
 */

public class App extends Application {
    public static Context appContext=null;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext=getApplicationContext();
        SDKInitializer.initialize(this);
        LitePal.initialize(this);
    }
}
