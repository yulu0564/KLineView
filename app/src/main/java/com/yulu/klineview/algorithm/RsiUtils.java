package com.yulu.klineview.algorithm;
import com.yulu.klineview.bean.QuotationBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RSI（相对强弱指标）工具类
 */
public class RsiUtils {
    public final static int[] rsi_default = {6, 12, 24};
    public final static String RSI6 = "rsi6";
    public final static String RSI12 = "rsi12";
    public final static String RSI24= "rsi24";


    public static Map<String, double[]> getRSIData(List<QuotationBean> closePrices, int day6, int day12, int day24) {
        Map<String, double[]> map = new HashMap<>();
        map.put(RSI6, getLongResult(day6, closePrices));
        map.put(RSI12, getLongResult(day12, closePrices));
        map.put(RSI24, getLongResult(day24, closePrices));
        return map;

    }

    public static double[] getLongResult(int n, List<QuotationBean> closePrices) {
        if (closePrices == null || n > closePrices.size())
            return null;
        double[] data = new double[closePrices.size()];
        for (int i = 0; i < n; i++) {
            data[i] = -1;
        }
        for (int i = n; i < closePrices.size(); i++) {
            double tempA = getA(n, i, closePrices);
            double tempB = getB(n, i, closePrices);
            if ((tempA + tempB) == 0) {
                data[i] = 0;
            } else {
                data[i] = (tempA * 100L * 100L) / (tempA + tempB);
            }
        }
        return data;
    }

    // 计算n天内的涨幅
    private static double getA(int n, int index, List<QuotationBean> datas) {
        double value = 0;
        for (int i = 0; i <= n - 1; i++) {
            double data = datas.get(index - i).getClose();
            double preData = datas.get(index - i - 1).getClose();
            double temp = data - preData;
            if (temp > 0) {
                value += temp;
            }
        }
        return value;
    }

    // 计算n天内的跌幅
    private static double getB(int n, int index, List<QuotationBean> datas) {
        double value = 0;
        for (int i = 0; i <= n - 1; i++) {
            double data = datas.get(index - i).getClose();
            double preData = datas.get(index - i - 1).getClose();
            double temp = data - preData;
            if (temp < 0) {
                value += temp;
            }
        }
        return -value;
    }
}
