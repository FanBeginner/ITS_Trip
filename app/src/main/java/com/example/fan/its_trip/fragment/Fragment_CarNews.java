package com.example.fan.its_trip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.fan.its_trip.R;
import com.example.fan.its_trip.activity.CarNewsDetailActivity;
import com.example.fan.its_trip.adapter.BaseRecyclerAdapter;
import com.example.fan.its_trip.adapter.BaseRecyclerHolder;
import com.example.fan.its_trip.bean.NewsInfo;
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
 * Created by Fan on 2019/10/30.
 */

public class Fragment_CarNews extends Fragment {
    private RecyclerView rc_CarNews;
    private List<NewsInfo>list=new ArrayList<>();
    private BaseRecyclerAdapter<NewsInfo> adapter;
    private SwipeRefreshLayout news_swipe_refresh;
    private static final String TAG = "Fragment_CarNews";
    private Handler handler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_carnews, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rc_CarNews = (RecyclerView) view.findViewById(R.id.rc_CarNews);
        news_swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.news_swipe_refresh);

        adapter=new BaseRecyclerAdapter<NewsInfo>(getContext(),list,R.layout.adp_carnews) {
            @Override
            public void convert(BaseRecyclerHolder holder, NewsInfo item, int position, boolean isScrolling) {
                holder.setText(R.id.item_news_title,item.getTitle());
                holder.setText(R.id.item_news_time,item.getCtime());
                ImageView imageView=holder.getView(R.id.item_news_pic);
                Picasso.with(getContext())
                        .load(item.getPicUrl().split("alt")[0])
                        .into(imageView);
            }
        };
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, final int position) {
                startActivity(new Intent(getContext(), CarNewsDetailActivity.class));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(list.get(position).getUrl());
                    }
                },500);
            }
        });
        rc_CarNews.setLayoutManager(new LinearLayoutManager(getContext()));
        rc_CarNews.setAdapter(adapter);
        getNewsData();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                adapter.notifyDataSetChanged();
            }
        };
        news_swipe_refresh.setColorSchemeResources(R.color.colorAccent);
        news_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getNewsData();
                                news_swipe_refresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void getNewsData() {
        HttpUtil.sendOkHttpRequest("http://api.tianapi.com/auto?key=59a439a9f98b350ed6e4f8d142eee920&num=50", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                list.clear();
                String responseText = response.body().string();
                Log.i(TAG, "---onResponse: " + responseText);
                try {
                    JSONObject jsonObject=new JSONObject(responseText);
                    JSONArray array=new JSONArray(jsonObject.getString("newslist"));
                    for(int i=0;i<array.length();i++){
                        list.add(new Gson().fromJson(array.get(i).toString(),NewsInfo.class));
                    }
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
