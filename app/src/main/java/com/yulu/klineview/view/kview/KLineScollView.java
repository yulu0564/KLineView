package com.yulu.klineview.view.kview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.yulu.klineview.algorithm.BiasUtils;
import com.yulu.klineview.algorithm.CciUtils;
import com.yulu.klineview.algorithm.KdjUtils;
import com.yulu.klineview.algorithm.RsiUtils;
import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.bean.Tagging;
import com.yulu.klineview.model.TargetManager;
import com.yulu.klineview.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * K线图(主线程绘制）
 */
public class KLineScollView extends BaseKlineView {

    protected int offsetWidthMax;

    protected int horizontalNum;//横坐标数量
    protected int offset;  //开始的地方下标
    protected int maxWidthNum;  //当前最大显示下标

    private int YcutoffCount;  //Y轴下标数量
    private int yLineNum = 5;//Y轴平分数量

    public KLineScollView(Context context) {
        this(context, null);
    }

    public KLineScollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KLineScollView(Context context, AttributeSet attrs, int defStyle) {
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
                path.moveTo(0, cutoffY);
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
                path.moveTo(bottomRect.left, cutoffY);
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
        for (Tagging mData: taggings) {
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

    // 绘制K线图
    @Override
    protected void drawKLine(Canvas mCanvas) {
        int indicateLineIndex = 0;
        float indicateLineY = 0;
        float lastY5 = -1, lastY10 = -1, lastY30 = -1;
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


        for (int i = offset; i < maxWidthNum; i++) {
            QuotationBean mQuotationBean = mDatas.get(i);
            double open = mQuotationBean.getOpen(); // 开盘价
            double close = mQuotationBean.getClose(); // 收盘价
            double high = mQuotationBean.getHigh(); // 最高价
            double low = mQuotationBean.getLow(); // 最低价
            double amount = mQuotationBean.getAmount(); // 成交额

            float highY = getCutoffKLY(high); // 最高价的坐标
            float lowY = getCutoffKLY(low); // 最低价的坐标
            float openY = getCutoffKLY(open); // 开盘价的坐标
            float closeY = getCutoffKLY(close); // 收盘价的坐标
            // 五日十日三十日均线
            float avgY5 = 0;
            float avgY10 = 0;
            float avgY30 = 0;
            if (initAverageData5 != null
                    && initAverageData5.length > i) {
                avgY5 = getCutoffKLY((initAverageData5[i]));
            }
            if (initAverageData10 != null
                    && initAverageData10.length > i) {
                avgY10 = getCutoffKLY((initAverageData10[i]));
            }
            if (initAverageData30 != null
                    && initAverageData30.length > i) {
                avgY30 = getCutoffKLY((initAverageData30[i]));
            }

            float teamLastX = startX + kLWidth / 2;
            if (i != 0 && initAverageData5[i - 1] > 0) {
                mCanvas.drawLine(lastX, lastY5, teamLastX, avgY5, avgY5Paint);
            }
            if (i != 0 && initAverageData10[i - 1] > 0) {
                mCanvas.drawLine(lastX, lastY10, teamLastX, avgY10, avgY10Paint);
            }
            if (i != 0 && initAverageData30[i - 1] > 0) {
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
            mCanvas.drawLine(startX + kLWidth / 2, closeY,
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
                    mCanvas.drawRect(kLstartX, getCutoffFTY(amount), endX, bottomRect.bottom, mDrawPaint);
                    break;
                case 1:
                    // 绘制MACD图
                    float farPointsY = getCutoffFTY(0);
                    if (macdMap != null) {
                        if (macdMap.get("macd") != null && macdMap.get("macd")[i] != 0) {
                            Paint mMacdPaint;
                            if (close < open) {
                                mMacdPaint = mFallPaint;
                            } else if (close > open) {
                                mMacdPaint = mRisePaint;
                            } else {
                                mMacdPaint = mPingPaint;
                            }
                            mCanvas.drawLine(startX + kLWidth / 2,
                                    farPointsY, teamLastX, getCutoffFTY(macdMap.get("macd")[i]), mMacdPaint);
                        }

                        if (macdMap.get("diff") != null) {
                            float tempDiffY = getCutoffFTY(macdMap.get("diff")[i]); // 最新数据
                            if (i != 0) {
                                mCanvas.drawLine(lastX, diffY, teamLastX,
                                        tempDiffY, avgY30Paint);
                            }
                            diffY = tempDiffY;
                        }
                        if (macdMap.get("dea") != null) {
                            float tempDeaY = getCutoffFTY(macdMap.get("dea")[i]); // 最新数据
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
            lastY5 = avgY5;
            lastY10 = avgY10;
            lastY30 = avgY30;
            lastX = teamLastX;
            startX += kLWidth;
        }
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
                    setFTMaxAndMin(offset, maxWidthNum, macdMap.get("dea"), macdMap.get("diff"),
                            macdMap.get("macd"));
                }
                break;
            case 2:

                if (kdjMap != null) {
                    setFTMaxAndMin(offset, maxWidthNum, kdjMap.get("k"), kdjMap.get("d"),
                            kdjMap.get("j"));
                }
                break;
            case 3:
                minFT = 0;
                maxFT = 100;
                break;
            case 4:
                if (biasMap != null) {
                    setFTMaxAndMin(offset, maxWidthNum, biasMap.get("bias6"), biasMap.get("bias12"),
                            biasMap.get("bias24"));
                }
                break;
            case 5:
                if (cciMap != null) {
                    setFTMaxAndMin(offset, maxWidthNum, cciMap.get("cci"));
                }
                break;
        }
        scrollTo(offsetWidth, 0);
    }


    /**
     * 设置K线坐标的最大和最小值
     */
    public void setKLMaxAndMin() {
        minKL = mDatas.get(offset).getLow();
        maxKL = mDatas.get(offset).getHigh();
        if (TARGET_FOOTER_INDEX == 0) {
            minFT = 0;
            maxFT = mDatas.get(offset).getAmount();
        }
        for (int i = offset; i < maxWidthNum; i++) {
            QuotationBean mQuotationBean = mDatas.get(i);
            minKL = minKL < mQuotationBean.getLow() ? minKL : mQuotationBean.getLow();
            maxKL = maxKL > mQuotationBean.getHigh() ? maxKL : mQuotationBean
                    .getHigh();
            if (TARGET_FOOTER_INDEX == 0) {
                maxFT = maxFT > mQuotationBean.getAmount() ? maxFT : mQuotationBean
                        .getAmount();
            }
            if (initAverageData5 != null
                    && initAverageData5.length > i
                    && initAverageData5[i] > 0) {
                minKL = minKL < initAverageData5[i] ? minKL
                        : initAverageData5[i];
                maxKL = maxKL > initAverageData5[i] ? maxKL
                        : initAverageData5[i];
            }
            if (initAverageData10 != null
                    && initAverageData10.length > i
                    && initAverageData10[i] > 0) {
                minKL = minKL < initAverageData10[i] ? minKL
                        : initAverageData10[i];
                maxKL = maxKL > initAverageData10[i] ? maxKL
                        : initAverageData10[i];
            }
            if (initAverageData30 != null
                    && initAverageData30.length > i
                    && initAverageData30[i] > 0) {
                minKL = minKL < initAverageData30[i] ? minKL
                        : initAverageData30[i];
                maxKL = maxKL > initAverageData30[i] ? maxKL
                        : initAverageData30[i];
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
                final int center = (int) Math.ceil((KLineScollView.this.offsetWidth + topRect.width() / 2) / KLineScollView.this.kLWidth);
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


}
