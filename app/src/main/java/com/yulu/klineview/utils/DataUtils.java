package com.yulu.klineview.utils;

/**
 * 数据处理工具类
 */
public class DataUtils {
    // 四舍五入
    public static int getOver(long number) {
        // 进位
        int over = 0;
        // 最后一位
        int lastNumber = (int) number % 10;

        if (lastNumber >= 5) {
            over = 1;
        }
        return over;
    }

    public static double[] MA(double[] data, int para) {
        double[] average = new double[data.length];
        double p;
        for (int ii = 0; ii < data.length; ii++) {
            if (ii < para - 1) {
                average[ii] = -1;
                continue;
            } else {
                p = 0;
                for (int i = ii - para + 1; i <= ii; i++) {
                    p += data[i];
                }
                average[ii] = p / para;
            }
        }
        return average;
    }
}
