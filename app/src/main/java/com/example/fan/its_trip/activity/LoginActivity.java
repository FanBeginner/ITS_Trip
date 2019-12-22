package com.example.fan.its_trip.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.dialog.LoadingDialog;
import com.example.fan.its_trip.http.HttpRequest;
import com.example.fan.its_trip.toast.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fan on 2019/7/5.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText ed_login_name;
    private EditText ed_login_password;
    private CheckBox checkbox_remember;
    private CheckBox checkbox_autoLogin;
    private Button btn_login;
    private Button btn_setIP;
    private String UserName,Password;
    private Context context;
    private static final String TAG = "LoginActivity";

    final String REMEMBER_PWD_PREF = "rememberPwd";
    final String AUTO_LOGIN_PREF = "autoLogin";
    final String ACCOUNT_PREF = "account";
    final String PASSWORD_PREF = "password";

    SharedPreferences preference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        ed_login_name = (EditText) findViewById(R.id.ed_login_name);
        ed_login_password = (EditText) findViewById(R.id.ed_login_password);
        checkbox_remember = (CheckBox) findViewById(R.id.checkbox_remember);
        checkbox_autoLogin = (CheckBox) findViewById(R.id.checkbox_autoLogin);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_setIP = (Button) findViewById(R.id.btn_setIP);

        btn_login.setOnClickListener(this);
        btn_setIP.setOnClickListener(this);
        context=this;

        //从 SharedPreferences 中获取【是否记住密码】参数
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean isRemember = preference.getBoolean(REMEMBER_PWD_PREF, false);
        boolean isAutoLogin = preference.getBoolean(AUTO_LOGIN_PREF, false);
        UserName=preference.getString(ACCOUNT_PREF,"");
        Password=preference.getString(PASSWORD_PREF,"");
        if (isRemember) {//设置【账号】与【密码】到文本框，并勾选【记住密码】
            ed_login_name.setText(preference.getString(ACCOUNT_PREF, ""));
            ed_login_password.setText(preference.getString(PASSWORD_PREF, ""));
            checkbox_remember.setChecked(true);
        }
        if(isAutoLogin && isRemember){
            checkbox_autoLogin.setChecked(true);
            LoadingDialog.showDialog(context);
            //延时
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Login(UserName,Password);
                }
            }, 2000);
        }
        checkbox_autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = preference.edit();
                if(b){
                    if(checkbox_remember.isChecked()) {
                        editor.putBoolean(AUTO_LOGIN_PREF,true);
                    }else{
                        MyToast.showInfo("请先把记住密码功能打开!");
                        checkbox_autoLogin.setChecked(false);
                    }
                }else {
                    editor.putBoolean(AUTO_LOGIN_PREF,false);
                }
                editor.apply();
            }
        });

    }
    public void Login(final String name, String password){
        JSONObject js=new JSONObject();
        try {
            js.put("UserName",name);
            js.put("UserPwd",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.request("user_login", js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("---user_login", ": " + jsonObject);
                try {
                    if (jsonObject.getString("RESULT").equals("S")) {
                        SharedPreferences.Editor editor = preference.edit();
                        if (checkbox_remember.isChecked()) {//记住账号与密码
                            editor.putBoolean(REMEMBER_PWD_PREF, true);
                            editor.putString(ACCOUNT_PREF, UserName);
                            editor.putString(PASSWORD_PREF, Password);
                        } else if (checkbox_autoLogin.isChecked()) {
                            editor.putBoolean(AUTO_LOGIN_PREF, true);
                        } else {//清空数据
                            editor.clear();
                            editor.putBoolean(REMEMBER_PWD_PREF, false);
                        }
                        editor.apply();
                        LoadingDialog.disDialog();
                        HttpRequest.setUserName(name);
                        String role=jsonObject.getString("UserRole");
                        HttpRequest.setRole(role);
                        MyToast.showSuccess( "登录" + jsonObject.getString("ERRMSG"));
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LoadingDialog.disDialog();
                MyToast.showError("登录失败");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                UserName=ed_login_name.getText().toString().trim();
                Password=ed_login_password.getText().toString().trim();
                if(UserName.isEmpty()||Password.isEmpty()){
                    MyToast.showInfo("用户名或密码输入为空！");
                }else{
                    LoadingDialog.showDialog(context);
                    //延时
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Login(UserName,Password);
                        }
                    }, 2000);
                }
                break;
            case R.id.btn_setIP:
                startActivity(new Intent(LoginActivity.this,SetIpActivity.class));
                break;
        }
    }
}
