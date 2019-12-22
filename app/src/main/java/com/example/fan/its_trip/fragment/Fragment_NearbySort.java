package com.example.fan.its_trip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.fan.its_trip.R;
import com.example.fan.its_trip.activity.NearbyActivity;
import com.example.fan.its_trip.adapter.BaseRecyclerAdapter;
import com.example.fan.its_trip.adapter.BaseRecyclerHolder;
import com.example.fan.its_trip.bean.UserInfoEvent;
import com.example.fan.its_trip.toast.MyToast;
import com.example.fan.its_trip.view.ZoomScrollView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Fan on 2019/10/23.
 */

public class Fragment_NearbySort extends Fragment {
    private RecyclerView rc_nearbySort;
    private BaseRecyclerAdapter<String> adapter;
    private ZoomScrollView scrollView_nearbysort;
    private List<String> strList=Arrays.asList("美食","中餐","西餐","火锅","咖啡厅","肯德基","自助餐","小吃","麦当劳","海鲜"
            ,"酒店","快捷酒店","宾馆","青年旅社","星级酒店","主题酒店","招待所","商务酒店","家庭旅馆","快捷连锁"
            ,"公交站","地铁站","火车站","汽车站","飞机场","停车场","加油站","加气站","服务区","充电桩"
            ,"景点","公园","游乐场","风景名胜","博物馆","寺庙","植物园","海洋馆","动物园","度假村"
            ,"银行","ATM","建设银行","工商银行","农业银行","中国银行","招商银行","邮政储蓄","交通银行","中信银行"
            ,"超市","商场","步行街","五金店","便利店","书店","菜市场","花店","建材市场","水果店"
            ,"厕所","医院","药店","诊所","汽车维修","洗车","营业厅","学校","理发店","快递");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_nearby_sort, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rc_nearbySort = (RecyclerView) view.findViewById(R.id.rc_nearbySort);
        scrollView_nearbysort = (ZoomScrollView) view.findViewById(R.id.scrollView_nearbysort);
        rc_nearbySort.setHasFixedSize(true);
        scrollView_nearbysort.setNestedScrollingEnabled(false);
        adapter=new BaseRecyclerAdapter<String>(getContext(),strList,R.layout.adp_nearbysort) {
            @Override
            public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling) {
                ImageView imageView=holder.getView(R.id.adp_nearby_im);
                holder.setText(R.id.adp_nearby_text,strList.get(position));
                if(position>=60){
                    Picasso.with(getContext())
                            .load(R.drawable.ic_lifeservice)
                            .into(imageView);
                }else if(position>=50){
                    Picasso.with(getContext())
                            .load(R.drawable.ic_shopping)
                            .into(imageView);
                }else if(position>=40){
                    Picasso.with(getContext())
                            .load(R.drawable.ic_bank)
                            .into(imageView);
                }else if(position>=30){
                    Picasso.with(getContext())
                            .load(R.drawable.ic_travel)
                            .into(imageView);
                }else if(position>=20){
                    Picasso.with(getContext())
                            .load(R.drawable.ic_trip)
                            .into(imageView);
                }else if(position>=10){
                    Picasso.with(getContext())
                            .load(R.drawable.ic_live)
                            .into(imageView);
                }else{
                    Picasso.with(getContext())
                            .load(R.drawable.ic_eat)
                            .into(imageView);
                }

            }
        };
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                final int index;
                index=position;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(strList.get(index));
                    }
                },500);
                int type=0;
                if(position>=60){
                    type=7;
                }else if(position>=50){
                    type=6;
                }else if(position>=40){
                    type=5;
                }else if(position>=30){
                    type=4;
                }else if(position>=20){
                    type=3;
                }else if(position>=10){
                    type=2;
                }else{
                    type=1;
                }
                Intent intent=new Intent(getContext(), NearbyActivity.class);
                intent.putExtra("Type",type);
                startActivity(intent);
            }
        });
        rc_nearbySort.setLayoutManager(new GridLayoutManager(getContext(),5));
        rc_nearbySort.setAdapter(adapter);
    }
}
