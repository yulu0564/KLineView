package com.yulu.klineview.view.kview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.yulu.klineview.algorithm.BollUtils;
import com.yulu.klineview.algorithm.MAUtils;
import com.yulu.klineview.model.TargetManager;
import com.yulu.klineview.utils.NumberUtils;

import java.util.Map;

/**
 * K线基础数据
 */

public abstract class BaseKlineBarView extends BaseKlineView {


    protected double[] initAverageData5 = null;
    protected double[] initAverageData10 = null;
    protected double[] initAverageData30 = null;

    public BaseKlineBarView(Context context) {
        this(context, null);
    }

    public BaseKlineBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseKlineBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 绘制头部M5等一些的内容
     */
    protected void drawTop(Canvas mCanvas, int indicateLineIndex) {
        String titleM5;
        String titleM10;
        String titleM30;
        if (TARGET_HEADER_INDEX == 0) {
            titleM5 = "M" + TargetManager.getInstance().getMaDefault()[0] + ":";
            titleM10 = "M" + TargetManager.getInstance().getMaDefault()[1]
                    + ":";
            titleM30 = "M" + TargetManager.getInstance().getMaDefault()[2]
                    + ":";
        } else {
            titleM5 = "M:";
            titleM10 = "U:";
            titleM30 = "L:";
        }
        if (initAverageData5 != null
                && initAverageData5.length > indicateLineIndex
                && initAverageData5[indicateLineIndex] > 0) {
            titleM5 = titleM5
                    + NumberUtils.getTwoStep(initAverageData5[indicateLineIndex]);
        } else {
            titleM5 = titleM5 + "--";
        }
        float x = setTextR(titleM5, topRect.left + offsetWidth, topRect.top - dip2px(5), mCanvas, Paint.Align.LEFT, colorAvlData5, 10);
        if (initAverageData10 != null
                && initAverageData10.length > indicateLineIndex
                && initAverageData10[indicateLineIndex] > 0) {
            titleM10 = titleM10
                    + NumberUtils.getTwoStep(initAverageData10[indicateLineIndex]);
        } else {
            titleM10 = titleM10 + "--";
        }
        x = setTextR(titleM10,
                x + dip2px(15), topRect.top - dip2px(5), mCanvas, Paint.Align.LEFT,
                colorAvlData10, 10);
        if (initAverageData30 != null
                && initAverageData30.length > indicateLineIndex
                && initAverageData30[indicateLineIndex] > 0) {
            titleM30 = titleM30
                    + NumberUtils.getTwoStep(initAverageData30[indicateLineIndex]);
        } else {
            titleM30 = titleM30 + "--";
        }
        setText(titleM30, x + dip2px(15),
                topRect.top - dip2px(5), mCanvas, Paint.Align.LEFT,
                colorAvlData30, 10);
    }

    @Override
    protected void initData() {
        super.initData();
        Map<String, double[]> averageMap = null;
        if (TARGET_HEADER_INDEX == 0) {
            int day5 = TargetManager.getInstance().getMaDefault()[0];
            int day10 = TargetManager.getInstance().getMaDefault()[1];
            int day30 = TargetManager.getInstance().getMaDefault()[2];
            averageMap = MAUtils.getInitAverageData(mDatas, day5,
                    day10, day30);
        } else if (TARGET_HEADER_INDEX == 1) {
            averageMap = BollUtils.getBollData(mDatas, TargetManager
                    .getInstance().getBollDefault()[0], TargetManager
                    .getInstance().getBollDefault()[1]);
        }
        if (averageMap != null) {
            initAverageData5 = averageMap.get(MAUtils.MA_5);
            initAverageData10 = averageMap.get(MAUtils.MA_10);
            initAverageData30 = averageMap.get(MAUtils.MA_30);
        }
    }

    /**
     * 主图技术指标索引
     */
    public void setTargetHeaderIndex(int targetHeaderIndex) {
        this.TARGET_HEADER_INDEX = targetHeaderIndex;
        Map<String, double[]> averageMap = null;
        if (TARGET_HEADER_INDEX == 0) {
            int day5 = TargetManager.getInstance().getMaDefault()[0];
            int day10 = TargetManager.getInstance().getMaDefault()[1];
            int day30 = TargetManager.getInstance().getMaDefault()[2];
            averageMap = MAUtils.getInitAverageData(mDatas, day5,
                    day10, day30);
        } else if (TARGET_HEADER_INDEX == 1) {
            averageMap = BollUtils.getBollData(mDatas, TargetManager
                    .getInstance().getBollDefault()[0], TargetManager
                    .getInstance().getBollDefault()[1]);
        }
        if (averageMap != null) {
            initAverageData5 = averageMap.get(MAUtils.MA_5);
            initAverageData10 = averageMap.get(MAUtils.MA_10);
            initAverageData30 = averageMap.get(MAUtils.MA_30);
        }
    }


}
