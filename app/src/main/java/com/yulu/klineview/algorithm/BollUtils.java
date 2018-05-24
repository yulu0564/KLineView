package com.yulu.klineview.algorithm;


import com.yulu.klineview.bean.QuotationBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BOLL通道的工具类
 */
public class BollUtils {
    public final static int[] boll_default = {26, 2};  //boll的默认值标准差和宽度
    public final static String BOLL_MID = "midBoll";
    public final static String BOLL_UP = "upBoll";
    public final static String BOLL_LOW = "lowBoll";

    /**
     * 获取boll线的值
     *
     * @param day：标准差
     * @param width   ：宽度
     * @return
     */
    public static Map<String, double[]> getBollData(List<QuotationBean> quotationBeanList, int day, int width) {
        Map<String, double[]> map = new HashMap<String, double[]>();
        // N日均值
        double[] ma = MAUtils.calcAverageData(quotationBeanList, day);
        // N日标准差
        double[] md = getMDValue(quotationBeanList, ma, day);
        // 中轨(N-1日均值)
        double[] midBoll = MAUtils.calcAverageData(quotationBeanList, (day - 1));
        int mdSize = md.length;
        // 上轨
        double[] upBoll = new double[mdSize];
        // 下轨
        double[] lowBoll = new double[mdSize];

        for (int index = 0; index < mdSize; index++) {
            if (index < day - 1) {
                upBoll[index] = -1;
                lowBoll[index] = -1;
                continue;
            }
            upBoll[index] = midBoll[index] + width * md[index];
            lowBoll[index] = midBoll[index] - width * md[index];
        }
        map.put(BOLL_MID, midBoll);
        map.put(BOLL_UP, upBoll);
        map.put(BOLL_LOW, lowBoll);
        return map;
    }

    private static double[] getMDValue(List<QuotationBean> close, double[] ma, int day) {
        int size = close.size();
        double[] md = new double[size];
        for (int index = 0; index < size; index++) {
            if (index < day - 1) {
                md[index] = -1;
                continue;
            }
            double total = 0;
            for (int ii = index; ii >= (index - day + 1); ii--) {
                total += Math.pow((close.get(ii).getClose() - ma[index]), 2);
            }
            double value = Math.sqrt((total / (double) day));
            md[index] = value;
        }
        return md;
    }
}
