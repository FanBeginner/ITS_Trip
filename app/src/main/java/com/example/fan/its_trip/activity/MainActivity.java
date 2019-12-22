package com.example.fan.its_trip.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fan.its_trip.App;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.fragment.ChooseAreaFragment;
import com.example.fan.its_trip.fragment.Fragment_AboutUs;
import com.example.fan.its_trip.fragment.Fragment_CarNews;
import com.example.fan.its_trip.fragment.Fragment_ETC;
import com.example.fan.its_trip.fragment.Fragment_Environment;
import com.example.fan.its_trip.fragment.Fragment_FeedBack;
import com.example.fan.its_trip.fragment.Fragment_Home;
import com.example.fan.its_trip.fragment.Fragment_NearbyIntroduce;
import com.example.fan.its_trip.fragment.Fragment_Person;
import com.example.fan.its_trip.fragment.Fragment_SuggestInfo;
import com.example.fan.its_trip.fragment.Fragment_Travel;
import com.example.fan.its_trip.fragment.Fragment_User;
import com.example.fan.its_trip.http.HttpRequest;
import com.example.fan.its_trip.toast.MyToast;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ImageView iv_menu_drawer;
    private TextView tv_Title;
    public TextView tv_account;
    private FrameLayout Main;
    private NavigationView navigation_drawer;
    private ConstraintLayout mydrawer;
    private DrawerLayout drawer_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        iv_menu_drawer = (ImageView) findViewById(R.id.iv_menu_drawer);
        tv_Title = (TextView) findViewById(R.id.tv_Title);
        Main = (FrameLayout) findViewById(R.id.Main);
        navigation_drawer = (NavigationView) findViewById(R.id.navigation_drawer);
        mydrawer = (ConstraintLayout) findViewById(R.id.mydrawer);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //这个TextView控件，在NavigationView的headerLayout中
        View headerView=navigation_drawer.getHeaderView(0);
        tv_account = headerView.findViewById(R.id.tv_account);

        navigation_drawer.setNavigationItemSelectedListener(this);
        iv_menu_drawer.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_Home()).commit();
        tv_Title.setText("首页");

        tv_account.setText(HttpRequest.getUserName());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homepage:
                SharedPreferences pref = App.appContext.getSharedPreferences("data", Context.MODE_PRIVATE);
                String weatherString=pref.getString("weather_id",null);
                if(weatherString!=null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.Main, new Fragment_Home()).commit();
                }else{
                    getSupportFragmentManager().beginTransaction().replace(R.id.Main, new ChooseAreaFragment()).commit();
                }
                tv_Title.setText("首页");
                drawer_layout.closeDrawer(mydrawer);
                break;
            case R.id.person:
                getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_Person()).commit();
                tv_Title.setText("个人中心");
                drawer_layout.closeDrawer(mydrawer);
                break;
            case R.id.users:
                getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_User()).commit();
                tv_Title.setText("用户中心");
                drawer_layout.closeDrawer(mydrawer);
                break;
            case R.id.environment:
                getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_Environment()).commit();
                tv_Title.setText("环境指标");
                drawer_layout.closeDrawer(mydrawer);
                break;
            case R.id.etc:
                getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_ETC()).commit();
                tv_Title.setText("ETC账户");
                drawer_layout.closeDrawer(mydrawer);
                break;
            case R.id.bus:
                startActivity(new Intent(MainActivity.this,BusLineSearchActivity.class));
                break;
            case R.id.nearby:
                getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_NearbyIntroduce()).commit();
                tv_Title.setText("周边搜索");
                drawer_layout.closeDrawer(mydrawer);
                break;
            case R.id.travel:
                getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_Travel()).commit();
                tv_Title.setText("旅游推荐");
                drawer_layout.closeDrawer(mydrawer);
                break;
            case R.id.news:
                getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_CarNews()).commit();
                tv_Title.setText("汽车资讯");
                drawer_layout.closeDrawer(mydrawer);
                break;
            case R.id.suggest:
                getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_SuggestInfo()).commit();
                tv_Title.setText("建议中心");
                drawer_layout.closeDrawer(mydrawer);
                break;
            case R.id.feedback:
                getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_FeedBack()).commit();
                tv_Title.setText("用户反馈");
                drawer_layout.closeDrawer(mydrawer);
                break;
            case R.id.about:
                getSupportFragmentManager().beginTransaction().replace(R.id.Main,new Fragment_AboutUs()).commit();
                tv_Title.setText("关于我们");
                drawer_layout.closeDrawer(mydrawer);
                break;
            case R.id.logout:
                SharedPreferences preference= PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preference.edit();
                editor.putBoolean("rememberPwd",false);
                editor.putBoolean("autoLogin",false);
                editor.apply();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_menu_drawer:
                drawer_layout.openDrawer(mydrawer);
                break;
        }
    }
    long firstClickTime=0;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis()-firstClickTime<2000){
            finish();
        }else{
            firstClickTime=System.currentTimeMillis();
            MyToast.showInfo("再按一次返回退出！");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
