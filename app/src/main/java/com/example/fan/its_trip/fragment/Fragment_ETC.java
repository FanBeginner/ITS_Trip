package com.example.fan.its_trip.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.adapter.BaseRecyclerAdapter;
import com.example.fan.its_trip.adapter.BaseRecyclerHolder;

import com.example.fan.its_trip.bean.CarAccountRecord;
import com.example.fan.its_trip.db.RechargeRecord;
import com.example.fan.its_trip.http.HttpRequest;
import com.example.fan.its_trip.toast.MyToast;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by Fan on 2019/10/15.
 */

public class Fragment_ETC extends Fragment implements View.OnClickListener {
    private TextView tv_etc_balance;
    private Spinner sp_etc_carNum;
    private Button btn_etc_query;
    private TextView tv_query_state;
    private Spinner sp_money;
    private Button btn_recharge;
    private TextView tv_recharge_state;
    private Spinner sp_record_carNum;
    private RadioButton rb_record_asc;
    private RadioButton rb_record_des;
    private Button btn_record_query;
    private RecyclerView etc_recyclerView;
    private static final String TAG = "Fragment_ETC";
    private int rechargeMoney=0;
    private List<RechargeRecord>recordList=new ArrayList<>();
    private Gson gson;
    private BaseRecyclerAdapter<RechargeRecord> adapter;
    private Comparator<RechargeRecord> comparator;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_etc, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_etc_balance = (TextView) view.findViewById(R.id.tv_etc_balance);
        sp_etc_carNum = (Spinner) view.findViewById(R.id.sp_etc_carNum);
        btn_etc_query = (Button) view.findViewById(R.id.btn_etc_query);
        tv_query_state = (TextView) view.findViewById(R.id.tv_query_state);
        sp_money = (Spinner) view.findViewById(R.id.sp_money);
        btn_recharge = (Button) view.findViewById(R.id.btn_recharge);
        tv_recharge_state = (TextView) view.findViewById(R.id.tv_recharge_state);
        sp_record_carNum = (Spinner) view.findViewById(R.id.sp_record_carNum);
        rb_record_asc = (RadioButton) view.findViewById(R.id.rb_record_asc);
        rb_record_des = (RadioButton) view.findViewById(R.id.rb_record_des);
        btn_record_query = (Button) view.findViewById(R.id.btn_record_query);
        etc_recyclerView = (RecyclerView) view.findViewById(R.id.etc_recyclerView);

        btn_etc_query.setOnClickListener(this);
        btn_recharge.setOnClickListener(this);
        btn_record_query.setOnClickListener(this);

        gson=new Gson();

        sp_money.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        rechargeMoney=50;
                        break;
                    case 1:
                        rechargeMoney=100;
                        break;
                    case 2:
                        rechargeMoney=150;
                        break;
                    case 3:
                        rechargeMoney=200;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        recordList=LitePal.findAll(RechargeRecord.class);
        adapter=new BaseRecyclerAdapter<RechargeRecord>(getContext(),recordList,R.layout.adp_etc_record) {
            @Override
            public void convert(BaseRecyclerHolder holder, RechargeRecord item, int position, boolean isScrolling) {
                holder.setText(R.id.adp_record_num,""+(position+1));
                holder.setText(R.id.adp_record_carId,""+item.getCarId());
                holder.setText(R.id.adp_record_money,""+item.getCost());
                holder.setText(R.id.adp_record_user,item.getOperator());
                holder.setText(R.id.adp_record_time,item.getTime());
            }
        };
        etc_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        etc_recyclerView.setAdapter(adapter);
        rb_record_asc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setSort(recordList,0);
                }
            }
        });
        rb_record_des.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setSort(recordList,1);
                }
            }
        });
    }
    public void getCarBalance(int carId){
        JSONObject js=new JSONObject();
        try {
            js.put("CarId",carId);
            js.put("UserName",HttpRequest.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.request("get_car_account_balance", js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if(jsonObject.getString("RESULT").equals("S")){
                        tv_etc_balance.setText(jsonObject.getString("Balance")+"元");
                        tv_query_state.setText("查询成功");
                    }else if(jsonObject.getString("RESULT").equals("F")){
                        tv_query_state.setText("查询失败");
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
    public void setCarRecharge(final int carId, final int money){
        JSONObject js=new JSONObject();
        try {
            js.put("CarId",carId);
            js.put("Money",money);
            js.put("UserName",HttpRequest.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.request("set_car_account_recharge", js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if(jsonObject.getString("RESULT").equals("S")){
                        tv_recharge_state.setText("充值成功");
                        RechargeRecord record=new RechargeRecord();
                        record.setCarId(carId);
                        record.setCost(money);
                        record.setOperator(HttpRequest.getUserName());
                        record.setTime(HttpRequest.getTime());
                        record.save();
                    }else if(jsonObject.getString("RESULT").equals("F")){
                        tv_recharge_state.setText("充值失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                MyToast.showError("获取失败");
            }
        });
    }

    //0升序，1降序
    public void setSort(List<RechargeRecord> list,int mode){
        switch (mode){
            case 0:
                comparator=new Comparator<RechargeRecord>() {
                    @Override
                    public int compare(RechargeRecord o1, RechargeRecord o2) {
                        long time1=0;
                        long time2=0;
                        try {
                            time1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(o1.getTime()).getTime();
                            time2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(o2.getTime()).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(time1>time2)
                            return 1;
                        else if(time1==time2)
                            return 0;
                        else
                            return -1;
                    }
                };
                break;
            case 1:
                comparator=new Comparator<RechargeRecord>() {
                    @Override
                    public int compare(RechargeRecord o1, RechargeRecord o2) {
                        long time1=0;
                        long time2=0;
                        try {
                            time1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(o1.getTime()).getTime();
                            time2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(o2.getTime()).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(time1>time2)
                            return -1;
                        else if(time1==time2)
                            return 0;
                        else
                            return 1;
                    }
                };
                break;
        }
        Collections.sort(list,comparator);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_etc_query:
                getCarBalance(sp_etc_carNum.getSelectedItemPosition()+1);
                break;
            case R.id.btn_recharge:
                setCarRecharge(sp_etc_carNum.getSelectedItemPosition()+1,rechargeMoney);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getCarBalance(sp_etc_carNum.getSelectedItemPosition()+1);
                    }
                }).start();
                break;
            case R.id.btn_record_query:
                recordList.clear();
                String carId=String.valueOf(sp_record_carNum.getSelectedItemPosition()+1);
                recordList.addAll(LitePal.where("CarId=?",carId).find(RechargeRecord.class));
                adapter.notifyDataSetChanged();
                break;
        }
    }
}
