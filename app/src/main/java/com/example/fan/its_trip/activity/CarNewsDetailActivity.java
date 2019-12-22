package com.example.fan.its_trip.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.fan.its_trip.R;
import com.example.fan.its_trip.toast.MyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Fan on 2019/10/30.
 */

public class CarNewsDetailActivity extends BaseActivity {
    private Toolbar news_toolbar;
    private WebView news_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsdetail);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        news_toolbar = (Toolbar) findViewById(R.id.news_toolbar);
        news_webview = (WebView) findViewById(R.id.news_webview);

        setSupportActionBar(news_toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        news_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Subscribe
    public void onEventBus(String url){
        news_webview.getSettings().setJavaScriptEnabled(true);
        news_webview.setWebViewClient(new WebViewClient());
        news_webview.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
