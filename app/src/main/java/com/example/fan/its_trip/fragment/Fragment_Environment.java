package com.example.fan.its_trip.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.fan.its_trip.App;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.http.HttpRequest;
import com.example.fan.its_trip.toast.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Fan on 2019/10/12.
 */

public class Fragment_Environment extends Fragment implements View.OnClickListener {
    private TextView tv_env_tem;
    private TextView tv_env_hum;
    private TextView tv_env_light;
    private TextView tv_env_pm;
    private TextView tv_env_co2;
    private TextView tv_env_title;
    private Button btn_env_threshold;
    private static final String TAG = "Fragment_Environment";
    int tem_limit, hum_limit, light_limit, pm_limit, co2_limit, tem, hum, light, pm, co2;
    private SharedPreferences pref = App.appContext.getSharedPreferences("data", Context.MODE_PRIVATE);
    private Timer timer;
    private Switch sw_autowarn;
    private EditText ed_msg_tem;
    private EditText ed_msg_hum;
    private EditText ed_msg_light;
    private EditText ed_msg_co2;
    private EditText ed_msg_pm;
    private Button btn_threshold_save;
    private Button btn_threshold_close;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_environment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_env_tem = (TextView) view.findViewById(R.id.tv_env_tem);
        tv_env_hum = (TextView) view.findViewById(R.id.tv_env_hum);
        tv_env_light = (TextView) view.findViewById(R.id.tv_env_light);
        tv_env_pm = (TextView) view.findViewById(R.id.tv_env_pm);
        tv_env_co2 = (TextView) view.findViewById(R.id.tv_env_co2);
        tv_env_title = (TextView) view.findViewById(R.id.tv_env_title);
        btn_env_threshold = (Button) view.findViewById(R.id.btn_env_threshold);

        btn_env_threshold.setOnClickListener(this);
        tv_env_tem.setOnClickListener(this);
        tv_env_hum.setOnClickListener(this);
        tv_env_light.setOnClickListener(this);
        tv_env_pm.setOnClickListener(this);
        tv_env_co2.setOnClickListener(this);
        tv_env_title.setSelected(true);

        tem_limit = pref.getInt("Tem", 21);
        hum_limit = pref.getInt("Hum", 70);
        light_limit = pref.getInt("Light", 3000);
        pm_limit = pref.getInt("PM", 200);
        co2_limit = pref.getInt("CO2", 8000);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getAllSense();
            }
        }, 50, 3000);
    }

    private void getAllSense() {
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
                        tv_env_tem.setText("" + tem);
                        tv_env_hum.setText("" + hum);
                        tv_env_light.setText("" + light);
                        tv_env_pm.setText("" + pm);
                        tv_env_co2.setText("" + co2);
                        if (tem > tem_limit) {
                            tv_env_tem.setBackgroundResource(R.drawable.bg_circle_red);
                        } else {
                            tv_env_tem.setBackgroundResource(R.drawable.bg_circle);
                        }
                        if (hum > hum_limit) {
                            tv_env_hum.setBackgroundResource(R.drawable.bg_circle_red);
                        } else {
                            tv_env_hum.setBackgroundResource(R.drawable.bg_circle);
                        }
                        if (light > light_limit) {
                            tv_env_light.setBackgroundResource(R.drawable.bg_circle_red);
                        } else {
                            tv_env_light.setBackgroundResource(R.drawable.bg_circle);
                        }
                        if (pm > pm_limit) {
                            tv_env_pm.setBackgroundResource(R.drawable.bg_circle_red);
                        } else {
                            tv_env_pm.setBackgroundResource(R.drawable.bg_circle);
                        }
                        if (co2 > co2_limit) {
                            tv_env_co2.setBackgroundResource(R.drawable.bg_circle_red);
                        } else {
                            tv_env_co2.setBackgroundResource(R.drawable.bg_circle);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                MyToast.showError("获取失败！");
            }
        });
    }

    private void showThreshold() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_threshold, null);
        sw_autowarn = (Switch) view.findViewById(R.id.sw_autowarn);
        ed_msg_tem = (EditText) view.findViewById(R.id.ed_msg_tem);
        ed_msg_hum = (EditText) view.findViewById(R.id.ed_msg_hum);
        ed_msg_light = (EditText) view.findViewById(R.id.ed_msg_light);
        ed_msg_co2 = (EditText) view.findViewById(R.id.ed_msg_co2);
        ed_msg_pm = (EditText) view.findViewById(R.id.ed_msg_pm);
        btn_threshold_save = (Button) view.findViewById(R.id.btn_threshold_save);
        btn_threshold_close = (Button) view.findViewById(R.id.btn_threshold_close);

        ed_msg_tem.setText(""+pref.getInt("Tem",21));
        ed_msg_hum.setText(""+pref.getInt("Hum",70));
        ed_msg_light.setText(""+pref.getInt("Light",3000));
        ed_msg_co2.setText(""+pref.getInt("CO2",8000));
        ed_msg_pm.setText(""+pref.getInt("PM",200));
        ed_msg_tem.setEnabled(false);
        ed_msg_hum.setEnabled(false);
        ed_msg_light.setEnabled(false);
        ed_msg_co2.setEnabled(false);
        ed_msg_pm.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("阈值设置");
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        btn_threshold_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tem = ed_msg_tem.getText().toString().trim();
                String Hum = ed_msg_hum.getText().toString().trim();
                String Light = ed_msg_light.getText().toString().trim();
                String Co2 = ed_msg_co2.getText().toString().trim();
                String Pm = ed_msg_pm.getText().toString().trim();
                if (Tem.isEmpty() || Hum.isEmpty() || Light.isEmpty() || Co2.isEmpty() || Pm.isEmpty()) {
                    MyToast.showInfo("请输入值！");
                } else {
                    tem_limit = Integer.valueOf(Tem);
                    hum_limit = Integer.valueOf(Hum);
                    light_limit = Integer.valueOf(Light);
                    co2_limit = Integer.valueOf(Co2);
                    pm_limit = Integer.valueOf(Pm);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("Tem", tem_limit);
                    editor.putInt("Hum", hum_limit);
                    editor.putInt("Light", light_limit);
                    editor.putInt("CO2", co2_limit);
                    editor.putInt("PM", pm_limit);
                    editor.apply();
                    MyToast.showSuccess("保存成功！");
                }
            }
        });
        btn_threshold_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sw_autowarn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ed_msg_tem.setEnabled(false);
                    ed_msg_hum.setEnabled(false);
                    ed_msg_light.setEnabled(false);
                    ed_msg_co2.setEnabled(false);
                    ed_msg_pm.setEnabled(false);
                } else {
                    ed_msg_tem.setEnabled(true);
                    ed_msg_hum.setEnabled(true);
                    ed_msg_light.setEnabled(true);
                    ed_msg_co2.setEnabled(true);
                    ed_msg_pm.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_env_threshold:
                showThreshold();
                break;
            case R.id.tv_env_tem:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_EnvironmentLine()).commit();
                Fragment_EnvironmentLine.page=0;
                break;
            case R.id.tv_env_hum:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_EnvironmentLine()).commit();
                Fragment_EnvironmentLine.page=1;
                break;
            case R.id.tv_env_light:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_EnvironmentLine()).commit();
                Fragment_EnvironmentLine.page=2;
                break;
            case R.id.tv_env_pm:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_EnvironmentLine()).commit();
                Fragment_EnvironmentLine.page=3;
                break;
            case R.id.tv_env_co2:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_EnvironmentLine()).commit();
                Fragment_EnvironmentLine.page=4;
                break;
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

}
