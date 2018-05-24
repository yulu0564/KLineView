package com.yulu.klineview.algorithm;


import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.utils.DataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Bias工具类
 */
public class BiasUtils {
    public final static int[] bias_default = {6, 12, 24};
    public final static String BIAS6 = "bias6";
    public final static String BIAS12 = "bias12";
    public final static String BIAS24 = "bias24";

    public static Map<String, double[]> getBias(List<QuotationBean> quotationBeanList, int day1, int day2, int day3) {
        if (quotationBeanList == null || quotationBeanList.size() == 0)
            return null;
        int size = quotationBeanList.size();
        double[] allClosePrice = new double[size];
        for (int i = 0; i < size; i++) {
            allClosePrice[i] = quotationBeanList.get(i).getClose();
        }
        double[] avg6 = DataUtils.MA(allClosePrice, day1);
        double[] avg12 = DataUtils.MA(allClosePrice, day2);
        double[] avg24 = DataUtils.MA(allClosePrice, day3);
        Map<String, double[]> map = new HashMap<>();
        map.put(BIAS6, getBiasValue(allClosePrice, avg6, day1));
        map.put(BIAS12, getBiasValue(allClosePrice, avg12, day2));
        map.put(BIAS24, getBiasValue(allClosePrice, avg24, day3));
        return map;
    }

    // 几天内的bias值
    private static double[] getBiasValue(double[] allClosePrice, double[] avg, int days) {
        int size = allClosePrice.length;
        double[] bias = new double[size];
        for (int i = 0; i < size; i++) {
            if (i < days - 1) {
                bias[i] = -1;
                continue;
            }
            double result = (allClosePrice[i] - avg[i]) / avg[i] * 10000;
            bias[i] = result;
        }
        return bias;
    }
}
