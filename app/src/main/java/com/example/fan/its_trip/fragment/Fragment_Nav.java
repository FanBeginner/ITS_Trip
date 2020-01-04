package com.example.fan.its_trip.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.example.fan.its_trip.App;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.activity.BNaviGuideActivity;

import com.example.fan.its_trip.activity.WNaviGuideActivity;
import com.example.fan.its_trip.adapter.BaseRecyclerAdapter;
import com.example.fan.its_trip.adapter.BaseRecyclerHolder;
import com.example.fan.its_trip.toast.MyToast;
import com.example.fan.its_trip.bean.Data;
import com.example.fan.its_trip.utils.NormalUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fan on 2019/7/11.
 */

public class Fragment_Nav extends Fragment implements View.OnClickListener {
    private MapView nav_mapView;
    private EditText ed_nav_addr;
    private Button bt_search;
    private FrameLayout search_layout;
    private TextView tv_nav_place;
    private TextView tv_nav_placeInfo;
    private Button btn_nav_riding;
    private Button btn_nav_walk;
    private RelativeLayout layout_navInfo;
    public LocationClient mLocationClient;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    private RecyclerView recyclerView_suggest;
    private String keyWord, locCity;
    private SuggestionSearch mSuggestionSearch;
    private PoiSearch poiSearch;
    private final String TAG = "Frament_Nav";
    private List<Data> list, list2;
    private BaseRecyclerAdapter<Data> adapter;
    private List<SuggestionResult.SuggestionInfo> suggestionInfoList;
    private List<List<PoiDetailInfo>> poiDetailInfoList;
    private Button btn_nav_drive;
    /*导航起终点Marker，可拖动改变起终点的坐标*/
    private Marker mStartMarker;
    private Marker mEndMarker;

    private LatLng startPt,endPt;

    BikeNaviLaunchParam bikeParam;
    WalkNaviLaunchParam walkParam;

    BitmapDescriptor bdStart = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_start);
    BitmapDescriptor bdEnd = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_end);

    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";

    private static final int authBaseRequestCode = 1;
    private static final String[] authBaseArr = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_nav, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        nav_mapView = (MapView) view.findViewById(R.id.nav_mapView);
        ed_nav_addr = (EditText) view.findViewById(R.id.ed_nav_addr);
        bt_search = (Button) view.findViewById(R.id.bt_search);
        search_layout = (FrameLayout) view.findViewById(R.id.search_layout);
        tv_nav_place = (TextView) view.findViewById(R.id.tv_nav_place);
        tv_nav_placeInfo = (TextView) view.findViewById(R.id.tv_nav_placeInfo);
        btn_nav_riding = (Button) view.findViewById(R.id.btn_nav_riding);
        btn_nav_walk = (Button) view.findViewById(R.id.btn_nav_walk);
        btn_nav_drive = (Button) view.findViewById(R.id.btn_nav_drive);
        layout_navInfo = (RelativeLayout) view.findViewById(R.id.layout_navInfo);
        recyclerView_suggest = (RecyclerView) view.findViewById(R.id.recyclerView_suggest);

        bt_search.setOnClickListener(this);
        btn_nav_riding.setOnClickListener(this);
        btn_nav_walk.setOnClickListener(this);
        btn_nav_drive.setOnClickListener(this);

        list = new ArrayList<>();
        list2 = new ArrayList<>();
        suggestionInfoList = new ArrayList<>();
        poiDetailInfoList = new ArrayList<>();
        locCity = "北京";
        keyWord = "景点";

        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions((Activity) getContext(), permissions, 1);
        } else {
            requestLocation();
        }

        baiduMap = nav_mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        mSuggestionSearch = SuggestionSearch.newInstance();
        poiSearch = PoiSearch.newInstance();
        //POI检索监听
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    for (int i = 0; i < poiResult.getAllPoi().size(); i++) {
                        Log.e(TAG, "onGetPoiResult: " + poiResult.getAllPoi().get(i));
                        //PoiInfo 检索到的第一条信息
                        PoiInfo poi = poiResult.getAllPoi().get(i);
                        //通过第一条检索信息对应的uid发起详细信息检索
                        poiSearch.searchPoiDetail((new PoiDetailSearchOption())
                                .poiUids(poi.uid)); // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔。
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            //poi检索详细信息返回
            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
                Log.e(TAG, "onGetPoiDetailResult: " + poiDetailSearchResult.toString());
                if (poiDetailSearchResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    poiDetailInfoList.add(poiDetailSearchResult.getPoiDetailInfoList());
                    List<PoiDetailInfo> poiDetailList = new ArrayList<>();
                    poiDetailList = poiDetailSearchResult.getPoiDetailInfoList();
                    for (int i = 0; i < poiDetailList.size(); i++) {
                        list.add(new Data(poiDetailList.get(i).getName(), "", R.drawable.location_white));
                        list2.add(new Data("", poiDetailList.get(i).getAddress(), R.drawable.location_white));
                    }
                    Log.e(TAG, "onGetPoiDetailResult--size: " + poiDetailInfoList.size());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        //编辑框改变监听
        ed_nav_addr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || "".equals(s.toString())) {
                    recyclerView_suggest.setVisibility(View.GONE);
                } else {
                    list.clear();
                    list2.clear();
                    poiDetailInfoList.clear();
                    suggestionInfoList.clear();
                    recyclerView_suggest.setVisibility(View.VISIBLE);
                    keyWord = ed_nav_addr.getText().toString();
                    mSuggestionSearch.requestSuggestion(new SuggestionSearchOption()
                            .city(locCity)
                            .keyword(keyWord));
                    Log.e(TAG, "afterTextChanged: " + s.toString());
                }
            }
        });
        //搜索建议监听
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                Log.e(TAG, "onGetSuggestionResult: " + suggestionResult.error);
                if (suggestionResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    suggestionInfoList = suggestionResult.getAllSuggestions();
                    for (int i = 0; i < suggestionResult.getAllSuggestions().size(); i++) {
                        Log.e(TAG, "onGetPoiResult: " + suggestionResult.getAllSuggestions().get(i));
                        //PoiInfo 检索到的第一条信息
                        SuggestionResult.SuggestionInfo suggestion = suggestionInfoList.get(i);
                        //通过第一条检索信息对应的uid发起详细信息检索
                        poiSearch.searchPoiDetail((new PoiDetailSearchOption())
                                .poiUids(suggestion.uid)); // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔。
                    }
                }
            }
        });
        //recycleview适配器adapter初始化
        adapter = new BaseRecyclerAdapter<Data>(getContext(), list, R.layout.list_item) {
            @Override
            public void convert(BaseRecyclerHolder holder, Data item, int position, boolean isScrolling) {
                holder.setText(R.id.item_text, item.getText());
                holder.setText(R.id.item_text2, list2.get(position).getText2());
                if (item.getImageUrl() != null) {
                    holder.setImageByUrl(R.id.item_image, item.getImageUrl());
                } else {
                    holder.setImageResource(R.id.item_image, item.getImageId());
                }
            }
        };
        //recycleview点击事件
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemClick(RecyclerView parent, final View view, int position) {
                Log.e(TAG, "onItemClick: " + poiDetailInfoList.size() + "---" + suggestionInfoList.size());
                moveToPosition(poiDetailInfoList.get(position).get(0).getLocation());
                tv_nav_place.setText(list.get(position).getText());
                tv_nav_placeInfo.setText(poiDetailInfoList.get(position).get(0).getAddress());
                endPt=new LatLng(poiDetailInfoList.get(position).get(0).getLocation().latitude,poiDetailInfoList.get(position).get(0).getLocation().longitude);
                //数据使用要在编辑框数据改变前用，放在后面就会被清掉出现错误
                ed_nav_addr.setText(list.get(position).getText());
                ed_nav_addr.clearFocus();
                //点击后发现软件盘未关闭，设置关闭
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                recyclerView_suggest.setVisibility(View.GONE);
                layout_navInfo.setVisibility(View.VISIBLE);

                initOverlay();
            }
        });
        //recycleview长按事件
        adapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View view, int position) {

                return true;
            }
        });
        recyclerView_suggest.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_suggest.setAdapter(adapter);

    }
    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }
    private boolean hasBasePhoneAuth() {
        PackageManager pm = getActivity().getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, getActivity().getPackageName()) != PackageManager
                    .PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    private void initNavi() {
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }
        BaiduNaviManagerFactory.getBaiduNaviManager().init(getContext(),
                mSDCardPath, APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        String result;
                        if (0 == status) {
                            result = "key校验成功!";
                        } else {
                            result = "key校验失败, " + msg;
                        }
                        MyToast.showInfo(result);
                    }

                    @Override
                    public void initStart() {
                        MyToast.showInfo("百度导航引擎初始化开始");
                    }

                    @Override
                    public void initSuccess() {
                        MyToast.showSuccess("百度导航引擎初始化成功");
                        // 初始化tts
                        initTTS();
                    }

                    @Override
                    public void initFailed(int errCode) {
                        MyToast.showError("百度导航引擎初始化失败 ");
                    }
                });
    }
    private void initTTS() {
        // 使用内置TTS
        BaiduNaviManagerFactory.getTTSManager().initTTS(getContext(),
                getSdcardDir(), APP_FOLDER_NAME, NormalUtils.getTTSAppID());

    }
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);//设置更新时间间隔
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    public void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    MyToast.showWarning("必须同意所有权限才能使用本程序");
                    getActivity().finish();
                    return;
                }
                if (result == 0) {
                    continue;
                } else {
                    MyToast.showInfo("缺少导航基本的权限!");
                    return;
                }
            }
            requestLocation();
            initNavi();
        }
    }

    private void moveToPosition(LatLng latLng) {
        //移动到指定位置
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(18.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        //绘制图标
        baiduMap.clear();//切换位置原来的图标清掉
        MarkerOptions option = new MarkerOptions().icon(bdStart).position(latLng);
        baiduMap.addOverlay(option);
    }

    private void navigateToNow(BDLocation location) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(18f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);

    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateToNow(location);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    locCity = location.getCity();

                }
            });
            //mapView 销毁后不在处理新接收的位置
            if (location == null || nav_mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(50)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            startPt=new LatLng(location.getLatitude(),location.getLongitude());
            SharedPreferences pre= App.appContext.getSharedPreferences("data",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=pre.edit();
            editor.putString("City",location.getCity());
            editor.putFloat("Longitude", (float) location.getLongitude());
            editor.putFloat("Latitude", (float) location.getLatitude());
            editor.apply();
        }
    }
    /**
     * 初始化导航起终点Marker
     */
    public void initOverlay() {
        /*构造导航起终点参数对象*/
        bikeParam = new BikeNaviLaunchParam().stPt(startPt).endPt(endPt);
        walkParam = new WalkNaviLaunchParam().stPt(startPt).endPt(endPt);

        MarkerOptions ooA = new MarkerOptions().position(startPt).icon(bdStart)
                .zIndex(9).draggable(true);

        mStartMarker = (Marker) (baiduMap.addOverlay(ooA));
        mStartMarker.setDraggable(true);
        MarkerOptions ooB = new MarkerOptions().position(endPt).icon(bdEnd)
                .zIndex(5);
        mEndMarker = (Marker) (baiduMap.addOverlay(ooB));
        mEndMarker.setDraggable(true);

        baiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
            }

            public void onMarkerDragEnd(Marker marker) {
                if(marker == mStartMarker){
                    startPt = marker.getPosition();
                }else if(marker == mEndMarker){
                    endPt = marker.getPosition();
                }
                bikeParam.stPt(startPt).endPt(endPt);
                walkParam.stPt(startPt).endPt(endPt);
            }

            public void onMarkerDragStart(Marker marker) {
            }
        });
    }

    /**
     * 开始骑行导航
     */
    private void startBikeNavi() {
        Log.d(TAG, "startBikeNavi");
        try {
            BikeNavigateHelper.getInstance().initNaviEngine((Activity) getContext(), new IBEngineInitListener() {
                @Override
                public void engineInitSuccess() {
                    Log.d(TAG, "BikeNavi engineInitSuccess");
                    routePlanWithBikeParam();
                }

                @Override
                public void engineInitFail() {
                    Log.d(TAG, "BikeNavi engineInitFail");
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "startBikeNavi Exception");
            e.printStackTrace();
        }
    }

    /**
     * 开始步行导航
     */
    private void startWalkNavi() {
        Log.d(TAG, "startBikeNavi");
        try {
            WalkNavigateHelper.getInstance().initNaviEngine((Activity) getContext(), new IWEngineInitListener() {
                @Override
                public void engineInitSuccess() {
                    Log.d(TAG, "WalkNavi engineInitSuccess");
                    routePlanWithWalkParam();
                }

                @Override
                public void engineInitFail() {
                    Log.d(TAG, "WalkNavi engineInitFail");
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "startBikeNavi Exception");
            e.printStackTrace();
        }
    }

    /**
     * 发起骑行导航算路
     */
    private void routePlanWithBikeParam() {
        BikeNavigateHelper.getInstance().routePlanWithParams(bikeParam, new IBRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d(TAG, "BikeNavi onRoutePlanStart");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d(TAG, "BikeNavi onRoutePlanSuccess");
                Intent intent = new Intent();
                intent.setClass(getContext(), BNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(BikeRoutePlanError error) {
                Log.d(TAG, "BikeNavi onRoutePlanFail");
            }

        });
    }

    /**
     * 发起步行导航算路
     */
    private void routePlanWithWalkParam() {
        WalkNavigateHelper.getInstance().routePlanWithParams(walkParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d(TAG, "WalkNavi onRoutePlanStart");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d("View", "onRoutePlanSuccess");
                Intent intent = new Intent();
                intent.setClass(getContext(), WNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError error) {
                Log.d(TAG, "WalkNavi onRoutePlanFail");
            }

        });
    }
    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_search:
                if(!ed_nav_addr.getText().toString().isEmpty()){
                    tv_nav_place.setText(list.get(0).getText());
                    tv_nav_placeInfo.setText(poiDetailInfoList.get(0).get(0).getAddress());
                    endPt=new LatLng(poiDetailInfoList.get(0).get(0).getLocation().latitude,poiDetailInfoList.get(0).get(0).getLocation().longitude);

                    ed_nav_addr.setText(list.get(0).getText());
                    //清除焦点，也就是光标
                    ed_nav_addr.clearFocus();
                    //点击后发现软件盘未关闭，设置关闭
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                    recyclerView_suggest.setVisibility(View.GONE);
                    layout_navInfo.setVisibility(View.VISIBLE);

                    initOverlay();
                }else {
                    MyToast.showInfo("请输入地址！");
                }
                break;
            case R.id.btn_nav_riding:
                startBikeNavi();
                layout_navInfo.setVisibility(View.GONE);
                break;
            case R.id.btn_nav_walk:
                walkParam.extraNaviMode(0);
                startWalkNavi();
                layout_navInfo.setVisibility(View.GONE);
                break;
            case R.id.btn_nav_drive:
                //startActivity(new Intent(getContext(), DemoActivity.class));
                walkParam.extraNaviMode(1);
                startWalkNavi();
                layout_navInfo.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        nav_mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        nav_mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        nav_mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        poiSearch.destroy();
        mSuggestionSearch.destroy();
        bdStart.recycle();
        bdEnd.recycle();
    }

}
