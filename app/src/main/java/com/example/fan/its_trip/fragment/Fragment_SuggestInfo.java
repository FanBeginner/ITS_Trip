package com.example.fan.its_trip.fragment;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;

import com.example.fan.its_trip.R;
import com.example.fan.its_trip.adapter.BaseRecyclerAdapter;
import com.example.fan.its_trip.adapter.BaseRecyclerHolder;
import com.example.fan.its_trip.db.SuggestInfo;
import com.example.fan.its_trip.http.HttpRequest;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fan on 2019/10/29.
 */

public class Fragment_SuggestInfo extends Fragment {
    private TextView tv_sug_tip;
    private RecyclerView rc_suggest;
    private List<SuggestInfo> list=new ArrayList<>();
    private BaseRecyclerAdapter<SuggestInfo> adapter;
    private SwipeRefreshLayout sug_swipe_refresh;
    private static final String TAG = "Fragment_SuggestInfo";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_suggest, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_sug_tip = (TextView) view.findViewById(R.id.tv_sug_tip);
        rc_suggest = (RecyclerView) view.findViewById(R.id.rc_suggest);
        sug_swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.sug_swipe_refresh);

        if(HttpRequest.getRole().equalsIgnoreCase("R02")){
            tv_sug_tip.setVisibility(View.GONE);
            rc_suggest.setVisibility(View.VISIBLE);
        }else{
            tv_sug_tip.setText("您还未拥有管理员权限！");
            tv_sug_tip.setVisibility(View.VISIBLE);
            rc_suggest.setVisibility(View.GONE);
        }
        //list=LitePal.findAll(SuggestInfo.class);
        adapter=new BaseRecyclerAdapter<SuggestInfo>(getContext(),list,R.layout.adp_suggest) {
            @Override
            public void convert(BaseRecyclerHolder holder, SuggestInfo item, final int position, boolean isScrolling) {
                holder.setText(R.id.item_sug_user,"用户："+item.getUser());
                holder.setText(R.id.item_sug_text,item.getText());
                holder.setText(R.id.item_sug_time,item.getTime());
                TextView tv_state=holder.getView(R.id.item_sug_state);
                if(item.isAccept()){
                    tv_state.setText("已受理");
                    tv_state.setTextColor(Color.GREEN);
                }else{
                    tv_state.setText("未受理");
                    tv_state.setTextColor(Color.RED);
                }
                holder.getView(R.id.item_sug_accept).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues contentValues=new ContentValues();
                        contentValues.put("isAccept",true);
                        LitePal.updateAll(SuggestInfo.class,contentValues,"time=?",list.get(position).getTime());
                        getData();
                    }
                });
            }
        };
        rc_suggest.setLayoutManager(new LinearLayoutManager(getContext()));
        rc_suggest.setAdapter(adapter);
        sug_swipe_refresh.setColorSchemeResources(R.color.colorAccent);
        sug_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        getData();
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
                        getData();
                        sug_swipe_refresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void getData(){
        list.clear();
        list.addAll(LitePal.findAll(SuggestInfo.class));
        if(list.size()==0){
            tv_sug_tip.setText("暂无意见反馈！");
            tv_sug_tip.setVisibility(View.VISIBLE);
        }
        Log.e(TAG, "---initView: "+list.size() );
        adapter.notifyDataSetChanged();
    }
}
