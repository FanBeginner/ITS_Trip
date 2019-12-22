package com.example.fan.its_trip.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fan.its_trip.App;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.gson.Forecast;
import com.example.fan.its_trip.gson.Weather;
import com.example.fan.its_trip.progress.CircleProgress;
import com.example.fan.its_trip.toast.MyToast;
import com.example.fan.its_trip.utils.HttpUtil;
import com.example.fan.its_trip.utils.Utility;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.Lifestyle;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.LifestyleBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Fan on 2019/7/11.
 */

public class Fragment_HomePage extends Fragment implements View.OnClickListener {


    private TextView title_city;
    private ImageView im_location;
    private TextView degree_text;
    private ImageView im_today;
    private TextView weather_info_text;
    private TextView tomorrow_text;
    private ImageView im_tomorrow;
    private TextView tomorrow_text_info;
    private LinearLayout forecast_layout;
    private LinearLayout suggestion_layout;
    private CircleProgress cp_aqi_text;
    private CircleProgress cp_pm_text;
    private ScrollView weather_layout;
    private SwipeRefreshLayout weather_refresh;
    private String weatherId;
    private static final String TAG = "Fragment_HomePage";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_homepage, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        title_city = (TextView) view.findViewById(R.id.title_city);
        title_city.setOnClickListener(this);
        im_location = (ImageView) view.findViewById(R.id.im_location);
        degree_text = (TextView) view.findViewById(R.id.degree_text);
        degree_text.setOnClickListener(this);
        im_today = (ImageView) view.findViewById(R.id.im_today);
        im_today.setOnClickListener(this);
        weather_info_text = (TextView) view.findViewById(R.id.weather_info_text);
        weather_info_text.setOnClickListener(this);
        tomorrow_text = (TextView) view.findViewById(R.id.tomorrow_text);
        tomorrow_text.setOnClickListener(this);
        im_tomorrow = (ImageView) view.findViewById(R.id.im_tomorrow);
        im_tomorrow.setOnClickListener(this);
        tomorrow_text_info = (TextView) view.findViewById(R.id.tomorrow_text_info);
        tomorrow_text_info.setOnClickListener(this);
        forecast_layout = (LinearLayout) view.findViewById(R.id.forecast_layout);
        suggestion_layout = (LinearLayout) view.findViewById(R.id.suggestion_layout);
        cp_aqi_text = (CircleProgress) view.findViewById(R.id.cp_aqi_text);
        cp_aqi_text.setOnClickListener(this);
        cp_pm_text = (CircleProgress) view.findViewById(R.id.cp_pm_text);
        cp_pm_text.setOnClickListener(this);

        weather_layout = (ScrollView) view.findViewById(R.id.weather_layout);
        weather_layout.setOnClickListener(this);
        weather_refresh=view.findViewById(R.id.weather_swip_refresh);

        //账户初始化
        HeConfig.init("HE1803051633311373", "2058b91be95d43e3864089a2550d4e38");
        //切换到免费服务域名
        HeConfig.switchToFreeServerNode();

        weather_refresh.setColorSchemeResources(R.color.colorAccent);
        weather_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getWeather(weatherId);
                                weather_refresh.setRefreshing(false);
                                MyToast.showInfo("刷新成功！");
                            }
                        });
                    }
                }).start();

            }
        });

        im_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Main,new ChooseAreaFragment()).commit();

            }
        });

        SharedPreferences pref = App.appContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        weatherId = pref.getString("weather_id", "");
        if(weatherId.isEmpty())
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Main, new ChooseAreaFragment()).commit();
        weather_layout.setVisibility(View.VISIBLE);
        getWeather(weatherId);

    }
    public void getWeather(String weatherId) {
        HeWeather.getWeatherNow(getContext(), weatherId, new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "Now-onError: ", throwable);
            }

            @Override
            public void onSuccess(Now now) {
                Log.i(TAG, "now--onSuccess: " + new Gson().toJson(now));

                if (now.getStatus().equals("ok")) {
                    title_city.setText(now.getBasic().getLocation());
                    degree_text.setText(now.getNow().getTmp() + "℃");
                    weather_info_text.setText(now.getNow().getCond_txt());
                    setWeatherLogo(im_today,now.getNow().getCond_txt());
                } else {
                    MyToast.showError("now-"+now.getStatus());
                    return;
                }
            }
        });
        HeWeather.getAirNow(getContext(), weatherId, new HeWeather.OnResultAirNowBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "airnow--onError: ", throwable);
            }

            @Override
            public void onSuccess(AirNow airNow) {
                Log.i(TAG, "airnow--onSuccess: "+new Gson().toJson(airNow));
                if(airNow.getStatus().equals("ok")){
                    int aqi=Integer.valueOf(airNow.getAir_now_city().getAqi());
                    int pm=Integer.valueOf(airNow.getAir_now_city().getPm25());
                    cp_aqi_text.setValue(aqi);
                    if(aqi<=50) {
                        cp_aqi_text.setUnit("优");
                        cp_aqi_text.setUnitColor(Color.parseColor("#00FF00"));
                    }else if(aqi<=100) {
                        cp_aqi_text.setUnit("良好");
                        cp_aqi_text.setUnitColor(Color.parseColor("#006400"));
                    }else if(aqi<=150) {
                        cp_aqi_text.setUnit("轻度污染");
                        cp_aqi_text.setUnitColor(Color.parseColor("#FF7F50"));
                    }else if(aqi<=200) {
                        cp_aqi_text.setUnit("中度污染");
                        cp_aqi_text.setUnitColor(Color.parseColor("#FF4500"));
                    }else if(aqi<=300) {
                        cp_aqi_text.setUnit("重度污染");
                        cp_aqi_text.setUnitColor(Color.parseColor("#FF0000"));
                    }else if(aqi>300) {
                        cp_aqi_text.setUnit("严重污染");
                        cp_aqi_text.setUnitColor(Color.parseColor("#8B0000"));
                    }
                    cp_pm_text.setValue(pm);
                    if(pm<=50) {
                        cp_pm_text.setUnit("优");
                        cp_pm_text.setUnitColor(Color.parseColor("#00FF00"));
                    }else if(pm<=100) {
                        cp_pm_text.setUnit("良好");
                        cp_pm_text.setUnitColor(Color.parseColor("#006400"));
                    }else if(pm<=150) {
                        cp_pm_text.setUnit("轻度污染");
                        cp_pm_text.setUnitColor(Color.parseColor("#FF7F50"));
                    }else if(pm<=200) {
                        cp_pm_text.setUnit("中度污染");
                        cp_pm_text.setUnitColor(Color.parseColor("#FF4500"));
                    }else if(pm<=300) {
                        cp_pm_text.setUnit("重度污染");
                        cp_pm_text.setUnitColor(Color.parseColor("#FF0000"));
                    }else if(pm>300) {
                        cp_pm_text.setUnit("严重污染");
                        cp_pm_text.setUnitColor(Color.parseColor("#8B0000"));
                    }
                }else {
                    MyToast.showError("air-"+airNow.getStatus());
                    return;
                }
            }
        });
        HeWeather.getWeatherForecast(getContext(), weatherId, new HeWeather.OnResultWeatherForecastBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "forecast--onError: ", throwable);
            }

            @Override
            public void onSuccess(interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast forecast) {
                Log.i(TAG, "forecast--onSuccess: "+new Gson().toJson(forecast));
                if(forecast.getStatus().equalsIgnoreCase("ok")){
                    tomorrow_text.setText(forecast.getDaily_forecast().get(1).getTmp_min()+"~"+forecast.getDaily_forecast().get(1).getTmp_max()+"℃");
                    tomorrow_text_info.setText(forecast.getDaily_forecast().get(1).getCond_txt_d());
                    setWeatherLogo(im_tomorrow,forecast.getDaily_forecast().get(1).getCond_txt_d());
                    forecast_layout.removeAllViews();
                    for(ForecastBase forecastBase:forecast.getDaily_forecast()){

                        View view = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item, forecast_layout, false);
                        TextView dateText = view.findViewById(R.id.date_text);
                        TextView infoText = view.findViewById(R.id.info_text);
                        TextView maxText = view.findViewById(R.id.max_text);
                        TextView minText = view.findViewById(R.id.min_text);
                        ImageView imInfoLogo=view.findViewById(R.id.im_info_text);
                        dateText.setText(forecastBase.getDate());
                        infoText.setText(forecastBase.getCond_txt_d());
                        setWeatherLogo(imInfoLogo,forecastBase.getCond_txt_d());
                        maxText.setText(forecastBase.getTmp_max()+"℃");
                        minText.setText(forecastBase.getTmp_min());
                        forecast_layout.addView(view);
                    }

                }else {
                    MyToast.showError("forecast-"+forecast.getStatus());
                    return;
                }
            }
        });
        HeWeather.getWeatherLifeStyle(getContext(), weatherId, new HeWeather.OnResultWeatherLifeStyleBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "lifestyle--onError: ",throwable );
            }

            @Override
            public void onSuccess(Lifestyle lifestyle) {
                Log.i(TAG, "lifestyle--onSuccess: "+new Gson().toJson(lifestyle));
                if(lifestyle.getStatus().equalsIgnoreCase("ok")){
                    suggestion_layout.removeAllViews();
                    for(LifestyleBase lifestyleBase:lifestyle.getLifestyle()){
                        View view=LayoutInflater.from(getContext()).inflate(R.layout.suggestion_item,suggestion_layout,false);
                        TextView tv_sug_brf= view.findViewById(R.id.tv_sug_brf);
                        TextView tv_sug_comf= view.findViewById(R.id.tv_sug_comf);
                        TextView tv_sug_txt= view.findViewById(R.id.tv_sug_txt);
                        ImageView im_sug_icon=view.findViewById(R.id.im_sug_icon);
                        if(lifestyleBase.getType().equalsIgnoreCase("comf")){
                            tv_sug_comf.setText("舒适度指数");
                            tv_sug_brf.setText(lifestyleBase.getBrf());
                            tv_sug_txt.setText(lifestyleBase.getTxt());
                            Picasso.with(getContext())
                                    .load(R.drawable.comfort)
                                    .into(im_sug_icon);
                            suggestion_layout.addView(view);
                        }else if(lifestyleBase.getType().equalsIgnoreCase("cw")){
                            tv_sug_comf.setText("洗车指数");
                            tv_sug_brf.setText(lifestyleBase.getBrf());
                            tv_sug_txt.setText(lifestyleBase.getTxt());
                            Picasso.with(getContext())
                                    .load(R.drawable.washcar)
                                    .into(im_sug_icon);
                            suggestion_layout.addView(view);
                        }else if(lifestyleBase.getType().equalsIgnoreCase("drsg")){
                            tv_sug_comf.setText("穿衣指数");
                            tv_sug_brf.setText(lifestyleBase.getBrf());
                            tv_sug_txt.setText(lifestyleBase.getTxt());
                            Picasso.with(getContext())
                                    .load(R.drawable.dressing)
                                    .into(im_sug_icon);
                            suggestion_layout.addView(view);
                        }else if(lifestyleBase.getType().equalsIgnoreCase("flu")){
                            tv_sug_comf.setText("感冒指数");
                            tv_sug_brf.setText(lifestyleBase.getBrf());
                            tv_sug_txt.setText(lifestyleBase.getTxt());
                            Picasso.with(getContext())
                                    .load(R.drawable.cold)
                                    .into(im_sug_icon);
                            suggestion_layout.addView(view);
                        }else if(lifestyleBase.getType().equalsIgnoreCase("sport")){
                            tv_sug_comf.setText("运动指数");
                            tv_sug_brf.setText(lifestyleBase.getBrf());
                            tv_sug_txt.setText(lifestyleBase.getTxt());
                            Picasso.with(getContext())
                                    .load(R.drawable.sport)
                                    .into(im_sug_icon);
                            suggestion_layout.addView(view);
                        }else if(lifestyleBase.getType().equalsIgnoreCase("uv")){
                            tv_sug_comf.setText("紫外线指数");
                            tv_sug_brf.setText(lifestyleBase.getBrf());
                            tv_sug_txt.setText(lifestyleBase.getTxt());
                            Picasso.with(getContext())
                                    .load(R.drawable.uv)
                                    .into(im_sug_icon);
                            suggestion_layout.addView(view);
                        }else if(lifestyleBase.getType().equalsIgnoreCase("trav")){
                            tv_sug_comf.setText("旅游指数");
                            tv_sug_brf.setText(lifestyleBase.getBrf());
                            tv_sug_txt.setText(lifestyleBase.getTxt());
                            Picasso.with(getContext())
                                    .load(R.drawable.travel)
                                    .into(im_sug_icon);
                            suggestion_layout.addView(view);
                        }
                    }
                }else {
                    MyToast.showError("life-"+lifestyle.getStatus());
                    return;
                }
            }
        });
    }

    public void setWeatherLogo(ImageView imageView,String str){
        if(str.equals("晴"))
            Picasso.with(getContext())
                .load(R.drawable.sunny)
                .into(imageView);
        else if(str.equals("阴"))
            Picasso.with(getContext())
                .load(R.drawable.overcast)
                .into(imageView);
        else if(str.equals("多云"))
            Picasso.with(getContext())
                .load(R.drawable.cloudy)
                .into(imageView);
        else if(str.equals("阵雨"))
            Picasso.with(getContext())
                .load(R.drawable.shower)
                .into(imageView);
        else if(str.equals("小雨"))
            Picasso.with(getContext())
                .load(R.drawable.light_rain)
                .into(imageView);
        else if(str.equals("中雨"))
            Picasso.with(getContext())
                .load(R.drawable.moderate_rain)
                .into(imageView);
        else if(str.equals("大雨"))
            Picasso.with(getContext())
                .load(R.drawable.heavy_rain)
                .into(imageView);
        else if(str.equals("雷阵雨"))
            Picasso.with(getContext())
                .load(R.drawable.thunder_shower)
                .into(imageView);
        else if(str.equals("雾"))
            Picasso.with(getContext())
                    .load(R.drawable.fog)
                    .into(imageView);
        else if(str.equals("小雪"))
            Picasso.with(getContext())
                    .load(R.drawable.light_snow)
                    .into(imageView);
        else if(str.equals("中雪"))
            Picasso.with(getContext())
                    .load(R.drawable.moderate_snow)
                    .into(imageView);
        else if(str.equals("大雪"))
            Picasso.with(getContext())
                    .load(R.drawable.heavy_snow)
                    .into(imageView);
        else if(str.equals("雨夹雪"))
            Picasso.with(getContext())
                    .load(R.drawable.sleet)
                    .into(imageView);
        else if(str.equals("暴风雪"))
            Picasso.with(getContext())
                    .load(R.drawable.blizzard)
                    .into(imageView);
        else if(str.equals("龙卷风"))
            Picasso.with(getContext())
                    .load(R.drawable.tornado)
                    .into(imageView);
        else if(str.equals("台风"))
            Picasso.with(getContext())
                    .load(R.drawable.typhoon)
                    .into(imageView);
        else if(str.equals("沙尘"))
            Picasso.with(getContext())
                    .load(R.drawable.sand_dust)
                    .into(imageView);
        else if(str.equals("沙尘暴"))
            Picasso.with(getContext())
                    .load(R.drawable.sand_storm)
                    .into(imageView);
    }
    @Override
    public void onClick(View view) {

    }
}
