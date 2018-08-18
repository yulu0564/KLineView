package com.yulu.klineview.view.scolltime;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.yulu.klineview.R;
import com.yulu.klineview.algorithm.BiasUtils;
import com.yulu.klineview.algorithm.CciUtils;
import com.yulu.klineview.algorithm.KdjUtils;
import com.yulu.klineview.algorithm.MAUtils;
import com.yulu.klineview.algorithm.MacdUtils;
import com.yulu.klineview.algorithm.RsiUtils;
import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.bean.Tagging;
import com.yulu.klineview.model.TargetManager;
import com.yulu.klineview.utils.NumberUtils;
import com.yulu.klineview.view.kview.BaseKlineView;

import java.util.ArrayList;
import java.util.List;

/**
 * 分时K线图(主线程绘制）
 */
public class TimeScollView extends BaseKlineView {

    protected int offsetWidthMax;

    protected int horizontalNum;//横坐标数量
    protected int offset;  //开始的地方下标
    protected int maxWidthNum;  //当前最大显示下标

    private int YcutoffCount;  //Y轴下标数量
    private int yLineNum = 5;//Y轴平分数量
    protected double[] initAverageData60 = null;

    public TimeScollView(Context context) {
        this(context, null);
    }

    public TimeScollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeScollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        horizontalNum = (int) Math.floor(topRect.width() / kLWidth);
        updateTaggingCoordinate();
        offsetWidthMax = (int) Math.floor(mDatas.size() * kLWidth - topRect.width());
        setOffsetWidth(offsetWidthMax);
    }

    @Override
    protected void initBaseKline(AttributeSet attrs) {
        setkLWidth(dip2px(10));
        setMaxKLwidth(dip2px(50));
        setMinKLwidth(dip2px(2));
        super.initBaseKline(attrs);
        if (attrs != null) {
            TypedArray mTypedArray = mContext.obtainStyledAttributes(attrs,
                    R.styleable.BaseTimeLineView);
            setRTPriceArcColor(mTypedArray.getColor(R.styleable.BaseTimeLineView_rTPriceArcColor, RTPriceArcColor));
            RTPriceLineColor = mTypedArray.getColor(R.styleable.BaseTimeLineView_rTPriceLineColor, RTPriceLineColor);

        }
    }

    @Override
    protected void initData() {
        super.initData();
        initAverageData60 = MAUtils.calcAverageData(mDatas, 60);
    }

    @Override
    protected void initDarw() {

    }

    /**
     * 画所有横向表格，包括X轴
     */
    @Override
    protected void drawAllXLine(Canvas mCanvas) {
        float cutoffHeight = topRect.height() / 4.0f;
        double ordinateValue = 0;
        if (minKL != 0 && maxKL > minKL) {
            ordinateValue = (maxKL - minKL) / 4.0f;
        }
        for (int i = 0; i < 5; i++) {
            float cutoffY = cutoffHeight * i + topRect.top;
            if (i != 0 && i != 4) {
                Path path = new Path();
                path.moveTo(topRect.left + offsetWidth, cutoffY);
                path.lineTo(topRect.right + offsetWidth, cutoffY);
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
                    cutoffY += dip2px(11);
                } else if (i == 4) {
                    cutoffY -= dip2px(1);
                } else {
                    cutoffY += dip2px(4);
                }
                setText(NumberUtils.getTwoStep(maxKL - ordinateValue * i),
                        topRect.left + offsetWidth, cutoffY, mCanvas, Paint.Align.LEFT,
                        textColor, 10);
            }
        }
        cutoffHeight = bottomRect.height() / 3;
        for (int i = 0; i < 4; i++) {
            float cutoffY = cutoffHeight * i + bottomRect.top;
            if (i != 0 && i != 3) {
                Path path = new Path();
                path.moveTo(bottomRect.left + offsetWidth, cutoffY);
                path.lineTo(bottomRect.right + offsetWidth, cutoffY);
                mCanvas.drawPath(path, mXLinePaint);
            }
            if (i == 0) {
                cutoffY += dip2px(8);
            } else if (i == 1) {
                cutoffY += dip2px(4);
            }
            // 设置副图的坐标
            if (maxFT > minFT && i == 0) {
                String title = null;
                switch (TARGET_FOOTER_INDEX) {
                    case 0:
                        title = ((int) maxFT) + "";
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
                setText(title, bottomRect.right + offsetWidth, cutoffY,
                        mCanvas, Paint.Align.RIGHT, textDefaultColor, 10);
            }
        }
    }

    /**
     * 画所有纵向表格，包括Y轴
     */
    @Override
    protected void drawAllYLine(Canvas mCanvas) {
        for (Tagging mData : taggings) {
            setText(mData.getText(), mData.getX(), mData.getY(), mCanvas, mData.getAlign(), textDefaultColor,
                    10);
        }

    }

    /**
     * 边框
     */
    private Paint mCanvasPaint;

    protected void drawBorder(Canvas mCanvas) {
        if (mCanvasPaint == null) {
            mCanvasPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mCanvasPaint.setColor(colorCanvas);
            mCanvasPaint.setStyle(Paint.Style.FILL);
        }
        mCanvas.drawRect(offsetWidth, 0, offsetWidth + topRect.left, canvasHeight, mCanvasPaint);
        mCanvas.drawRect(offsetWidth + topRect.right, 0, offsetWidth + canvasWidth, canvasHeight, mCanvasPaint);
        mCanvas.drawRect(topRect.left + offsetWidth - borderlineWidth, topRect.top - borderlineWidth, topRect.right + offsetWidth + borderlineWidth, topRect.bottom + borderlineWidth, mBorderPaint);
        mCanvas.drawRect(bottomRect.left + offsetWidth - borderlineWidth, bottomRect.top - borderlineWidth, bottomRect.right + offsetWidth + borderlineWidth, bottomRect.bottom + borderlineWidth, mBorderPaint);
    }


    /**
     * 绘制头部M5等一些的内容
     */
    protected void drawTop(Canvas mCanvas, int indicateLineIndex) {

        String titleM60;
        titleM60 = "M60:";
        if (initAverageData60 != null
                && initAverageData60.length > indicateLineIndex
                && initAverageData60[indicateLineIndex] > 0) {
            titleM60 = titleM60
                    + NumberUtils.getTwoStep(initAverageData60[indicateLineIndex]);
        } else {
            titleM60 = titleM60 + "--";
        }
        float x = setTextR(titleM60, topRect.left + offsetWidth, topRect.top - dip2px(5), mCanvas, Paint.Align.LEFT, colorAvlData30, 10);


    }

    // 绘制K线图
    @Override
    protected void drawKLine(Canvas mCanvas) {
        initTimeLine();
        int indicateLineIndex = 0;
        float indicateLineY = 0;
        float lastY60 = -1;
        lastX = -1; // 绘图时X的历史值
        float startX = bottomRect.left + offset * kLWidth;

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

        Path pathTime2 = new Path();
        Path pathTime = new Path();
        for (int i = offset; i < maxWidthNum; i++) {
            QuotationBean mQuotationBean = mDatas.get(i);
            double open = mQuotationBean.getOpen(); // 开盘价
            double close = mQuotationBean.getClose(); // 收盘价
//            double amount = mQuotationBean.getAmount(); // 成交额
            double volume = mQuotationBean.getVolume();
            float closeY = getCutoffKLY(close); // 收盘价的坐标
            // 五日十日三十日均线
            float avgY60 = 0;

            if (initAverageData60 != null
                    && initAverageData60.length > i) {
                avgY60 = getCutoffKLY((initAverageData60[i]));
            }

            float teamLastX = startX + kLWidth / 2;
            if (i != 0 && initAverageData60[i - 1] > 0) {
                mCanvas.drawLine(lastX, lastY60, teamLastX, avgY60, avgY30Paint);
            }

            if (i == offset) {
                pathTime.moveTo(teamLastX, closeY);
                pathTime2.moveTo(teamLastX, topRect.bottom);
            } else {
                pathTime.lineTo(teamLastX, closeY);
            }
            pathTime2.lineTo(teamLastX, closeY);

            if (i == maxWidthNum - 1) {
                pathTime2.lineTo(teamLastX, topRect.bottom);
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
            kLstartX += dip2px(0.3f);
            endX -= dip2px(0.3f);

            switch (TARGET_FOOTER_INDEX) {
                case 0:
                    // VOL图
                    mCanvas.drawRect(kLstartX, getCutoffFTY(volume), endX, bottomRect.bottom, mDrawPaint);
                    break;
                case 1:
                    // 绘制MACD图
                    float farPointsY = getCutoffFTY(0);
                    if (macdMap != null) {
                        if (macdMap.get(MacdUtils.MACD) != null && macdMap.get(MacdUtils.MACD)[i] != 0) {
                            Paint mMacdPaint;
                            if (close < open) {
                                mMacdPaint = mFallPaint;
                            } else if (close > open) {
                                mMacdPaint = mRisePaint;
                            } else {
                                mMacdPaint = mPingPaint;
                            }
                            mCanvas.drawLine(teamLastX,
                                    farPointsY, teamLastX, getCutoffFTY(macdMap.get(MacdUtils.MACD)[i]), mMacdPaint);
                        }

                        if (macdMap.get(MacdUtils.MACD_DIFF) != null) {
                            float tempDiffY = getCutoffFTY(macdMap.get(MacdUtils.MACD_DIFF)[i]); // 最新数据
                            if (i != 0) {
                                mCanvas.drawLine(lastX, diffY, teamLastX,
                                        tempDiffY, avgY30Paint);
                            }
                            diffY = tempDiffY;
                        }
                        if (macdMap.get(MacdUtils.MACD_DEA) != null) {
                            float tempDeaY = getCutoffFTY(macdMap.get(MacdUtils.MACD_DEA)[i]); // 最新数据
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
                            float tempKY = getCutoffFTY(kdjMap.get(KdjUtils.KDJ_K)[i]);
                            if (i != 0) {
                                mCanvas.drawLine(lastX, kY, teamLastX, tempKY,
                                        avgY5Paint);
                            }
                            kY = tempKY;
                        }
                        if (kdjMap.get(KdjUtils.KDJ_D) != null) {
                            float tempDY = getCutoffFTY(kdjMap.get(KdjUtils.KDJ_D)[i]);
                            if (i != 0) {
                                mCanvas.drawLine(lastX, dY, teamLastX, tempDY,
                                        avgY10Paint);
                            }
                            dY = tempDY;
                        }
                        if (kdjMap.get(KdjUtils.KDJ_J) != null) {
                            float tempJY = getCutoffFTY(kdjMap.get(KdjUtils.KDJ_J)[i]);
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
                        float tempRsi6 = getCutoffFTY(rsiMap.get(RsiUtils.RSI6)[i] / 100.0);
                        float tempRsi12 = getCutoffFTY(rsiMap.get(RsiUtils.RSI12)[i] / 100.0);
                        float tempRsi24 = getCutoffFTY(rsiMap.get(RsiUtils.RSI24)[i] / 100.0);
                        if (i != 0) {
                            if (rsiMap.get(RsiUtils.RSI6) != null && rsiMap.get(RsiUtils.RSI6)[i - 1] >= 0) {
                                mCanvas.drawLine(lastX, rsi6Y, teamLastX, tempRsi6,
                                        avgY5Paint);

                            }
                            if (rsiMap.get(RsiUtils.RSI12) != null && rsiMap.get(RsiUtils.RSI12)[i - 1] >= 0) {
                                mCanvas.drawLine(lastX, rsi12Y, teamLastX,
                                        tempRsi12, avgY10Paint);

                            }
                            if (rsiMap.get(RsiUtils.RSI24) != null && rsiMap.get(RsiUtils.RSI24)[i - 1] >= 0) {
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
                            float tempBias6Y = getCutoffFTY(biasMap.get(BiasUtils.BIAS6)[i]);
                            if (i >= bias1 && i != 0) {
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
                            float tempBias12Y = getCutoffFTY(biasMap.get(BiasUtils.BIAS12)[i]);
                            if (i >= bias2 && i != 0) {
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
                            float tempBias24Y = getCutoffFTY(biasMap.get(BiasUtils.BIAS24)[i]);
                            if (i >= bias3 && i != 0) {
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
                        float tempCCIY = getCutoffFTY(cciMap.get(CciUtils.CCI)[i]);
                        if (i != 0 && i >= cciValue) {
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
            if (isShowIndicateLine && scollX >= startX - offsetWidth
                    && scollX < startX + kLWidth - offsetWidth) {
                scollX = teamLastX - offsetWidth;
                indicateLineIndex = i;
                indicateLineY = closeY;
            }
            lastY60 = avgY60;
            lastX = teamLastX;
            startX += kLWidth;
        }
        mCanvas.drawPath(pathTime, mTimeLinePaint);  //分数线
        updateTimeLineRectPaint();
        mCanvas.drawPath(pathTime2, mTimeLineRectPaint);  //闭合区域
        drawIndicateLine(mCanvas, indicateLineIndex, indicateLineY);
    }

    protected void setOffsetWidth(int offsetWidth) {
        this.offsetWidth = offsetWidth;
        if (mDatas.size() > horizontalNum) {
            offset = (int) Math.floor(offsetWidth / kLWidth);
            maxWidthNum = (int) Math.ceil((offsetWidth + topRect.width()) / kLWidth);
        } else {
            offset = 0;
            maxWidthNum = mDatas.size();
        }
        setKLMaxAndMin();
        switch (TARGET_FOOTER_INDEX) {
            case 1:
                if (macdMap != null) {
                    setFTMaxAndMin(offset, maxWidthNum, macdMap.get(MacdUtils.MACD_DEA), macdMap.get(MacdUtils.MACD_DIFF),
                            macdMap.get(MacdUtils.MACD));
                }
                break;
            case 2:

                if (kdjMap != null) {
                    setFTMaxAndMin(offset, maxWidthNum, kdjMap.get(KdjUtils.KDJ_K), kdjMap.get(KdjUtils.KDJ_D),
                            kdjMap.get(KdjUtils.KDJ_J));
                }
                break;
            case 3:
                minFT = 0;
                maxFT = 100;
                break;
            case 4:
                if (biasMap != null) {
                    setFTMaxAndMin(offset, maxWidthNum, biasMap.get(BiasUtils.BIAS6), biasMap.get(BiasUtils.BIAS12),
                            biasMap.get(BiasUtils.BIAS24));
                }
                break;
            case 5:
                if (cciMap != null) {
                    setFTMaxAndMin(offset, maxWidthNum, cciMap.get(CciUtils.CCI));
                }
                break;
        }
        scrollTo(offsetWidth, 0);
    }


    /**
     * 设置K线坐标的最大和最小值
     */
    public void setKLMaxAndMin() {
        minKL = mDatas.get(offset).getClose();
        maxKL = mDatas.get(offset).getClose();
        if (TARGET_FOOTER_INDEX == 0) {
            minFT = 0;
            maxFT = mDatas.get(offset).getVolume();
        }
        for (int i = offset; i < maxWidthNum; i++) {
            QuotationBean mQuotationBean = mDatas.get(i);
            if (mQuotationBean.getClose() >= 0) {
                minKL = minKL < mQuotationBean.getClose() ? minKL
                        : mQuotationBean.getClose();
                maxKL = maxKL > mQuotationBean.getClose() ? maxKL
                        : mQuotationBean.getClose();
            }
            if (TARGET_FOOTER_INDEX == 0) {
                maxFT = maxFT > mQuotationBean.getVolume() ? maxFT : mQuotationBean
                        .getVolume();
            }
            if (initAverageData60 != null
                    && initAverageData60.length > i
                    && initAverageData60[i] > 0) {
                minKL = minKL < initAverageData60[i] ? minKL
                        : initAverageData60[i];
                maxKL = maxKL > initAverageData60[i] ? maxKL
                        : initAverageData60[i];
            }
        }
        if (maxKL == minKL) {
            maxKL *= 1.1f;
            minKL *= 0.9f;
        }
    }

    @Override
    public void setkLWidth(float kLWidth) {
        super.setkLWidth(kLWidth);
        setkLWidth(kLWidth, false);
    }

    public void setkLWidth(final float kLWidth, boolean isRefresh) {
        this.kLWidth = kLWidth;
        if (isRefresh) {
            offsetWidthMax = (int) Math.floor(mDatas.size() * kLWidth - topRect.width());
            horizontalNum = (int) Math.floor(topRect.width() / kLWidth);
            int offsetWidth;
            if (mDatas.size() > horizontalNum) {
                final int center = (int) Math.ceil((TimeScollView.this.offsetWidth + topRect.width() / 2) / TimeScollView.this.kLWidth);
                offsetWidth = Math.round(center * kLWidth - topRect.width() / 2);
                offsetWidth = offsetWidth > offsetWidthMax ? offsetWidthMax : offsetWidth;
                offsetWidth = offsetWidth < 0 ? 0 : offsetWidth;
            } else {
                offsetWidth = 0;
            }
            setOffsetWidth(offsetWidth);
            updateTaggingCoordinate();
            invalidate();
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    offsetWidthMax = (int) Math.floor(mDatas.size() * kLWidth - topRect.width());
                    updateTaggingCoordinate();
                }
            });
        }
    }

    private List<Tagging> taggings = new ArrayList<>();  //标注的坐标
    protected float yCutoffWidth;  //最大长度坐标

    protected void updateTaggingCoordinate() {
        yCutoffWidth = kLWidth * mDatas.size();
        float cutoffWidth = topRect.width() / (yLineNum - 1);
        YcutoffCount = (int) Math.floor(yCutoffWidth * (yLineNum - 1) / topRect.width());
        lastDate = null;
        taggings.clear();
        float y = topRect.bottom + dip2px(10);
        if (YcutoffCount > 0) {
            for (int i = 0; i < YcutoffCount; i++) {
                float x = yCutoffWidth - cutoffWidth * (i + 0.5f);
                Tagging mTaggingCoordinate = new Tagging();
                mTaggingCoordinate.setY(y);
                int position;
                if (i == YcutoffCount && x < cutoffWidth / 2) {
                    x = yCutoffWidth + dip2px(3);
                    mTaggingCoordinate.setAlign(Paint.Align.LEFT);
                    position = 0;
                } else {
                    position = Math.round(x / kLWidth) - 1;
                }
                if (position > mDatas.size() - 1) {
                    position = mDatas.size() - 1;
                }
                if (position < 0) {
                    position = 0;
                }
                String time = getStockDate(mDatas.get(position).getTime());
                mTaggingCoordinate.setText(time);
                mTaggingCoordinate.setX(x + topRect.left);
                taggings.add(mTaggingCoordinate);
            }
        } else {
            if (!mDatas.isEmpty()) {
                String time = getStockDate(mDatas.get(0).getTime());
                Tagging mTaggingCoordinate = new Tagging();
                mTaggingCoordinate.setText(time);
                mTaggingCoordinate.setY(y);
                mTaggingCoordinate.setAlign(Paint.Align.LEFT);
                mTaggingCoordinate.setX(topRect.left + dip2px(3));
                taggings.add(mTaggingCoordinate);
            }
        }
    }


    public Paint mTimeLinePaint;  //分时画笔
    public Paint mTimeLineRectPaint;  //闭合区域画笔
    protected int RTPriceArcColor = 0x80047CC6;// 分时阴影部分
    protected int RTPriceArcColor2 = 0x00047CC6;// 分时阴影部分
    protected int RTPriceLineColor = 0xFF047CC6;// 分时线的颜色

    protected void initTimeLine() {
        if (mTimeLinePaint == null) {
            mTimeLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTimeLinePaint.setStrokeWidth(dip2px(1));
            mTimeLinePaint.setColor(RTPriceLineColor);
            mTimeLinePaint.setAntiAlias(true);
            mTimeLinePaint.setStyle(Paint.Style.STROKE);
        }
        if (mTimeLineRectPaint == null) {
            mTimeLineRectPaint = new Paint();
            mTimeLineRectPaint.setStyle(Paint.Style.FILL);
            // 渐变的颜色
            LinearGradient lg = new LinearGradient(topRect.left + offset * kLWidth, topRect.top, topRect.right + offset * kLWidth, topRect.bottom, RTPriceArcColor2,
                    RTPriceArcColor, Shader.TileMode.CLAMP);// CLAMP重复最后一个颜色至最后
            mTimeLineRectPaint.setShader(lg);
            mTimeLineRectPaint.setXfermode(new PorterDuffXfermode(
                    android.graphics.PorterDuff.Mode.SRC_ATOP));
//            mTimeLineRectPaint.setColor(RTPriceArcColor);

        }
    }

    protected void updateTimeLineRectPaint() {
        if (mTimeLineRectPaint == null) {
            mTimeLineRectPaint = new Paint();
            mTimeLineRectPaint.setStyle(Paint.Style.FILL);
            // 渐变的颜色
            LinearGradient lg = new LinearGradient(topRect.left + offset * kLWidth, topRect.top, topRect.right + offset * kLWidth, topRect.bottom, RTPriceArcColor2,
                    RTPriceArcColor, Shader.TileMode.CLAMP);// CLAMP重复最后一个颜色至最后
            mTimeLineRectPaint.setShader(lg);
            mTimeLineRectPaint.setXfermode(new PorterDuffXfermode(
                    android.graphics.PorterDuff.Mode.SRC_ATOP));
        } else {
            LinearGradient lg = new LinearGradient(topRect.left + offset * kLWidth, topRect.top, topRect.right + offset * kLWidth, topRect.bottom, RTPriceArcColor2,
                    RTPriceArcColor, Shader.TileMode.CLAMP);// CLAMP重复最后一个颜色至最后
            mTimeLineRectPaint.setShader(lg);
        }
    }

    public void setRTPriceArcColor(int RTPriceArcColor) {
        this.RTPriceArcColor = RTPriceArcColor;
        this.RTPriceArcColor2 = RTPriceArcColor % 0x1000000;
    }
}
