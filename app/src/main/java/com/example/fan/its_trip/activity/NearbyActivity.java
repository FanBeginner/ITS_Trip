package com.example.fan.its_trip.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.fan.its_trip.App;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.adapter.BaseRecyclerAdapter;
import com.example.fan.its_trip.adapter.BaseRecyclerHolder;
import com.example.fan.its_trip.bean.UserInfoEvent;
import com.example.fan.its_trip.toast.MyToast;
import com.example.fan.its_trip.utils.PoiOverlay;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 演示poi搜索功能
 */
public class NearbyActivity extends FragmentActivity implements
        OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private BaiduMap mBaiduMap = null;
    /* 搜索关键字输入窗口 */
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int loadIndex = 0;

    private LatLng center;
    private int radius = 2000;

    private int searchType = 0;  // 搜索的类型，在显示时区分

    private SlidingUpPanelLayout mLayout;
    private RecyclerView rc_nearby;
    private TextView tv_name;
    private String city;
    private List<PoiDetailInfo> poiDetailInfoList=new ArrayList<>();
    private List<PoiInfo> list=new ArrayList<>();
    private BaseRecyclerAdapter<PoiInfo> adapter;
    private int type;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        EventBus.getDefault().register(this);

        mLayout = findViewById(R.id.sliding_layout);
        tv_name = findViewById(R.id.name);
        rc_nearby = findViewById(R.id.rc_nearby);

        initData();
    }

    public void initData() {
        SharedPreferences pre= App.appContext.getSharedPreferences("data",MODE_PRIVATE);
        city=pre.getString("City","北京");
        center= new LatLng(pre.getFloat("Latitude", (float) 39.92235), pre.getFloat("Longitude", (float) 116.380338));

        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(PanelState.COLLAPSED);
            }
        });
        tv_name.setText(Html.fromHtml("点击展开"));
        mLayout.setAnchorPoint(0.7f);
        mLayout.setPanelState(PanelState.ANCHORED);

        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
        sugAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);
        mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map))).getBaiduMap();

        /* 当输入关键字变化时，动态更新建议列表 */
        keyWorldsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /* 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新 */
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString())
                        .city(city));

            }
        });
        keyWorldsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBaiduMap.clear();
                searchNearbyProcess();
                type=8;
            }
        });

        type=getIntent().getIntExtra("Type",0);
        adapter=new BaseRecyclerAdapter<PoiInfo>(this,list,R.layout.adp_nearby) {
            @Override
            public void convert(BaseRecyclerHolder holder, PoiInfo item, int position, boolean isScrolling) {
                if(type==7){
                    holder.setImageResource(R.id.adp_slid_pic,R.drawable.im_life);
                }else if(type==6){
                    holder.setImageResource(R.id.adp_slid_pic,R.drawable.im_market);
                }else if(type==5){
                    holder.setImageResource(R.id.adp_slid_pic,R.drawable.im_bank);
                }else if(type==4){
                    holder.setImageResource(R.id.adp_slid_pic,R.drawable.im_place);
                }else if(type==3){
                    holder.setImageResource(R.id.adp_slid_pic,R.drawable.im_trip);
                }else if(type==2){
                    holder.setImageResource(R.id.adp_slid_pic,R.drawable.im_hotel);
                }else if(type==1){
                    holder.setImageResource(R.id.adp_slid_pic,R.drawable.im_food);
                }else{
                    holder.getView(R.id.adp_slid_pic).setVisibility(View.GONE);
                }
                holder.setText(R.id.adp_slid_name,item.getName());
                holder.setText(R.id.adp_slid_addr,"具体位置："+item.getAddress());
                if(item.getPhoneNum().isEmpty())
                    holder.setText(R.id.adp_slid_tel,"咨询电话：暂无");
                else
                    holder.setText(R.id.adp_slid_tel,"咨询电话："+item.getPhoneNum());
            }
        };
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                TextView popupText = new TextView(NearbyActivity.this);
                popupText.setGravity(Gravity.CENTER);
                popupText.setBackgroundResource(R.drawable.popup);
                popupText.setTextColor(0xff000000);
                // 移动到指定索引的坐标
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(list.get(position).getLocation()));
                // 弹出泡泡
                popupText.setText(list.get(position).getName());
                mBaiduMap.showInfoWindow(new InfoWindow(popupText, list.get(position).getLocation(), 10));

                mLayout.setPanelState(PanelState.COLLAPSED);
            }
        });
        rc_nearby.setLayoutManager(new LinearLayoutManager(this));
        rc_nearby.setAdapter(adapter);

    }
    @Subscribe
    public void onEventBus(String str) {
        keyWorldsView.setText(str);
        keyWorldsView.clearFocus();
        searchNearbyProcess();
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
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 响应周边搜索功能
     *
     */
    public void searchNearbyProcess() {
        searchType = 2;
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword(keyWorldsView.getText().toString())
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(center)
                .radius(radius)
                .pageNum(loadIndex)
                .scope(1);

        mPoiSearch.searchNearby(nearbySearchOption);
    }

    /**
     * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
     *
     * @param result Poi检索结果，包括城市检索，周边检索，区域检索
     */
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            MyToast.showError("未找到结果");
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();

            list.clear();
            list.addAll(result.getAllPoi());
            adapter.notifyDataSetChanged();
//            for(int i=0;i<result.getAllPoi().size();i++){
//
//                Log.e("---1111", "onGetPoiResult: "+list.size() );
//                Log.e("---222-", "onGetPoiResult: "+"--"
//                        +result.getAllPoi().get(i).getDistance()+"--"+result.getAllPoi().get(i).getName()+"--"+result.getAllPoi().get(i).getAddress()
//                        +"--"+result.getAllPoi().get(i).getPhoneNum()+"--"+result.getAllPoi().get(i).getStreetId());
//            }

            switch (searchType) {
                case 2:
                    showNearbyArea(center, radius);
                    break;
                case 3:

                    break;
                default:
                    break;
            }
            return;
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";

            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            MyToast.showInfo(strInfo);
        }
    }

    /**
     * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
     * V5.2.0版本之后，还方法废弃，使用{@link #onGetPoiDetailResult(PoiDetailSearchResult)}代替
     *
     * @param result POI详情检索结果
     */
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast.showError("抱歉，未找到结果");
        } else {
            //MyToast.showInfo(result.getName() + ": " + result.getAddress());
            Log.e("----", "onGetPoiDetailResult: "+result.getTag()+"--"+result.getType()+"--"
                    +result.getShopHours()+"--"+result.getTelephone());
            TextView popupText = new TextView(NearbyActivity.this);
            popupText.setGravity(Gravity.CENTER);
            popupText.setBackgroundResource(R.drawable.popup);
            popupText.setTextColor(0xff000000);
            // 移动到指定索引的坐标
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

            // 弹出泡泡
            popupText.setText(result.getName());
            mBaiduMap.showInfoWindow(new InfoWindow(popupText, result.getLocation(), 10));
        }
    }
    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
        if (poiDetailSearchResult.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast.showError("抱歉，未找到结果");
        } else {
            poiDetailInfoList = poiDetailSearchResult.getPoiDetailInfoList();
            if (null == poiDetailInfoList || poiDetailInfoList.isEmpty()) {
                MyToast.showInfo("抱歉，检索结果为空");
                return;
            }

            for (int i = 0; i < poiDetailInfoList.size(); i++) {
                PoiDetailInfo poiDetailInfo = poiDetailInfoList.get(i);
                if (null != poiDetailInfo) {
                    MyToast.showInfo(poiDetailInfo.getName() + ": " + poiDetailInfo.getAddress());
                }
            }

        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    /**
     * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
     *
     * @param res Sug检索结果
     */
    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }

        List<String> suggest = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
            }
        }

        sugAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                suggest);
        keyWorldsView.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    private class MyPoiOverlay extends PoiOverlay {
        MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
            // }
            return true;
        }
    }

    /**
     * 对周边检索的范围进行绘制
     *
     * @param center 周边检索中心点坐标
     * @param radius 周边检索半径，单位米
     */
    public void showNearbyArea(LatLng center, int radius) {
        BitmapDescriptor centerBitmap = BitmapDescriptorFactory.fromResource(R.drawable.location_blue);
        MarkerOptions ooMarker = new MarkerOptions().position(center).icon(centerBitmap);
        mBaiduMap.addOverlay(ooMarker);

        OverlayOptions ooCircle = new CircleOptions().fillColor(0x8FFFFFF)
                .center(center)
                .stroke(new Stroke(5, 0xFF1E90FF))
                .radius(radius);

        mBaiduMap.addOverlay(ooCircle);
    }
    @Override
    public void onBackPressed() {
        //返回按钮监听，如果是展开、锚定状态时，将其这为折叠状态
        if (mLayout != null &&
                (mLayout.getPanelState() == PanelState.EXPANDED || mLayout.getPanelState() == PanelState.ANCHORED)) {
            mLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
