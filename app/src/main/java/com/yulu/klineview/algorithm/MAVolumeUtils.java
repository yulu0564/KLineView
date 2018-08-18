package com.yulu.klineview.algorithm;

import com.yulu.klineview.bean.QuotationBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MA均价的算法
 */
public class MAVolumeUtils {
    public final static String MA_5 = "5";
    public final static String MA_10 = "10";
    public final static String MA_30 = "30";

    /**
     * 获取5、10、30均线数据，para代表日数
     */
    public static double[] calcAverageData(List<QuotationBean> quotationBeanList, int para) {
        double[] average = new double[quotationBeanList.size()];
        double p;
        for (int ii = 0; ii < quotationBeanList.size(); ii++) {
            if (ii < para - 1) {
                average[ii] = -1;
                continue;
            }
            p = 0;
            for (int i = ii - para + 1; i <= ii; i++) {
                p += quotationBeanList.get(i).getVolume();   //昨收价相加
            }
            average[ii] = p / para;
        }
        return average;
    }

    public static Map<String, double[]> getInitAverageData(List<QuotationBean> quotationBeanList, int para0, int para1, int para2) {

        Map<String, double[]> map = new HashMap<>();
        map.put(MA_5,
                calcAverageData(quotationBeanList, para0));
        map.put(MA_10,
                calcAverageData(quotationBeanList, para1));
        map.put(MA_30,
                calcAverageData(quotationBeanList, para2));
        return map;
    }
}
