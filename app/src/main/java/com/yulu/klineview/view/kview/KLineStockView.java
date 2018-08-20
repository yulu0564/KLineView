package com.yulu.klineview.view.kview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.yulu.klineview.algorithm.BiasUtils;
import com.yulu.klineview.algorithm.CciUtils;
import com.yulu.klineview.algorithm.KdjUtils;
import com.yulu.klineview.algorithm.MacdUtils;
import com.yulu.klineview.algorithm.RsiUtils;
import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.model.TargetManager;
import com.yulu.klineview.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * K线图(主线程绘制）
 */
public class KLineStockView extends BaseKlineBarView {

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
        switch (TARGET_FOOTER_INDEX) {
            case 1:
                if (macdMap != null) {
                    setFTMaxAndMin(deviant,showQuotationBeanList.size()+deviant,macdMap.get(MacdUtils.MACD_DEA), macdMap.get(MacdUtils.MACD_DIFF),
                            macdMap.get(MacdUtils.MACD));
                }
                break;
            case 2:
                if (kdjMap != null) {
                    setFTMaxAndMin(deviant,showQuotationBeanList.size()+deviant,kdjMap.get(KdjUtils.KDJ_K), kdjMap.get(KdjUtils.KDJ_D),
                            kdjMap.get(KdjUtils.KDJ_J));
                }
                break;
            case 3:
                minFT = 0;
                maxFT = 100;
                break;
            case 4:
                if (biasMap != null) {
                    setFTMaxAndMin(deviant,showQuotationBeanList.size()+deviant,biasMap.get("bias6"), biasMap.get("bias12"),
                            biasMap.get("bias24"));
                }
                break;
            case 5:
                if (cciMap != null) {
                    setFTMaxAndMin(deviant,showQuotationBeanList.size()+deviant,cciMap.get("cci"));
                }
                break;
        }
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
            if (maxFT > minFT && (i == 0 || i == 3)) {
                String title = null;
                switch (TARGET_FOOTER_INDEX) {
                    case 0:
                        title = NumberUtils.getTwoStep((maxFT - ordinateValue * i));
                        break;
                    case 1:
                    case 2:
                        if (i == 0) {
                            title = NumberUtils.getTwoStep(maxFT / 100.0f);
                        } else if (i == 3) {
                            title = NumberUtils.getTwoStep(minFT / 100.0f);
                        }
                        break;
                    case 3:
                        if (i == 0) {
                            title = "100";
                        } else if (i == 3) {
                            title = "0";
                        } else {
                            title = "";
                        }
                        break;
                    case 4:
                    case 5:
                        if (i == 0) {
                            title = NumberUtils.getTwoStep(maxFT / 100.0);
                        } else if (i == 3) {
                            title = NumberUtils.getTwoStep(minFT / 100.0);
                        }
                        break;
                }
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
                if (showQuotationBeanList != null && showQuotationBeanList.size() > 0 && valueStock > 0) {
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

        float diffY = -1;
        float deaY = -1;

        // KDJ
        float kY = -1;
        float dY = -1;
        float jY = -1;

        // rsi
        float rsi6Y = -1;
        float rsi12Y = -1;
        float rsi24Y = -1;

        // bias
        float bias6Y = -1;
        float bias12Y = -1;
        float bias24Y = -1;

        // cci
        float cciY = -1;

        for (int i = 0; i < showQuotationBeanList.size(); i++) {
            QuotationBean mQuotationBean = showQuotationBeanList.get(i);
            double open = mQuotationBean.getOpen(); // 开盘价
            double close = mQuotationBean.getClose(); // 收盘价
            double high = mQuotationBean.getHigh(); // 最高价
            double low = mQuotationBean.getLow(); // 最低价
            double volume = mQuotationBean.getVolume(); // 成交量
//            double amount = mQuotationBean.getAmount(); // 成交额

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

            float teamLastX = startX + kLWidth/ 2;
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
            float endX = startX + kLWidth - 1;
            mCanvas.drawLine(startX + kLWidth/ 2, closeY,
                    teamLastX, highY, mDrawPaint);
            mCanvas.drawLine(startX + kLWidth / 2, openY,
                    teamLastX, lowY, mDrawPaint);
            kLstartX += dip2px(0.3f);
            endX -= dip2px(0.3f);
            if (close > open) {
                mCanvas.drawRect(kLstartX, closeY, endX, openY, mDrawPaint);
            } else {
                mCanvas.drawRect(kLstartX, openY, endX, closeY, mDrawPaint);
            }
            switch (TARGET_FOOTER_INDEX) {
                case 0:
                    // VOL图
                    mCanvas.drawRect(kLstartX, getCutoffFTY(volume), endX, bottomRect.bottom, mDrawPaint);
                    break;
                case 1:
                    // 绘制MACD图
                    float farPointsY = getCutoffFTY(0);
                    if (macdMap != null) {
                        if (macdMap.get("macd") != null && macdMap.get("macd")[i + deviant] != 0) {
                            Paint mMacdPaint;
                            if (close < open) {
                                mMacdPaint = mFallPaint;
                            } else if (close > open) {
                                mMacdPaint = mRisePaint;
                            } else {
                                mMacdPaint = mPingPaint;
                            }
                            mCanvas.drawLine(startX + kLWidth / 2,
                                    farPointsY, teamLastX, getCutoffFTY(macdMap.get("macd")[i
                                            + deviant]), mMacdPaint);
                        }

                        if (macdMap.get("diff") != null) {
                            float tempDiffY = getCutoffFTY(macdMap.get("diff")[i + deviant]); // 最新数据
                            if (i != 0) {
                                mCanvas.drawLine(lastX, diffY, teamLastX,
                                        tempDiffY, avgY30Paint);
                            }
                            diffY = tempDiffY;
                        }
                        if (macdMap.get("dea") != null) {
                            float tempDeaY = getCutoffFTY(macdMap.get("dea")[i + deviant]); // 最新数据
                            if (i != 0) {
                                mCanvas.drawLine(lastX, deaY, teamLastX, tempDeaY,
                                        avgY10Paint);
                            }
                            deaY = tempDeaY;
                        }
                    }
                    break;
                case 2:
                    // 绘制KDJ图
                    if (kdjMap != null) {
                        if (kdjMap.get(KdjUtils.KDJ_K) != null) {
                            float tempKY = getCutoffFTY(kdjMap.get(KdjUtils.KDJ_K)[i + deviant]);
                            if (i != 0) {
                                mCanvas.drawLine(lastX, kY, teamLastX, tempKY,
                                        avgY5Paint);
                            }
                            kY = tempKY;
                        }
                        if (kdjMap.get(KdjUtils.KDJ_D) != null) {
                            float tempDY = getCutoffFTY(kdjMap.get(KdjUtils.KDJ_D)[i + deviant]);
                            if (i != 0) {
                                mCanvas.drawLine(lastX, dY, teamLastX, tempDY,
                                        avgY10Paint);
                            }
                            dY = tempDY;
                        }
                        if (kdjMap.get(KdjUtils.KDJ_J) != null) {
                            float tempJY = getCutoffFTY(kdjMap.get(KdjUtils.KDJ_J)[i + deviant]);
                            if (i != 0) {
                                mCanvas.drawLine(lastX, jY, teamLastX, tempJY,
                                        avgY30Paint);
                            }
                            jY = tempJY;
                        }

                    }
                    break;
                case 3:
                    // 绘制RIS相对强弱指标
                    if (rsiMap != null) {
                        float tempRsi6 = getCutoffFTY(rsiMap.get(RsiUtils.RSI6)[deviant + i] / 100.0);
                        float tempRsi12 = getCutoffFTY(rsiMap.get(RsiUtils.RSI12)[deviant + i] / 100.0);
                        float tempRsi24 = getCutoffFTY(rsiMap.get(RsiUtils.RSI24)[deviant + i] / 100.0);
                        if (i != 0) {
                            if (rsiMap.get(RsiUtils.RSI6) != null && rsiMap.get(RsiUtils.RSI6)[deviant + i - 1] >= 0) {
                                mCanvas.drawLine(lastX, rsi6Y, teamLastX, tempRsi6,
                                        avgY5Paint);

                            }
                            if (rsiMap.get(RsiUtils.RSI12) != null && rsiMap.get(RsiUtils.RSI12)[deviant + i - 1] >= 0) {
                                mCanvas.drawLine(lastX, rsi12Y, teamLastX,
                                        tempRsi12, avgY10Paint);

                            }
                            if (rsiMap.get(RsiUtils.RSI24) != null && rsiMap.get(RsiUtils.RSI24)[deviant + i - 1] >= 0) {
                                mCanvas.drawLine(lastX, rsi24Y, teamLastX,
                                        tempRsi24, avgY30Paint);
                            }
                        }
                        rsi6Y = tempRsi6;
                        rsi12Y = tempRsi12;
                        rsi24Y = tempRsi24;

                    }
                    break;
                case 4:
                    // BIAS线
                    if (biasMap != null) {
                        int bias1 = Integer.valueOf(TargetManager.getInstance().getBiasDefault()[0]);
                        int bias2 = Integer.valueOf(TargetManager.getInstance().getBiasDefault()[1]);
                        int bias3 = Integer.valueOf(TargetManager.getInstance().getBiasDefault()[2]);
                        if (biasMap.get(BiasUtils.BIAS6) != null) {
                            float tempBias6Y = getCutoffFTY(biasMap.get(BiasUtils.BIAS6)[i + deviant]);
                            if (i + deviant >= bias1 && i != 0) {
                                if (bias6Y > 0) {
                                    mCanvas.drawLine(teamLastX, tempBias6Y, lastX,
                                            bias6Y, avgY5Paint);
                                } else {
                                    mCanvas.drawLine(teamLastX, tempBias6Y + 2,
                                            teamLastX, tempBias6Y + 3, avgY5Paint);
                                }
                            }
                            bias6Y = tempBias6Y;
                        }

                        if (biasMap.get(BiasUtils.BIAS12) != null) {
                            float tempBias12Y = getCutoffFTY(biasMap.get(BiasUtils.BIAS12)[i + deviant]);
                            if (i + deviant >= bias2 && i != 0) {
                                if (bias12Y > 0) {
                                    mCanvas.drawLine(teamLastX, tempBias12Y, lastX,
                                            bias12Y, avgY10Paint);
                                } else {
                                    mCanvas.drawLine(teamLastX, tempBias12Y + 2,
                                            teamLastX, tempBias12Y + 3, avgY10Paint);
                                }
                            }
                            bias12Y = tempBias12Y;
                        }

                        if (biasMap.get(BiasUtils.BIAS24) != null) {
                            float tempBias24Y = getCutoffFTY(biasMap.get(BiasUtils.BIAS24)[i + deviant]);
                            if (i + deviant >= bias3 && i != 0) {
                                if (bias12Y > 0) {
                                    mCanvas.drawLine(teamLastX, tempBias24Y, lastX,
                                            bias24Y, avgY30Paint);
                                } else {
                                    mCanvas.drawLine(teamLastX, tempBias24Y + 2,
                                            teamLastX, tempBias24Y + 3, avgY30Paint);
                                }
                            }
                            bias24Y = tempBias24Y;
                        }
                    }
                    break;
                case 5:
                    // CCI线
                    if (cciMap.get(CciUtils.CCI) != null) {
                        int cciValue = TargetManager.getInstance().getCciDefault();
                        float tempCCIY = getCutoffFTY(cciMap.get(CciUtils.CCI)[i + deviant]);
                        if (i != 0 && i + deviant >= cciValue) {
                            if (cciY > 0) {
                                mCanvas.drawLine(teamLastX, tempCCIY, lastX, cciY,
                                        avgY5Paint);
                            } else {
                                mCanvas.drawLine(teamLastX, tempCCIY + 2,
                                        teamLastX, tempCCIY + 3, avgY5Paint);
                            }
                        }
                        cciY = tempCCIY;
                    }
                    break;
            }
            if (isShowIndicateLine && scollX >= startX
                    && scollX < startX + kLWidth) {
                scollX = teamLastX;
                indicateLineIndex = i;
                indicateLineY = closeY;
            }
            lastY5 = avgY5;
            lastY10 = avgY10;
            lastY30 = avgY30;
            lastX = teamLastX;
            startX += kLWidth;
        }
        drawIndicateLine(mCanvas, indicateLineIndex + deviant, indicateLineY);
    }


    @Override
    protected void initData() {
        leftDeviant = 0;
        valueStock = (int) (topRect.width() / kLWidth);
        super.initData();
        if (valueStock > 0) {
            initDarw();
        }
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
        if (TARGET_FOOTER_INDEX == 0) {
            maxFT = showQuotationBeanList.get(0).getVolume();
        }
        for (int i = 0; i < showQuotationBeanList.size(); i++) {
            QuotationBean mQuotationBean = showQuotationBeanList.get(i);
            minKL = minKL < mQuotationBean.getLow() ? minKL : mQuotationBean.getLow();
            maxKL = maxKL > mQuotationBean.getHigh() ? maxKL : mQuotationBean
                    .getHigh();
            if (TARGET_FOOTER_INDEX == 0) {
                maxFT = maxFT > mQuotationBean.getVolume() ? maxFT : mQuotationBean
                        .getVolume();
                if (initVolumeData5 != null
                        && initVolumeData5.length > i + deviant
                        && initVolumeData5[i + deviant] > 0) {
                    minKL = minKL < initVolumeData5[i + deviant] ? minKL
                            : initVolumeData5[i + deviant];
                    maxKL = maxKL > initVolumeData5[i + deviant] ? maxKL
                            : initVolumeData5[i + deviant];
                }
                if (initVolumeData10 != null
                        && initVolumeData10.length > i + deviant
                        && initVolumeData10[i + deviant] > 0) {
                    minKL = minKL < initVolumeData10[i + deviant] ? minKL
                            : initVolumeData10[i + deviant];
                    maxKL = maxKL > initVolumeData10[i + deviant] ? maxKL
                            : initVolumeData10[i + deviant];
                }
                if (initVolumeData30 != null
                        && initVolumeData30.length > i + deviant
                        && initVolumeData30[i + deviant] > 0) {
                    minKL = minKL < initVolumeData30[i + deviant] ? minKL
                            : initVolumeData30[i + deviant];
                    maxKL = maxKL > initVolumeData30[i + deviant] ? maxKL
                            : initVolumeData30[i + deviant];
                }
            }
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
    public void setkLWidth(float kLWidth) {
        setkLWidth(kLWidth, false);
    }

    public void setkLWidth(final float kLWidth, boolean isRefresh) {

        this.kLWidth = kLWidth;
        if (isRefresh) {
            valueStock = (int) (topRect.width() / kLWidth); // 修改显示数量
            int centerDeviant = (showQuotationBeanList.size() + valueStock)
                    / 2 + 1 + deviant;
            if (centerDeviant < mDatas.size()) {
                leftDeviant = mDatas.size()
                        - centerDeviant;
            } else {
                leftDeviant = 0;
            }
            invalidate();
        }
    }

}
