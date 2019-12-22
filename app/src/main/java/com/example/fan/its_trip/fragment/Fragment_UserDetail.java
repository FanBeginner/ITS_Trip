package com.example.fan.its_trip.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.adapter.BaseRecyclerAdapter;
import com.example.fan.its_trip.adapter.BaseRecyclerHolder;
import com.example.fan.its_trip.bean.CarInfo;
import com.example.fan.its_trip.bean.CarPecInfo;
import com.example.fan.its_trip.bean.CarPecType;
import com.example.fan.its_trip.bean.UserInfo;
import com.example.fan.its_trip.bean.UserInfoEvent;
import com.example.fan.its_trip.http.HttpRequest;
import com.example.fan.its_trip.toast.MyToast;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fan on 2019/9/30.
 */

public class Fragment_UserDetail extends Fragment implements View.OnClickListener {
    private ImageView detail_image;
    private TextView tv_detail_name;
    private TextView tv_detail_sex;
    private TextView tv_detail_tel;
    private TextView tv_detail_tip;
    private String UserName, Name, Sex, Tel,CardId;
    private Button btn_detail_back;
    private List<CarInfo>carInfoList=new ArrayList<>();
    private List<CarInfo>detailList=new ArrayList<>();
    private List<CarPecInfo>carPecInfoList=new ArrayList<>();
    private List<CarPecInfo>pecList=new ArrayList<>();
    private List<CarPecType>carPecTypeList=new ArrayList<>();
    private List<CarPecType>typeList=new ArrayList<>();
    private RecyclerView recycl_userDetail;
    private BaseRecyclerAdapter<CarInfo> adapter;
    private Gson gson;
    private Handler handlerCarInfo=new Handler();
    private Handler handlerCarPec=new Handler();
    private Handler handlerType=new Handler();
    private static final String TAG = "Fragment_UserDetail";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_userdetail, container, false);
        EventBus.getDefault().register(this);
        initView(view);
        return view;
    }

    private void initView(View view) {
        detail_image = (ImageView) view.findViewById(R.id.detail_image);
        tv_detail_name = (TextView) view.findViewById(R.id.tv_detail_name);
        tv_detail_sex = (TextView) view.findViewById(R.id.tv_detail_sex);
        tv_detail_tel = (TextView) view.findViewById(R.id.tv_detail_tel);
        tv_detail_tip = (TextView) view.findViewById(R.id.tv_detail_tip);
        recycl_userDetail=view.findViewById(R.id.recycl_userDetail);

        btn_detail_back = (Button) view.findViewById(R.id.btn_detail_back);
        btn_detail_back.setOnClickListener(this);

        gson=new Gson();

    }

    @Subscribe
    public void onEventBus(UserInfoEvent ss) {
        UserName=ss.getUsername();
        Sex=ss.getPsex();
        CardId=ss.getPcardid();
        tv_detail_name.setText(ss.getPname());
        tv_detail_sex.setText(Sex);
        tv_detail_tel.setText(ss.getPtel());
        if(Sex.equals("男"))
            detail_image.setImageResource(R.drawable.touxiang_2);
        else
            detail_image.setImageResource(R.drawable.touxiang_1);

        getCarInfo();
        handlerCarPec=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                getAllCarPecInfo();
            }
        };
        handlerType=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                getCarType();
            }
        };

        adapter=new BaseRecyclerAdapter<CarInfo>(getContext(),detailList,R.layout.adp_user_detail) {
            @Override
            public void convert(BaseRecyclerHolder holder, CarInfo item, int position, boolean isScrolling) {
                setCarLogo((ImageView) holder.getView(R.id.im_carLogo),detailList.get(position).getCarbrand());
                holder.setText(R.id.tv_detail_carId,item.getCarnumber());
                Log.e(TAG, "--detail: "+detailList.size());
                Log.e(TAG, "--pec: "+pecList.size());
                Log.e(TAG, "--carPec: "+carPecInfoList.size());
                for(int i=0;i<pecList.size();i++){
                    if(pecList.get(i).getCarnumber().equals(item.getCarnumber())) {
                        holder.setText(R.id.tv_detail_addr, pecList.get(i).getPaddr());
                        holder.setText(R.id.tv_detail_time, pecList.get(i).getPdatetime());
                        for(int j=0;j<typeList.size();j++){
                            if(typeList.get(j).getPcode().equals(pecList.get(i).getPcode())) {
                                holder.setText(R.id.tv_detail_cause, typeList.get(i).getPremarks());
                                holder.setText(R.id.tv_detail_money, ""+typeList.get(i).getPmoney());
                                holder.setText(R.id.tv_detail_score, ""+typeList.get(i).getPscore());
                                holder.setText(R.id.tv_detail_status, "未处理");
                            }
                        }
                    }
                    else{
                        holder.setText(R.id.tv_detail_addr,"无");
                        holder.setText(R.id.tv_detail_time,"无");
                        holder.setText(R.id.tv_detail_cause,"无");
                        holder.setText(R.id.tv_detail_money,"0");
                        holder.setText(R.id.tv_detail_score,"0");
                        holder.setText(R.id.tv_detail_status,"无");
                    }

                }

            }
        };
        recycl_userDetail.setLayoutManager(new LinearLayoutManager(getContext()));
        recycl_userDetail.setAdapter(adapter);
    }
    public void getCarInfo(){
        JSONObject js=new JSONObject();
        try {
            js.put("UserName",HttpRequest.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.request("get_car_info", js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG, "---carInfo: "+jsonObject );
                try {
                    if (jsonObject.getString("ERRMSG").equals("成功")) {
                        JSONArray array = new JSONArray(jsonObject.getString("ROWS_DETAIL"));
                        for (int i = 0; i < array.length(); i++) {
                            carInfoList.add(gson.fromJson(array.get(i).toString(), CarInfo.class));
                        }
                        Log.e(TAG, "--carList: "+carInfoList.size() );
                        for(int i=0;i<carInfoList.size();i++){
                            if(carInfoList.get(i).getPcardid().equals(CardId)){
                                detailList.add(carInfoList.get(i));
                            }
                        }
                        if(detailList.size()>0)
                            tv_detail_tip.setVisibility(View.GONE);
                        else
                            tv_detail_tip.setVisibility(View.VISIBLE);
                        handlerCarPec.sendEmptyMessage(1);
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
    public void getAllCarPecInfo(){
        JSONObject js=new JSONObject();
        try {
            js.put("UserName",HttpRequest.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.request("get_all_car_peccancy", js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG, "---carPec: "+jsonObject );
                try {
                    if (jsonObject.getString("ERRMSG").equals("成功")) {
                        JSONArray array = new JSONArray(jsonObject.getString("ROWS_DETAIL"));
                        for (int i = 0; i < array.length(); i++) {
                            carPecInfoList.add(gson.fromJson(array.get(i).toString(), CarPecInfo.class));
                        }
                        Log.e(TAG, "--carPec: "+carPecInfoList.size() );
                        for(int i=0;i<detailList.size();i++){
                            for(int j=0;j<carPecInfoList.size();j++){
                                if(detailList.get(i).getCarnumber().equals(carPecInfoList.get(j).getCarnumber())){
                                    pecList.add(carPecInfoList.get(j));
                                }
                            }
                        }
                        handlerType.sendEmptyMessage(1);
                        for(int i=0;i<pecList.size();i++){
                            Log.e(TAG, "--onResponse: "+pecList.get(i).getPaddr() );
                            Log.e(TAG, "--onResponse: "+pecList.get(i).getCarnumber() );
                            Log.e(TAG, "--onResponse: "+pecList.get(i).getPcode() );
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
    public void getCarType(){
        JSONObject js=new JSONObject();
        try {
            js.put("UserName",HttpRequest.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.request("get_peccancy_type", js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG, "---carType: "+jsonObject );
                try {
                    if (jsonObject.getString("ERRMSG").equals("成功")) {
                        JSONArray array = new JSONArray(jsonObject.getString("ROWS_DETAIL"));
                        for (int i = 0; i < array.length(); i++) {
                            carPecTypeList.add(gson.fromJson(array.get(i).toString(), CarPecType.class));
                        }
                        Log.e(TAG, "--111carType: "+carPecTypeList.size() );
                        for(int i=0;i<pecList.size();i++){
                            for(int j=0;j<carPecTypeList.size();j++){
                                if(pecList.get(i).getPcode().equals(carPecTypeList.get(j).getPcode())){
                                    typeList.add(carPecTypeList.get(j));
                                }
                            }
                        }
                        Log.e(TAG, "--111Typelist: "+typeList.size() );
                        adapter.notifyDataSetChanged();
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
    public void setCarLogo(ImageView imageView,String logoName){
        if(logoName.equals("audi"))
            imageView.setImageResource(R.drawable.audi);
        else if(logoName.equals("baoma"))
            imageView.setImageResource(R.drawable.baoma);
        else if(logoName.equals("benchi"))
            imageView.setImageResource(R.drawable.benchi);
        else if(logoName.equals("bentian"))
            imageView.setImageResource(R.drawable.bentian);
        else if(logoName.equals("bieke"))
            imageView.setImageResource(R.drawable.bieke);
        else if(logoName.equals("biyadi"))
            imageView.setImageResource(R.drawable.biyadi);
        else if(logoName.equals("dazhong"))
            imageView.setImageResource(R.drawable.dazhong);
        else if(logoName.equals("fengtian"))
            imageView.setImageResource(R.drawable.fengtian);
        else if(logoName.equals("fute"))
            imageView.setImageResource(R.drawable.fute);
        else if(logoName.equals("mazhida"))
            imageView.setImageResource(R.drawable.mazhida);
        else if(logoName.equals("qirui"))
            imageView.setImageResource(R.drawable.qirui);
        else if(logoName.equals("richan"))
            imageView.setImageResource(R.drawable.richan);
        else if(logoName.equals("sanling"))
            imageView.setImageResource(R.drawable.sanling);
        else if(logoName.equals("sibalu"))
            imageView.setImageResource(R.drawable.sibalu);
        else if(logoName.equals("tesila"))
            imageView.setImageResource(R.drawable.tesila);
        else if(logoName.equals("voervo"))
            imageView.setImageResource(R.drawable.voervo);
        else if(logoName.equals("xiandai"))
            imageView.setImageResource(R.drawable.xiandai);
        else if(logoName.equals("xuefulan"))
            imageView.setImageResource(R.drawable.xuefulan);
        else if(logoName.equals("zhonghua"))
            imageView.setImageResource(R.drawable.zhonghua);
        else if(logoName.equals("biaozhi"))
            imageView.setImageResource(R.drawable.biaozhi);
        else
            imageView.setImageResource(R.drawable.ic_help_outline);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_detail_back:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_User()).commit();
                break;
        }
    }
}
