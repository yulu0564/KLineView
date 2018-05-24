package com.yulu.klineview.algorithm;

import com.yulu.klineview.bean.QuotationBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MACD的工具类
 */
public class MacdUtils {
    public final static float[] macd_default = {12, 26, 9};  //默认值
    public final static String MACD_DIFF = "diff";
    public final static String MACD_DEA = "dea";
    public final static String MACD = "macd";

    /**
     * @param datas     :数据
     * @param shortTerm ：短期
     * @param longTerm  ：长期
     * @param m         ：M
     * @return
     */
    public static Map<String, double[]> getInitMacdData(List<QuotationBean> datas, float shortTerm, float longTerm, float m) {
        if (datas == null || datas.size() == 0)
            return null;
        /** diff */
        double[] t0 = new double[datas.size()];
        /** dea */
        double[] t1 = new double[datas.size()];
        /** macd */
        double[] t2 = new double[datas.size()];
        double[] t3 = new double[datas.size()];
        double[] t4 = new double[datas.size()];
        Map<String, double[]> map = new HashMap<>();
        t3[0] = (float) datas.get(0).getClose();
        t4[0] = (float) datas.get(0).getClose();
        double kkk12 = 2 / (shortTerm + 1);
        double kkk26 = 2 / (longTerm + 1);
        for (int i = 1; i < datas.size(); i++) {
            double close = datas.get(i).getClose();
            double f29 = t3[i - 1] + (close - t3[i - 1]);
            if (f29 != 0.0) {
                t3[i] = kkk12 * close + ((float) 1 - kkk12) * t3[i - 1];
            }
            f29 = t4[i - 1] + (close - t4[i - 1]);
            if (f29 != 0.0) {
                t4[i] = kkk26 * close + ((float) 1 - kkk26) * t4[i - 1];
            }
        }
        for (int j = 0; j < datas.size(); j++) {
            double f32 = t3[j] - t4[j];
            if (f32 != 0.0) {
                t0[j] = t3[j] - t4[j];
            }
        }
        double f33 = t0[0];
        if (f33 != 0.0) {
            t1[0] = t0[0];
        }
        for (int ii = 1; ii < datas.size(); ii++) {
            double f43 = t1[ii - 1] + (t0[ii] - t1[ii - 1]);
            if (f43 != 0.0) {
                t1[ii] = t1[ii - 1] + ((t0[ii] - t1[ii - 1]) * 2D)
                        / (m + 1);
            }
        }
        for (int jj = 0; jj < datas.size(); jj++) {
            double f48 = t0[jj] - t1[jj];
            if (f48 != 0.0) {
                t2[jj] = (float) ((t0[jj] - t1[jj]) * 2.0);
            }
        }
        map.put(MACD_DIFF, t0);
        map.put(MACD_DEA, t1);
        map.put(MACD, t2);
        return map;
    }
}
