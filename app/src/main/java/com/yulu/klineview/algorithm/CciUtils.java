package com.yulu.klineview.algorithm;


import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.model.TargetManager;
import com.yulu.klineview.utils.DataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CCI算法工具类
 */
public class CciUtils {
    public final static String CCI = "cci";

    public static Map<String, double[]> getCCI(List<QuotationBean> quotationBeanList, int days) {
        if (quotationBeanList == null || quotationBeanList.size() == 0)
            return null;
        int size = quotationBeanList.size();
        double[] allClosePrice = new double[size];
        double[] tp = new double[size];
        for (int ii = 0; ii < size; ii++) {
            allClosePrice[ii] = quotationBeanList.get(ii).getClose();
            double high = quotationBeanList.get(ii).getHigh();
            double low = quotationBeanList.get(ii).getLow();
            double close = quotationBeanList.get(ii).getClose();
            tp[ii] = (high + low + close) / 3;
        }
        double[] avg14 = DataUtils.MA(tp, days);
        double[] avgMD = avgdev(tp, avg14, days);
        Map<String, double[]> map = new HashMap<>();
        map.put(CCI, getCCIValue(tp, avg14, avgMD, days));
        return map;
    }

    public static Map<String, double[]> getCCI(List<QuotationBean> quotationBeanList) {
        return getCCI(quotationBeanList, TargetManager.getInstance().getCciDefault());
    }


    private static double[] avgdev(double[] tp, double[] avg14, int para) {

        double[] average = new double[tp.length];
        double p = 0;
        double avg = 0;
        for (int ii = 0; ii < tp.length; ii++) {
            avg = avg14[ii];
            if (ii < para - 1) {
                average[ii] = -1;
                continue;
            } else {
                p = 0;
                for (int i = ii - para + 1; i <= ii; i++) {
                    p += Math.abs(tp[i] - avg);
                }
                average[ii] = p / para;
            }
        }
        return average;
    }

    // 几天内的cci值
    private static double[] getCCIValue(double[] tp, double[] avg14, double[] avgMD, int days) {
        int size = tp.length;
        double[] cci = new double[size];
        for (int ii = 0; ii < size; ii++) {
            if (ii < days - 1) {
                cci[ii] = 0;
                continue;
            }
            double result = (tp[ii] - avg14[ii]) / (0.015 * avgMD[ii]) * 100;
            cci[ii] = result;
        }
        return cci;
    }
}
