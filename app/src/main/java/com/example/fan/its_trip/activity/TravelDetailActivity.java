package com.example.fan.its_trip.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fan.its_trip.R;
import com.example.fan.its_trip.fragment.Fragment_Travel;
import com.example.fan.its_trip.toast.MyToast;
import com.example.fan.its_trip.utils.HttpUtil;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Fan on 2019/10/28.
 */

public class TravelDetailActivity extends BaseActivity {
    private ImageView im_detail_1;
    private Toolbar toolBar;
    private CollapsingToolbarLayout collapsing_toorbar;
    private AppBarLayout appbar;
    private TextView tv_text_desc1;
    private ImageView im_detail_2;
    private ImageView im_detail_3;
    private TextView tv_text_desc2;
    private ImageView im_detail_4;
    private ImageView im_detail_5;
    private ImageView im_detail_6;
    private String desc1,desc2;
    private Handler handler;
    private List<String> images=new ArrayList<>();
    private static final String TAG = "TravelDetailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveldetail);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        im_detail_1 = (ImageView) findViewById(R.id.im_detail_1);
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        collapsing_toorbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toorbar);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        tv_text_desc1 = (TextView) findViewById(R.id.tv_text_desc1);
        im_detail_2 = (ImageView) findViewById(R.id.im_detail_2);
        im_detail_3 = (ImageView) findViewById(R.id.im_detail_3);
        tv_text_desc2 = (TextView) findViewById(R.id.tv_text_desc2);
        im_detail_4 = (ImageView) findViewById(R.id.im_detail_4);
        im_detail_5 = (ImageView) findViewById(R.id.im_detail_5);
        im_detail_6 = (ImageView) findViewById(R.id.im_detail_6);

        setSupportActionBar(toolBar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                tv_text_desc1.setText(desc1);
                tv_text_desc2.setText(desc2);
                Picasso.with(TravelDetailActivity.this)
                        .load("http://192.168.43.92:8089/TravelSystem_By_Xww/"+images.get(0))
                        .into(im_detail_1);
                Picasso.with(TravelDetailActivity.this)
                        .load("http://192.168.43.92:8089/TravelSystem_By_Xww/"+images.get(1))
                        .into(im_detail_2);
                Picasso.with(TravelDetailActivity.this)
                        .load("http://192.168.43.92:8089/TravelSystem_By_Xww/"+images.get(2))
                        .into(im_detail_3);
                Picasso.with(TravelDetailActivity.this)
                        .load("http://192.168.43.92:8089/TravelSystem_By_Xww/"+images.get(3))
                        .into(im_detail_4);
                Picasso.with(TravelDetailActivity.this)
                        .load("http://192.168.43.92:8089/TravelSystem_By_Xww/"+images.get(4))
                        .into(im_detail_5);
                Picasso.with(TravelDetailActivity.this)
                        .load("http://192.168.43.92:8089/TravelSystem_By_Xww/"+images.get(5))
                        .into(im_detail_6);
            }
        };
    }
    @Subscribe
    public void onEventBus(String name){
        collapsing_toorbar.setTitle(name);
        getTravelDetail(name);
    }
    public void getTravelDetail(String name){
        HttpUtil.sendOkHttpRequest("http://192.168.43.92:8089/TravelSystem_By_Xww/GetDetailInfo.do?loc_name="+name, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MyToast.showError(""+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                Log.i(TAG, "---onResponse: "+responseText);
                images.clear();
                try {
                    JSONArray array=new JSONArray(responseText);
                    JSONObject jsonObject=new JSONObject(array.get(0).toString());
                    desc1=jsonObject.getString("desc_1");
                    desc2=jsonObject.getString("desc_2");
                    for(int i=1;i<7;i++){
                        images.add(jsonObject.getString("img_"+i));
                    }
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
