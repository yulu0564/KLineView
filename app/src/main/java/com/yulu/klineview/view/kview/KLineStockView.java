package com.yulu.klineview.view.kview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * K线图(主线程绘制）
 */
public class KLineStockView extends BaseKlineView {
    protected float[] kLWidthArray = new float[16]; // K线图的宽度
    protected int kLWidthSub = 5;

    protected int deviant = 0; // 偏移量

    protected int valueStock; // 显示的数量

    protected int leftDeviant = 0; // 左划了多少

    protected List<QuotationBean> showQuotationBeanList = new ArrayList<>(); // 可见的K线数据

    public KLineStockView(Context context) {
        this(context, null);
    }

    public KLineStockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KLineStockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        for (int i = 0; i < kLWidthArray.length; i++) {
            int num = 256 / (i + 1);
            kLWidthArray[i] = topRect.width() / num;
        }
        initData();
    }

    @Override
    protected void initDarw() {
        if (valueStock < mDatas.size()) {
            if (valueStock + leftDeviant > mDatas.size()) {
                leftDeviant = mDatas.size() - valueStock;
            }
            showQuotationBeanList = mDatas.subList(mDatas.size()
                    - valueStock - leftDeviant, mDatas.size()
                    - leftDeviant);
            deviant = mDatas.size() - valueStock - leftDeviant;
        } else {
            showQuotationBeanList = mDatas;
            deviant = 0;
        }
        setKLMaxAndMin();
    }

    /**
     * 画所有横向表格，包括X轴
     */
    @Override
    protected void drawAllXLine(Canvas mCanvas) {
        double ordinateValue = 0;
        if (minKL != 0 && maxKL > minKL) {
            ordinateValue = (maxKL - minKL) / 4.0f;
        }
        float cutoffHeight = topRect.height() / 4.0f;
        for (int i = 0; i < 5; i++) {
            float cutoffY = cutoffHeight * i + topRect.top;
            if (i != 0 && i != 4) {
//                mCanvas.drawLine(topRect.left, cutoffY, topRect.right, cutoffY, mPaint);// X坐标
                Path path = new Path();
                path.moveTo(topRect.left, cutoffY);
                path.lineTo(topRect.right, cutoffY);
                mCanvas.drawPath(path, mXLinePaint);
            }
            if (ordinateValue != 0 && i % 2 == 0) {
                int textColor;
                if (i < 2) {
                    textColor = colorRise;
                } else if (i == 2) {
                    textColor = textDefaultColor;
                } else {
                    textColor = colorFall;
                }
                if (i == 0) {
                    cutoffY += dip2px(8);
                } else if (i != 4) {
                    cutoffY += dip2px(4);
                }
                setText(NumberUtils.getTwoStep(maxKL - ordinateValue * i),
                        topRect.left, cutoffY, mCanvas, Paint.Align.LEFT,
                        textColor, 10);
            }
        }
        // 副图
        cutoffHeight = bottomRect.height() / 3;
        ordinateValue = (maxFT - minFT) / 3.0f;
        for (int i = 0; i < 4; i++) {
            float cutoffY = cutoffHeight * i + bottomRect.top;
            if (i != 0 && i != 3) {
                Path path = new Path();
                path.moveTo(bottomRect.left, cutoffY);
                path.lineTo(bottomRect.right, cutoffY);
                mCanvas.drawPath(path, mXLinePaint);
            }
            if (i == 0) {
                cutoffY += dip2px(8);
            } else if (i == 1) {
                cutoffY += dip2px(4);
            }
            // 设置副图的坐标
            if (maxFT > minFT && i == 0) {
                String title = NumberUtils.getTwoStep((maxFT - ordinateValue * i));
                setText(title, bottomRect.right, cutoffY,
                        mCanvas, Paint.Align.RIGHT, textDefaultColor, 10);
            }
        }
    }

    /**
     * 边框
     */
    protected void drawBorder(Canvas mCanvas) {
        mCanvas.drawRect(topRect.left - borderlineWidth, topRect.top - borderlineWidth, topRect.right + borderlineWidth, topRect.bottom + borderlineWidth, mBorderPaint);
        mCanvas.drawRect(bottomRect.left - borderlineWidth, bottomRect.top - borderlineWidth, bottomRect.right + borderlineWidth, bottomRect.bottom + borderlineWidth, mBorderPaint);
    }

    /**
     * 画所有纵向表格，包括Y轴
     */
    @Override
    protected void drawAllYLine(Canvas mCanvas) {
        float cutoffWidth = topRect.width() / 4.0f;
        lastDate = null;
        for (int i = 0; i < 5; i++) {
            float cutoffX = cutoffWidth * i + topRect.left;
            if (i == 0) {
                if (showQuotationBeanList != null && showQuotationBeanList.size() > 0) {
                    String time = getStockDate(showQuotationBeanList.get(0)
                            .getTime());
                    setText(time, cutoffX, topRect.bottom + dip2px(10), mCanvas, Paint.Align.LEFT, textDefaultColor,
                            10);
                }
            } else if (i == 4) {
                if (showQuotationBeanList != null && showQuotationBeanList.size() > 0&&valueStock>0) {
                    if (showQuotationBeanList.size() == valueStock) {
                        String time = getStockDate(showQuotationBeanList.get(
                                showQuotationBeanList.size() - 1).getTime());
                        setText(time, cutoffX, topRect.bottom
                                        + dip2px(10), mCanvas, Paint.Align.RIGHT, textDefaultColor,
                                10);
                    } else if (showQuotationBeanList.size() > valueStock * i / 4) {
                        String time = getStockDate(showQuotationBeanList.get(
                                valueStock * i / 4).getTime());
                        setText(time, cutoffX, topRect.bottom + dip2px(10), mCanvas, Paint.Align.CENTER, textDefaultColor,
                                10);
                    }
                }

            }
        }
    }

    // 绘制K线图
    @Override
    protected void drawKLine(Canvas mCanvas) {
        int indicateLineIndex = 0;
        float indicateLineY = 0;
        float lastY5 = -1, lastY10 = -1, lastY30 = -1;
        lastX = -1; // 绘图时X的历史值
        float startX = bottomRect.left;
        for (int i = 0; i < showQuotationBeanList.size(); i++) {
            QuotationBean mQuotationBean = showQuotationBeanList.get(i);
            double open = mQuotationBean.getOpen(); // 开盘价
            double close = mQuotationBean.getClose(); // 收盘价
            double high = mQuotationBean.getHigh(); // 最高价
            double low = mQuotationBean.getLow(); // 最低价
//            float volume = mQuotationBean.getVolume(); // 成交量
            double amount = mQuotationBean.getAmount(); // 成交额

            float highY = getCutoffKLY(high); // 最高价的坐标
            float lowY = getCutoffKLY(low); // 最低价的坐标
            float openY = getCutoffKLY(open); // 开盘价的坐标
            float closeY = getCutoffKLY(close); // 收盘价的坐标
//            int state = mQuotationBean.getState();

            // 五日十日三十日均线
            float avgY5 = 0;
            float avgY10 = 0;
            float avgY30 = 0;
            if (initAverageData5 != null
                    && initAverageData5.length > i + deviant) {
                avgY5 = getCutoffKLY((initAverageData5[i + deviant]));
            }
            if (initAverageData10 != null
                    && initAverageData10.length > i + deviant) {
                avgY10 = getCutoffKLY((initAverageData10[i + deviant]));
            }
            if (initAverageData30 != null
                    && initAverageData30.length > i + deviant) {
                avgY30 = getCutoffKLY((initAverageData30[i + deviant]));
            }

            float teamLastX = startX + kLWidthArray[kLWidthSub] / 2;
            if (i != 0 && initAverageData5[i + deviant - 1] > 0) {
                mCanvas.drawLine(lastX, lastY5, teamLastX, avgY5, avgY5Paint);
            }
            if (i != 0 && initAverageData10[i + deviant - 1] > 0) {
                mCanvas.drawLine(lastX, lastY10, teamLastX, avgY10, avgY10Paint);
            }
            if (i != 0 && initAverageData30[i + deviant - 1] > 0) {
                mCanvas.drawLine(lastX, lastY30, teamLastX, avgY30, avgY30Paint);
            }

            Paint mDrawPaint;
            if (close < open) {
                mDrawPaint = mFallPaint;
            } else if (close > open) {
                mDrawPaint = mRisePaint;
            } else {
                mDrawPaint = mPingPaint;
            }

            float kLstartX = startX + 1;
            float endX = startX + kLWidthArray[kLWidthSub] - 1;
            mCanvas.drawLine(startX + kLWidthArray[kLWidthSub] / 2, closeY,
                    teamLastX, highY, mDrawPaint);
            mCanvas.drawLine(startX + kLWidthArray[kLWidthSub] / 2, openY,
                    teamLastX, lowY, mDrawPaint);
            kLstartX += dip2px(0.3f);
            endX -= dip2px(0.3f);
            if (close > open) {
                mCanvas.drawRect(kLstartX, closeY, endX, openY, mDrawPaint);
            } else {
                mCanvas.drawRect(kLstartX, openY, endX, closeY, mDrawPaint);
            }
            mCanvas.drawRect(kLstartX, getCutoffFTY(amount), endX, bottomRect.bottom, mDrawPaint);
            if (isShowIndicateLine && scollX >= startX
                    && scollX < startX + kLWidthArray[kLWidthSub]) {
                scollX = teamLastX;
                indicateLineIndex = i;
                indicateLineY = closeY;
            }
            lastY5 = avgY5;
            lastY10 = avgY10;
            lastY30 = avgY30;
            lastX = teamLastX;
            startX += kLWidthArray[kLWidthSub];
        }
        drawIndicateLine(mCanvas, indicateLineIndex + deviant, indicateLineY);
    }


    @Override
    protected void initData() {
        leftDeviant = 0;
        valueStock = (int) (topRect.width() / kLWidthArray[kLWidthSub]);
        initDarw();
        super.initData();
    }


    public void setLleftDeviant(int leftDeviant) {
        this.leftDeviant = leftDeviant;
    }

    /**
     * 设置K线坐标的最大和最小值
     */
    public void setKLMaxAndMin() {
        if (showQuotationBeanList.size() == 0) {
            return;
        }
        minKL = showQuotationBeanList.get(0).getLow();
        maxKL = showQuotationBeanList.get(0).getHigh();
        minFT = 0;
        maxFT = showQuotationBeanList.get(0).getAmount();
        for (int i = 0; i < showQuotationBeanList.size(); i++) {
            QuotationBean mQuotationBean = showQuotationBeanList.get(i);
            minKL = minKL < mQuotationBean.getLow() ? minKL : mQuotationBean.getLow();
            maxKL = maxKL > mQuotationBean.getHigh() ? maxKL : mQuotationBean
                    .getHigh();

            maxFT = maxFT > mQuotationBean.getAmount() ? maxFT : mQuotationBean
                    .getAmount();
            if (initAverageData5 != null
                    && initAverageData5.length > i + deviant
                    && initAverageData5[i + deviant] > 0) {
                minKL = minKL < initAverageData5[i + deviant] ? minKL
                        : initAverageData5[i + deviant];
                maxKL = maxKL > initAverageData5[i + deviant] ? maxKL
                        : initAverageData5[i + deviant];
            }
            if (initAverageData10 != null
                    && initAverageData10.length > i + deviant
                    && initAverageData10[i + deviant] > 0) {
                minKL = minKL < initAverageData10[i + deviant] ? minKL
                        : initAverageData10[i + deviant];
                maxKL = maxKL > initAverageData10[i + deviant] ? maxKL
                        : initAverageData10[i + deviant];
            }
            if (initAverageData30 != null
                    && initAverageData30.length > i + deviant
                    && initAverageData30[i + deviant] > 0) {
                minKL = minKL < initAverageData30[i + deviant] ? minKL
                        : initAverageData30[i + deviant];
                maxKL = maxKL > initAverageData30[i + deviant] ? maxKL
                        : initAverageData30[i + deviant];
            }
        }
        if (maxKL == minKL) {
            maxKL *= 1.1f;
            minKL *= 0.9f;
        }
    }

    /**
     * 设置副图坐标的最大和最小值
     */
    public void setFTMaxAndMin(float[]... array) {
        minFT = 0;
        maxFT = 0;
        if (array == null) {
            return;
        }
        if (array[0] != null && array[0].length > 0) {
            maxFT = array[0][deviant];
            minFT = array[0][deviant];
        }
        for (int i = 0; i < array.length; i++) {
            float[] s = array[i];
            if (s == null) {
                continue;
            }
            for (int ii = 0; ii < showQuotationBeanList.size(); ii++) {
                minFT = minFT < s[ii + deviant] ? minFT
                        : s[ii + deviant];
                maxFT = maxFT > s[ii + deviant] ? maxFT
                        : s[ii + deviant];
            }
        }
    }

    public void setFTMaxAndMin(int[]... array) {
        minFT = 0;
        maxFT = 0;
        if (array == null) {
            return;
        }
        if (array[0] != null && array[0].length > 0) {
            maxFT = array[0][deviant];
            minFT = array[0][deviant];
        }
        for (int i = 0; i < array.length; i++) {
            int[] s = array[i];
            if (s == null) {
                continue;
            }
            for (int ii = 0; ii < showQuotationBeanList.size(); ii++) {
                minFT = minFT < s[ii + deviant] ? minFT : s[ii + deviant];
                maxFT = maxFT > s[ii + deviant] ? maxFT : s[ii + deviant];
            }
        }
    }

}
