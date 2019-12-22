package com.example.fan.its_trip.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;

import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import com.example.fan.its_trip.App;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.adapter.BaseRecyclerAdapter;
import com.example.fan.its_trip.adapter.BaseRecyclerHolder;
import com.example.fan.its_trip.toast.MyToast;
import com.example.fan.its_trip.utils.BusLineOverlay;

import java.util.ArrayList;
import java.util.List;


/**
 * 此demo用来展示如何进行公交线路详情检索，并使用RouteOverlay在地图上绘制 同时展示如何浏览路线节点并弹出泡泡
 */
public class BusLineSearchActivity extends FragmentActivity implements
        OnGetPoiSearchResultListener, OnGetBusLineSearchResultListener,
        BaiduMap.OnMapClickListener {
    public static int nodeIndex = -2; // 节点索引,供浏览节点时使用
    private BusLineResult route = null; // 保存驾车/步行路线数据的变量，供浏览节点时使用
    private List<String> busLineIDList = null;
    private int busLineIndex = 0;
    // 搜索相关
    private PoiSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private BusLineSearch mBusLineSearch = null;
    private BaiduMap mBaiduMap = null;
    BusLineOverlay overlay; // 公交路线绘制对象
    private static final String TAG = "BusLineSearchActivity";
    private RecyclerView rc_bus_num,rc_bus_station;
    private BaseRecyclerAdapter<String> adapter_num;
    private BaseRecyclerAdapter<String> adapter_station;
    private List<String>busNumList=new ArrayList<>();
    private List<String>busStationList=new ArrayList<>();
    private CardView cv_busStation;
    private FloatingActionButton fab_close;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busline);
        CharSequence titleLable = "公交线路查询功能";
        setTitle(titleLable);
        mBaiduMap = ((SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.bmapView)).getBaiduMap();
        rc_bus_num=findViewById(R.id.busNum_recyclerView);
        rc_bus_station=findViewById(R.id.busStation_recyclerView);
        cv_busStation=findViewById(R.id.cv_busStation);
        fab_close=findViewById(R.id.fab_busStation_close);

        mBaiduMap.setOnMapClickListener(this);
        mSearch = PoiSearch.newInstance();
        mSearch.setOnGetPoiSearchResultListener(this);
        mBusLineSearch = BusLineSearch.newInstance();
        mBusLineSearch.setOnGetBusLineSearchResultListener(this);
        busLineIDList = new ArrayList<String>();
        overlay = new BusLineOverlay(mBaiduMap);
        mBaiduMap.setOnMarkerClickListener(overlay);
        fab_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv_busStation.setVisibility(View.GONE);
                fab_close.setVisibility(View.GONE);
                //动画效果
                Animation animation= AnimationUtils.loadAnimation(App.appContext,R.anim.translate_off);
                cv_busStation.startAnimation(animation);
                fab_close.startAnimation(animation);
            }
        });

        SharedPreferences pre= App.appContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        EditText ed_city=findViewById(R.id.city);
        ed_city.setText(pre.getString("City",""));

        adapter_num=new BaseRecyclerAdapter<String>(this,busNumList,R.layout.adp_busnum) {
            @Override
            public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling) {
                holder.setText(R.id.item_busNum,item);
            }
        };
        adapter_num.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                busLineIndex=position;
                searchNextBusline(null);
                route = null;
                rc_bus_num.setVisibility(View.GONE);

            }
        });
        rc_bus_num.setLayoutManager(new LinearLayoutManager(this));
        rc_bus_num.setAdapter(adapter_num);

        adapter_station=new BaseRecyclerAdapter<String>(this,busStationList,R.layout.adp_bus_station) {
            @Override
            public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling) {
                holder.setText(R.id.item_busStation,item);
            }
        };
        adapter_station.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                overlay.onBusStationClick(position);
            }
        });
        rc_bus_station.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rc_bus_station.setAdapter(adapter_station);
    }

    /**
     * 发起检索
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        busLineIDList.clear();
        busLineIndex = 0;
        EditText editCity = (EditText) findViewById(R.id.city);
        EditText editSearchKey = (EditText) findViewById(R.id.searchkey);
        // 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
        mSearch.searchInCity((new PoiCitySearchOption()).city(
                editCity.getText().toString())
                        .keyword(editSearchKey.getText().toString()));
        //清除焦点，也就是光标
        editCity.clearFocus();
        //点击后发现软件盘未关闭，设置关闭
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        cv_busStation.setVisibility(View.GONE);
        fab_close.setVisibility(View.GONE);
        //动画效果
        Animation animation= AnimationUtils.loadAnimation(App.appContext,R.anim.translate_off);
        cv_busStation.startAnimation(animation);
        fab_close.startAnimation(animation);
    }

    public void searchNextBusline(View v) {
        if (busLineIndex >= busLineIDList.size()) {
            busLineIndex = 0;
        }
        if (busLineIndex >= 0 && busLineIndex < busLineIDList.size()
                && busLineIDList.size() > 0) {
            mBusLineSearch.searchBusLine((new BusLineSearchOption()
                    .city(((EditText) findViewById(R.id.city)).getText()
                            .toString()).uid(busLineIDList.get(busLineIndex))));
            busLineIndex++;
        }
    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        TextView popupText = new TextView(BusLineSearchActivity.this);
        popupText.setGravity(Gravity.CENTER);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xff000000);
        if (nodeIndex < -1 || route == null
                || nodeIndex >= route.getStations().size()) {
            return;
        }

        if (nodeIndex >= 0) {
            // 移动到指定索引的坐标
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(route
                    .getStations().get(nodeIndex).getLocation()));
            // 弹出泡泡
            popupText.setText(route.getStations().get(nodeIndex).getTitle());
            mBaiduMap.showInfoWindow(new InfoWindow(popupText, route.getStations()
                    .get(nodeIndex).getLocation(), 10));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mSearch.destroy();
        mBusLineSearch.destroy();
        super.onDestroy();
    }

    @Override
    public void onGetBusLineResult(BusLineResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast.showInfo("抱歉，未找到结果-BusLine");
            return;
        }
        mBaiduMap.clear();
        route = result;
        nodeIndex = -1;
        overlay.removeFromMap();
        overlay.setData(result);
        overlay.addToMap();
        overlay.zoomToSpan();

        for(int i=0;i<route.getStations().size();i++){
            busStationList.add(route.getStations().get(i).getTitle());
        }
        if(busStationList.size()>0){
            cv_busStation.setVisibility(View.VISIBLE);
            fab_close.setVisibility(View.VISIBLE);
            adapter_station.notifyDataSetChanged();
            //动画效果
            Animation animation= AnimationUtils.loadAnimation(App.appContext,R.anim.translate_on);
            cv_busStation.startAnimation(animation);
            fab_close.startAnimation(animation);
        }

        MyToast.showInfo(result.getBusLineName());

        Log.e(TAG, "--onGetBusLineResult: "+result.getBusLineName()+"--"+busNumList.size() );
    }

    @Override
    public void onGetPoiResult(PoiResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast.showInfo("抱歉，未找到结果--Poi");
            return;
        }
        // 遍历所有poi，找到类型为公交线路的poi
        busLineIDList.clear();
        busNumList.clear();
        busStationList.clear();
        for (PoiInfo poi : result.getAllPoi()) {
            busLineIDList.add(poi.uid);
            busNumList.add(poi.getName());
            Log.e(TAG, "---onGetPoiResult: "+poi.getName());
        }
        if(busNumList.size()>0)
            rc_bus_num.setVisibility(View.VISIBLE);
        adapter_num.notifyDataSetChanged();
    }

    /**
     * V5.2.0版本之后，还方法废弃，使用{@link #onGetPoiDetailResult(PoiDetailSearchResult)}
     * 代替
     */
    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult result) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    public void onMapClick(LatLng point) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }
}
