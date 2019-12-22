package com.example.fan.its_trip.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.example.fan.its_trip.App;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.bean.ChartBean;
import com.example.fan.its_trip.http.HttpRequest;
import com.github.mikephil.charting.charts.LineChart;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/4/26 0026.
 */

public class Fragment_EnvironmentLine extends Fragment implements ViewPager.OnPageChangeListener {
    private ViewPager env_viewPager;
    private LinearLayout layout_point;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentPagerAdapter adapter;
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    public static int page = 0;
    public int tem, hum, light, co2, pm;
    public static Handler temHandler = new Handler();
    public static Handler humHandler = new Handler();
    public static Handler lightHandler = new Handler();
    public static Handler co2Handler = new Handler();
    public static Handler pmHandler = new Handler();
    public static List<Integer> temList = new ArrayList<>();
    public static List<Integer> humList = new ArrayList<>();
    public static List<Integer> lightList = new ArrayList<>();
    public static List<Integer> co2List = new ArrayList<>();
    public static List<Integer> pmList = new ArrayList<>();
    public static List<String> timeList = new ArrayList<>();
    private ImageView im_back;
    public static SharedPreferences pref = App.appContext.getSharedPreferences("data", Context.MODE_PRIVATE);
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_environment_line, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        env_viewPager = (ViewPager) view.findViewById(R.id.env_viewPager);
        layout_point = (LinearLayout) view.findViewById(R.id.layout_point);
        im_back = (ImageView) view.findViewById(R.id.im_envline_back);
        im_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Main, new Fragment_Environment()).commit();
            }
        });

        fragmentList.add(new temFragment());
        fragmentList.add(new humFragment());
        fragmentList.add(new lightFragment());
        fragmentList.add(new pmFragment());
        fragmentList.add(new co2Fragment());
        adapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        env_viewPager.setAdapter(adapter);
        env_viewPager.setOffscreenPageLimit(fragmentList.size());
        env_viewPager.addOnPageChangeListener(this);
        env_viewPager.setCurrentItem(page);

        setTip(page);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getAllSense();
                    }
                });
            }
        }, 50, 3000);
    }

    public void getAllSense() {
        JSONObject js = new JSONObject();
        try {
            js.put("UserName", HttpRequest.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.request("get_all_sense", js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.get("RESULT").equals("S")) {
                        tem = Integer.valueOf(jsonObject.get("temperature").toString());
                        hum = Integer.valueOf(jsonObject.get("humidity").toString());
                        light = Integer.valueOf(jsonObject.get("LightIntensity").toString());
                        pm = Integer.valueOf(jsonObject.get("pm2.5").toString());
                        co2 = Integer.valueOf(jsonObject.get("co2").toString());
                        String time = new SimpleDateFormat("HH:mm:ss").format(new Date().getTime()).toString();

                        setValue(tem, temList, temHandler);
                        setValue(hum, humList, humHandler);
                        setValue(light, lightList, lightHandler);
                        setValue(co2, co2List, co2Handler);
                        setValue(pm, pmList, pmHandler);
                        if (timeList.size() == 10) {
                            timeList.remove(0);
                            timeList.add(9, time);
                        } else {
                            timeList.add(time);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    public void setValue(int value, List<Integer> list, Handler handler) {
        if (list.size() == 10) {
            list.remove(0);
            list.add(9, value);
        } else {
            list.add(value);
        }
        if (handler != null) {
            handler.sendEmptyMessage(1);
        }
    }

    public static float countAverage(List<Integer> list) {
        float average = 0, total = 0;
        for (int i = 0; i < list.size(); i++) {
            total += list.get(i);
        }
        average = total / list.size();
        return average;
    }

    public void setTip(int pos) {
        layout_point.removeAllViews();
        for (int i = 0; i < fragmentList.size(); i++) {
            ImageView imageView = new ImageView(getActivity());
            if (i == pos) {
                imageView.setImageResource(R.drawable.select);
            } else {
                imageView.setImageResource(R.drawable.unselect);
            }
            layout_point.addView(imageView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setTip(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class temFragment extends Fragment {
        private LineChart line_tem;
        private ChartBean bean = new ChartBean(10);
        private TextView tv_average_tem;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fg_tem, container, false);
            initView(view);
            return view;
        }

        private void initView(View view) {
            line_tem = (LineChart) view.findViewById(R.id.line_tem);

            bean.getLineSet(line_tem);
            temHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    bean.putData(timeList, temList, pref.getInt("Tem", 21));
                    line_tem.setData(bean.getLineData());
                    line_tem.invalidate();
                    tv_average_tem.setText("平均温度：" + Fragment_EnvironmentLine.countAverage(temList) + "℃");
                }
            };
            tv_average_tem = (TextView) view.findViewById(R.id.tv_average_tem);
        }
    }

    public static class humFragment extends Fragment {
        private LineChart line_hum;
        private ChartBean bean = new ChartBean(10);
        private TextView tv_average_hum;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fg_hum, container, false);
            initView(view);
            return view;
        }

        private void initView(View view) {
            line_hum = (LineChart) view.findViewById(R.id.line_hum);

            bean.getLineSet(line_hum);
            humHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    bean.putData(timeList, humList, pref.getInt("Hum", 70));
                    line_hum.setData(bean.getLineData());
                    line_hum.invalidate();
                    tv_average_hum.setText("平均湿度：" + Fragment_EnvironmentLine.countAverage(humList) + "%");
                }
            };
            tv_average_hum = (TextView) view.findViewById(R.id.tv_average_hum);
        }
    }

    public static class lightFragment extends Fragment {
        private LineChart line_light;
        private ChartBean bean = new ChartBean(10);
        private TextView tv_average_light;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fg_light, container, false);
            initView(view);
            return view;
        }

        private void initView(View view) {
            line_light = (LineChart) view.findViewById(R.id.line_light);

            bean.getLineSet(line_light);
            lightHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    bean.putData(timeList, lightList, pref.getInt("Light", 3000));
                    line_light.setData(bean.getLineData());
                    line_light.invalidate();
                    tv_average_light.setText("平均光照强度：" + Fragment_EnvironmentLine.countAverage(lightList) + "lux");

                }
            };
            tv_average_light = (TextView) view.findViewById(R.id.tv_average_light);
        }
    }

    public static class co2Fragment extends Fragment {
        private LineChart line_co2;
        private ChartBean bean = new ChartBean(10);
        private TextView tv_average_co2;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fg_co2, container, false);
            initView(view);
            return view;
        }

        private void initView(View view) {
            line_co2 = (LineChart) view.findViewById(R.id.line_co2);

            bean.getLineSet(line_co2);
            co2Handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    bean.putData(timeList, co2List, pref.getInt("CO2", 8000));
                    line_co2.setData(bean.getLineData());
                    line_co2.invalidate();
                    tv_average_co2.setText("平均二氧化碳值：" + Fragment_EnvironmentLine.countAverage(co2List) + "kgmol");

                }
            };
            tv_average_co2 = (TextView) view.findViewById(R.id.tv_average_co2);
        }
    }

    public static class pmFragment extends Fragment {
        private LineChart line_pm;
        private ChartBean bean = new ChartBean(10);
        private TextView tv_average_pm;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fg_pm, container, false);
            initView(view);
            return view;
        }

        private void initView(View view) {
            line_pm = (LineChart) view.findViewById(R.id.line_pm);

            bean.getLineSet(line_pm);
            pmHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    bean.putData(timeList, pmList, pref.getInt("PM", 200));
                    line_pm.setData(bean.getLineData());
                    line_pm.invalidate();
                    tv_average_pm.setText("平均温度：" + Fragment_EnvironmentLine.countAverage(pmList) + "μg/m³");
                }
            };
            tv_average_pm = (TextView) view.findViewById(R.id.tv_average_pm);
        }
    }
}
