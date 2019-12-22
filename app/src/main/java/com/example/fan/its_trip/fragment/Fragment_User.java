package com.example.fan.its_trip.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.bean.UserInfoEvent;
import com.example.fan.its_trip.adapter.BaseRecyclerAdapter;
import com.example.fan.its_trip.adapter.BaseRecyclerHolder;
import com.example.fan.its_trip.bean.UserInfo;
import com.example.fan.its_trip.http.HttpRequest;
import com.example.fan.its_trip.toast.MyToast;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fan on 2019/9/27.
 */

public class Fragment_User extends Fragment {
    private RecyclerView user_recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<UserInfo> list;
    private BaseRecyclerAdapter<UserInfo> adapter;
    private static final String TAG = "Fragment_User";
    private Gson gson;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_users, container, false);
        initView(view);

        return view;
    }

    public void initView(View view) {
        user_recyclerView = (RecyclerView) view.findViewById(R.id.user_recyclerView);
        swipeRefreshLayout=view.findViewById(R.id.user_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        if(!HttpRequest.getRole().equalsIgnoreCase("R02"))
            MyToast.showInfo("需要管理员权限！");
        gson=new Gson();
        list=new ArrayList<>();

        adapter=new BaseRecyclerAdapter<UserInfo>(getContext(),list,R.layout.adp_user) {
            @Override
            public void convert(BaseRecyclerHolder holder, UserInfo item, final int position, boolean isScrolling) {
                holder.getView(R.id.item_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new UserInfoEvent(list.get(position).getUsername(),
                                        list.get(position).getPname(),list.get(position).getPsex(),
                                        list.get(position).getPtel(),list.get(position).getPcardid()));
                            }
                        },500);

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_UserDetail()).commit();
                    }
                });
                if(list.get(position).getPsex().equals("男"))
                    holder.setImageResource(R.id.item_circleImage,R.drawable.touxiang_2);
                else
                    holder.setImageResource(R.id.item_circleImage,R.drawable.touxiang_1);
                holder.setText(R.id.item_user, "用户名："+item.getUsername());
                holder.setText(R.id.item_name, "姓名："+item.getPname());
                holder.setText(R.id.item_phone, "电话："+item.getPtel());
            }
        };
        user_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        user_recyclerView.setAdapter(adapter);
        getUserInfo();
    }

    private void refreshData(){
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
                        getUserInfo();
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    private void getUserInfo(){
        list.clear();
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
                            list.add(gson.fromJson(array.get(i).toString(), UserInfo.class));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
