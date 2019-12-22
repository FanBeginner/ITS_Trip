package com.example.fan.its_trip.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fan.its_trip.App;
import com.example.fan.its_trip.R;
import com.example.fan.its_trip.toast.MyToast;

/**
 * Created by Fan on 2019/7/5.
 */

public class SetIpActivity extends BaseActivity implements View.OnClickListener {
    private EditText ed_seting_ip;
    private EditText ed_seting_port;
    private Button btn_seting_save;
    private Button btn_seting_cancel;
    private SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setip);
        initView();
    }

    private void initView() {
        ed_seting_ip = (EditText) findViewById(R.id.ed_seting_ip);
        ed_seting_port = (EditText) findViewById(R.id.ed_seting_port);
        btn_seting_save = (Button) findViewById(R.id.btn_seting_save);
        btn_seting_cancel = (Button) findViewById(R.id.btn_seting_cancel);

        btn_seting_save.setOnClickListener(this);
        btn_seting_cancel.setOnClickListener(this);

        pref= App.appContext.getSharedPreferences("setting",MODE_PRIVATE);
        ed_seting_ip.setText(pref.getString("ipUrl",""));
        ed_seting_port.setText(pref.getString("ipPort",""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_seting_save:
                String ipUrl=ed_seting_ip.getText().toString().trim();
                String ipPort=ed_seting_port.getText().toString().trim();
                if(ipUrl.isEmpty()||ipPort.isEmpty()){
                    MyToast.showInfo("输入不能为空！");
                }else{
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("ipUrl",ipUrl);
                    editor.putString("ipPort",ipPort);
                    editor.apply();
                    finish();
                }
                break;
            case R.id.btn_seting_cancel:
                finish();
                break;
        }
    }
}
