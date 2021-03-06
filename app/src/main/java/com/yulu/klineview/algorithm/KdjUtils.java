package com.yulu.klineview.algorithm;

import com.yulu.klineview.bean.QuotationBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * KDJ的工具类
 */
public class KdjUtils {

    public final static int[] kdj_default = {9, 3, 3};
    public final static String KDJ_K = "k";
    public final static String KDJ_D = "d";
    public final static String KDJ_J = "j";

    /**
     * @param quotationBeanList:数据
     * @param nDay：N天数，一般取9
     * @param kValue：M1天数，一般3
     * @param dValue：M2天数，一般3
     * @return
     */
    public static Map<String, double[]> getKDJ(List<QuotationBean> quotationBeanList, int nDay, int kValue, int dValue) {
        if (quotationBeanList == null || quotationBeanList.size() == 0)
            return null;
        double[] closeDatas = new double[quotationBeanList.size()];
        double[] minDatas = new double[quotationBeanList.size()];
        double[] maxDatas = new double[quotationBeanList.size()];
        double minTemp = -1;
        double maxTemp = -1;
        for (int ii = 0; ii < quotationBeanList.size(); ii++) {
            closeDatas[ii] = quotationBeanList.get(ii).getClose();
            double tempMin = quotationBeanList.get(ii).getLow();
            double tempMax = quotationBeanList.get(ii).getHigh();
            if (ii < nDay) {
                if (minTemp < 0) {
                    minTemp = tempMin;
                    maxTemp = tempMax;
                } else {
                    if (tempMin < minTemp) {
                        minTemp = tempMin;
                    }
                    if (tempMax > maxTemp) {
                        maxTemp = tempMax;
                    }
                }
            } else {
                minTemp = -1;
                maxTemp = -1;
                for (int index = ii - nDay + 1; index <= ii; index++) {
                    tempMin = quotationBeanList.get(ii).getLow();
                    tempMax = quotationBeanList.get(ii).getHigh();
                    if (minTemp < 0) {
                        minTemp = tempMin;
                        maxTemp = tempMax;
                    } else {
                        if (tempMin < minTemp) {
                            minTemp = tempMin;
                        }
                        if (tempMax > maxTemp) {
                            maxTemp = tempMax;
                        }
                    }
                }
            }

            minDatas[ii] = minTemp;
            maxDatas[ii] = maxTemp;
        }

        double[] rsv = new double[quotationBeanList.size()];
        double[] k = new double[quotationBeanList.size()];
        double[] d = new double[quotationBeanList.size()];
        double[] j = new double[quotationBeanList.size()];
        for (int ii = 0; ii < quotationBeanList.size(); ii++) {
            double tempValue = maxDatas[ii] - minDatas[ii];
            if (tempValue == 0)
                tempValue = 1;
            double tempRsv = (closeDatas[ii] - minDatas[ii]) * 100 * 100 * 10 / tempValue;
            rsv[ii] = tempRsv / 10 + tempRsv;
            if (ii > 0) {
                double beginNum = (kValue - 1) * k[ii - 1] * 10 / kValue;
                double endNum = (int) (rsv[ii] * 10 / kValue);
                k[ii] = (beginNum / 10 + beginNum)
                        + (endNum / 10 + endNum);
                beginNum = (dValue - 1) * d[ii - 1] * 10 / dValue;
                endNum = k[ii] * 10 / dValue;
                d[ii] = (beginNum / 10 + beginNum)
                        + (endNum / 10 + endNum);
                j[ii] = 3 * k[ii] - 2 * d[ii];
            } else {
                k[ii] = (int) rsv[ii];
                d[ii] = (int) rsv[ii];
                j[ii] = (int) rsv[ii];
            }
        }
        Map<String, double[]> map = new HashMap<>();
        map.put(KDJ_K, k);
        map.put(KDJ_D, d);
        map.put(KDJ_J, j);
        return map;
    }

}
