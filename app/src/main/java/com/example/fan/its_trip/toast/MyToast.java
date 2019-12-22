package com.example.fan.its_trip.toast;

import android.graphics.Color;
import android.widget.Toast;

import com.example.fan.its_trip.App;
import com.example.fan.its_trip.R;

import es.dmoral.toasty.Toasty;

/**
 * Created by xww on 17-9-1.
 */

public class MyToast {
    /**
     *错误Toast：
     */
    public static void showError(String mHint) {
        Toasty.error(App.appContext,
                ""+mHint, Toast.LENGTH_SHORT,true).show();
    }

    /**
     *成功Toast：
     */
    public static void showSuccess(String mHint) {
        Toasty.success(App.appContext,
                ""+mHint,Toast.LENGTH_SHORT,true).show();
    }

    /**
     *信息Toast：
     */
    public static void showInfo(String mHint) {
        Toasty.info(App.appContext,
                ""+mHint,Toast.LENGTH_SHORT,true).show();
    }

    /**
     *警告Toast：
     */
    public static void showWarning(String mHint) {
        Toasty.warning(App.appContext,
                ""+mHint,Toast.LENGTH_SHORT,true).show();
    }
    /***
     *通常的Toast
     */
    private void showUsual(String mHint) {
        Toasty.normal(App.appContext,mHint,Toast.LENGTH_SHORT).show();
    }

    /**
     *带有图标的常用Toast：
     */
    private void showIcon(String mHint) {
        Toasty.normal(App.appContext,mHint, R.mipmap.ic_launcher).show();
    }

    /**
     * 创建自定义Toasts ：
     */
    private void showCustom(String mHint) {
        Toasty.custom(App.appContext,mHint,
                R.mipmap.ic_launcher, Color.WHITE,Color.CYAN,Toast.LENGTH_SHORT,true,true).show();
    }
}
