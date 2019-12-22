package com.example.fan.its_trip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.fan.its_trip.R;
import com.example.fan.its_trip.activity.TravelDetailActivity;
import com.example.fan.its_trip.adapter.BaseRecyclerAdapter;
import com.example.fan.its_trip.adapter.BaseRecyclerHolder;
import com.example.fan.its_trip.bean.TravelInfo;
import com.example.fan.its_trip.toast.MyToast;
import com.example.fan.its_trip.utils.GlideImageLoader;
import com.example.fan.its_trip.utils.HttpUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by Fan on 2019/10/27.
 */

public class Fragment_Travel extends Fragment {


    private Banner banner;
    private RecyclerView rc_travel;
    private List<Integer> imageResIds = Arrays.asList(R.drawable.ad_1, R.drawable.ad_2, R.drawable.ad_3,
            R.drawable.ad_4, R.drawable.ad_5);
    private BaseRecyclerAdapter<TravelInfo>adapter;
    private List<TravelInfo> list=new ArrayList<>();
    private Handler handler;
    private NestedScrollView travel_srollView;
    private static final String TAG = "Fragment_Travel";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_travel, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {
        banner = (Banner) view.findViewById(R.id.banner);
        rc_travel = (RecyclerView) view.findViewById(R.id.rc_travel);

        travel_srollView=view.findViewById(R.id.travel_srollView);
        rc_travel.setHasFixedSize(true);
        travel_srollView.setNestedScrollingEnabled(false);

        banner.setImageLoader(new GlideImageLoader());
        banner.setImages(imageResIds);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.RotateDown);
        banner.start();

        adapter=new BaseRecyclerAdapter<TravelInfo>(getContext(),list,R.layout.adp_travel_all) {
            @Override
            public void convert(BaseRecyclerHolder holder, TravelInfo item, int position, boolean isScrolling) {
                holder.setText(R.id.item_travel_name,item.getLocation());
                ImageView imageView=holder.getView(R.id.item_travel_pic);
                Picasso.with(getContext())
                        .load("http://192.168.43.92:8089/TravelSystem_By_Xww/"+item.getImgs())
                        .into(imageView);
            }
        };
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, final int position) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(list.get(position).getLocation());
                    }
                },500);
                startActivity(new Intent(getContext(), TravelDetailActivity.class));
            }
        });
        rc_travel.setLayoutManager(new GridLayoutManager(getContext(),2));
        rc_travel.setAdapter(adapter);
        getTravelInfo();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                adapter.notifyDataSetChanged();
            }
        };
    }

    public void getTravelInfo(){
        HttpUtil.sendOkHttpRequest("http://192.168.43.92:8089/TravelSystem_By_Xww/GetTravelInfoAll.do", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //MyToast.showError(""+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                list.clear();
                String responseText = response.body().string();
                Log.i(TAG, "---onResponse: "+responseText);
                try {
                    JSONArray array=new JSONArray(responseText);
                    for(int i=0;i<array.length();i++){
                        list.add(new Gson().fromJson(array.get(i).toString(),TravelInfo.class));
                    }
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //如果你需要考虑更好的体验，可以这么操作
    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }
}
