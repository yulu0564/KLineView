package com.yulu.klineview.utils;

import android.content.Context;

/**
 * 获取屏幕相关的工具类
 */
public class DisplayUtils {
    private static float scale;

    private static void initScale(Context context) {
        if (scale == 0) {
            scale = context.getResources().getDisplayMetrics().density;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        initScale(context);
        return dpValue * scale + 0.5f;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        initScale(context);
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * px转sp
     */
    public static int px2sp(Context context, float pxValue) {
        initScale(context);
        return (int) (pxValue / scale + 0.5f);
    }

    public static float sp2px(Context context, float spValue) {
        initScale(context);
        return spValue * scale + 0.5f;
    }
}
