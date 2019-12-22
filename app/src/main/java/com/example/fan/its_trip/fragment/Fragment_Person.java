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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.adapter.BaseRecyclerAdapter;
import com.example.fan.its_trip.adapter.BaseRecyclerHolder;
import com.example.fan.its_trip.bean.CarInfo;
import com.example.fan.its_trip.bean.UserInfo;
import com.example.fan.its_trip.http.HttpRequest;
import com.example.fan.its_trip.toast.MyToast;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fan on 2019/10/14.
 */

public class Fragment_Person extends Fragment {
    private TextView tv_pec_user;
    private TextView tv_pec_name;
    private TextView tv_pec_sex;
    private TextView tv_pec_tel;
    private TextView tv_pec_idCard;
    private TextView tv_pec_time;
    private ImageView im_pec;
    private RecyclerView pec_recyclerView;
    private BaseRecyclerAdapter<CarInfo>adapter;
    private List<CarInfo> carInfoList=new ArrayList<>();
    private List<CarInfo>detailList=new ArrayList<>();
    private Gson gson;
    private String CardId;
    private static final String TAG = "Fragment_Person";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_person, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_pec_user = (TextView) view.findViewById(R.id.tv_pec_user);
        tv_pec_name = (TextView) view.findViewById(R.id.tv_pec_name);
        tv_pec_sex = (TextView) view.findViewById(R.id.tv_pec_sex);
        tv_pec_tel = (TextView) view.findViewById(R.id.tv_pec_tel);
        tv_pec_idCard = (TextView) view.findViewById(R.id.tv_pec_idCard);
        tv_pec_time = (TextView) view.findViewById(R.id.tv_pec_time);
        im_pec = (ImageView) view.findViewById(R.id.im_pec);
        pec_recyclerView = (RecyclerView) view.findViewById(R.id.pec_recyclerView);

        gson=new Gson();
        getUserInfo();
        getCarInfo();
        adapter=new BaseRecyclerAdapter<CarInfo>(getContext(),detailList,R.layout.adp_pec) {
            @Override
            public void convert(BaseRecyclerHolder holder, CarInfo item, int position, boolean isScrolling) {
                holder.setText(R.id.adp_pec_carId,item.getCarnumber());
                holder.setText(R.id.adp_pec_buyDate,item.getBuydate());
                setCarLogo((ImageView) holder.getView(R.id.im_pec_carLogo),detailList.get(position).getCarbrand());
            }
        };
        pec_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pec_recyclerView.setAdapter(adapter);
    }
    private void getUserInfo(){
        JSONObject js=new JSONObject();
        try {
            js.put("UserName", HttpRequest.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.request("get_all_user_info", js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG, "onResponse: "+jsonObject );
                try {
                    if (jsonObject.getString("ERRMSG").equals("成功")) {
                        JSONArray array = new JSONArray(jsonObject.getString("ROWS_DETAIL"));
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object=new JSONObject(array.get(i).toString());
                            if(object.getString("username").equals(HttpRequest.getUserName())){
                                tv_pec_user.setText(object.getString("username"));
                                tv_pec_name.setText(object.getString("pname"));
                                tv_pec_sex.setText(object.getString("psex"));
                                tv_pec_tel.setText(object.getString("ptel"));
                                tv_pec_idCard.setText(object.getString("pcardid"));
                                tv_pec_time.setText(object.getString("pregisterdate"));
                                CardId=object.getString("pcardid");
                                if(object.getString("psex").equals("男")){
                                    Picasso.with(getContext())
                                            .load(R.drawable.touxiang_2)
                                            .into(im_pec);
                                }else{
                                    Picasso.with(getContext())
                                            .load(R.drawable.touxiang_1)
                                            .into(im_pec);
                                }
                            }
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
}
