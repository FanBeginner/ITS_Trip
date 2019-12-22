/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.example.fan.its_trip.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBNaviStatusListener;
import com.baidu.mapapi.bikenavi.adapter.IBRouteGuidanceListener;
import com.baidu.mapapi.bikenavi.adapter.IBTTSPlayer;
import com.baidu.mapapi.bikenavi.model.BikeRouteDetailInfo;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.walknavi.model.RouteGuideKind;

public class BNaviGuideActivity extends Activity {

    private final static String TAG = BNaviGuideActivity.class.getSimpleName();

    private BikeNavigateHelper mNaviHelper;

    BikeNaviLaunchParam param;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNaviHelper.quit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNaviHelper.resume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNaviHelper = BikeNavigateHelper.getInstance();

        View view = mNaviHelper.onCreate(BNaviGuideActivity.this);
        if (view != null) {
            setContentView(view);
        }

        mNaviHelper.setBikeNaviStatusListener(new IBNaviStatusListener() {
            @Override
            public void onNaviExit() {
                Log.d(TAG, "onNaviExit");
            }
        });

        mNaviHelper.setTTsPlayer(new IBTTSPlayer() {
            @Override
            public int playTTSText(String s, boolean b) {
                Log.d("tts", s);
                return 0;
            }
        });

        mNaviHelper.startBikeNavi(BNaviGuideActivity.this);

        mNaviHelper.setRouteGuidanceListener(this, new IBRouteGuidanceListener() {
            @Override
            public void onRouteGuideIconUpdate(Drawable icon) {
                //诱导图标更新
            }

            @Override
            public void onRouteGuideKind(RouteGuideKind routeGuideKind) {
                //诱导类型枚举
            }

            @Override
            public void onRoadGuideTextUpdate(CharSequence charSequence, CharSequence charSequence1) {
                //诱导信息
            }

            @Override
            public void onRemainDistanceUpdate(CharSequence charSequence) {
                //总的剩余距离
            }

            @Override
            public void onRemainTimeUpdate(CharSequence charSequence) {
                //总的剩余时间
            }

            @Override
            public void onGpsStatusChange(CharSequence charSequence, Drawable drawable) {
                //GPS状态发生变化，来自诱导引擎的消息
            }

            @Override
            public void onRouteFarAway(CharSequence charSequence, Drawable drawable) {
                //已经开始偏航
            }

            @Override
            public void onRoutePlanYawing(CharSequence charSequence, Drawable drawable) {
                //偏航规划中
            }

            @Override
            public void onReRouteComplete() {
                //重新算路成功
            }

            @Override
            public void onArriveDest() {
                //到达目的地
            }

            @Override
            public void onVibrate() {
                //震动
            }

            @Override
            public void onGetRouteDetailInfo(BikeRouteDetailInfo bikeRouteDetailInfo) {
                //获取骑行导航路线详细信息类
            }
        });
    }

}
