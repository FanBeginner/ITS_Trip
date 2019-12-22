/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.example.fan.its_trip.utils;

import android.content.Context;

public class NormalUtils {

    public static String getTTSAppID() {
        return "16934900";
    }

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
}
