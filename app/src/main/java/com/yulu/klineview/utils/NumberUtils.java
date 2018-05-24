package com.yulu.klineview.utils;

import java.text.DecimalFormat;

/**
 * 数据处理工具类
 */

public class NumberUtils {
    /**
     * 保留两位有效数字
     */
    public static String getTwoStep(double vaule) {
        try {
            DecimalFormat df = new DecimalFormat("######0.00");
            return df.format(Math.round(vaule * 100) / 100.0);
        } catch (Exception e) {
        }
        return Math.round(vaule * 100) / 100.00f + "";
    }

    public static String getTwoStepStr(String vaule) {
        try {
            float vauleF = Float.parseFloat(vaule);
            String unit = "";
            if (Math.abs(vauleF) > 100000000) {
                unit = "亿";
                vauleF /= 100000000;
            } else if (Math.abs(vauleF) > 10000) {
                unit = "万";
                vauleF /= 10000;
            }
            if (vauleF == (int) vauleF) {
                return ((int) vauleF) + unit;
            } else {
                DecimalFormat df = new DecimalFormat("###########0.00");
                return df.format(Math.round(vauleF * 100) / 100.0) + unit;
            }
        } catch (Exception e) {
        }
        return "--";
    }

    public static String getTwoStepStr(double vaule) {
        try {
            String unit = "";
            if (Math.abs(vaule) > 100000000) {
                unit = "亿";
                vaule /= 100000000;
            } else if (Math.abs(vaule) > 10000) {
                unit = "万";
                vaule /= 10000;
            }
            if (vaule == (int) vaule) {
                return ((int) vaule) + unit;
            } else {
                DecimalFormat df = new DecimalFormat("###########0.00");
                return df.format(Math.round(vaule * 100) / 100.0) + unit;
            }
        } catch (Exception e) {
        }
        return "--";
    }
    /**
     * 保留一位有效数字
     */
    public static float getTwoStepFloat(double vaule) {
        return Math.round(vaule * 100) / 100.00f;
    }

}
