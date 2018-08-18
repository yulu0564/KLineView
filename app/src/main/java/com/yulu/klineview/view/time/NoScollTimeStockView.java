package com.yulu.klineview.view.time;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.bean.Tagging;
import com.yulu.klineview.utils.DateUtils;
import com.yulu.klineview.utils.DrawTextUtils;
import com.yulu.klineview.utils.NumberUtils;

/**
 * 分时图,主线程绘制
 */
public class NoScollTimeStockView extends BaseNoScollTimeLineView {

    public NoScollTimeStockView(Context context) {
        super(context);
    }

    public NoScollTimeStockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScollTimeStockView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initDarw() {

    }

    @Override
    protected void drawAllXLine(Canvas mCanvas) {
        float cutoffHeight = topRect.height() / 4.0f;
        double ordinateValue = 0;
        if (minTime != 0 && maxTime > minTime) {
            ordinateValue = (maxTime - minTime) / 4.0f;
        }
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
                    cutoffY += dip2px(11);
                } else if (i == 4) {
                    cutoffY -= dip2px(1);
                } else {
                    cutoffY += dip2px(4);
                }
                setText(NumberUtils.getTwoStep(maxTime - ordinateValue * i),
                        topRect.left, cutoffY, mCanvas, Paint.Align.LEFT,
                        textColor, 10);
            }
        }
        cutoffHeight = bottomRect.height() / 3;
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
                String title = NumberUtils.getTwoStepStr((maxFT - ordinateValue * i));
                setText(title, bottomRect.right, cutoffY,
                        mCanvas, Paint.Align.RIGHT, textDefaultColor, 10);
            }
        }
    }

    @Override
    protected void drawAllYLine(Canvas mCanvas) {

    }

    @Override
    protected void drawTimeLine(Canvas mCanvas) {
        Path path2 = new Path();
        path2.moveTo(topRect.left, topRect.bottom);
        Path path = new Path();
        path.moveTo(topRect.left, lastCloseY);

        mCanvas.drawPath(path, mXLinePaint);
        for (Tagging mTagging : taggingTopPaths) {
            path2.lineTo(mTagging.getX(), mTagging.getY());
            path.lineTo(mTagging.getX(), mTagging.getY());
        }
        if (taggingTopPaths.size() > 0) {
            path2.lineTo(taggingTopPaths.get(taggingTopPaths.size() - 1).getX(), topRect.bottom);
        }
        mCanvas.drawPath(path, mTimeLinePaint);  //分数线
        mCanvas.drawPath(path2, mTimeLineRectPaint);  //闭合区域

        Path avePath = new Path();
        avePath.moveTo(topRect.left, lastCloseY);
        for (Tagging mTagging : taggingAvePaths) {
            avePath.lineTo(mTagging.getX(), mTagging.getY());
        }
        mCanvas.drawPath(avePath, mAvePaint);  //均线
        for (Tagging mTagging : taggingBottomPaths) {
            Paint mDrawPaint;
            switch (mTagging.getState()) {
                case 0:
                    mDrawPaint = mFallPaint;
                    break;
                case 2:
                    mDrawPaint = mRisePaint;
                    break;
                default:
                    mDrawPaint = mPingPaint;
                    break;

            }
            mCanvas.drawRect(mTagging.getX(), mTagging.getY(), mTagging.getX() + timeWidth, bottomRect.bottom, mDrawPaint);
        }
    }
    /**
     * 显示弹出框,现在坐标显示的数值
     */
    @Override
    protected void drawIndicateLine(Canvas mCanvas) {
        if (isShowIndicateLine) {
            float scollXNews = scollX;
            int indicateLineIndex = Math.round((scollX - timeWidth-topRect.left) / (timeWidth * 2));
            if (indicateLineIndex < 0) {
                indicateLineIndex = 0;
            }
            float indicateLineY = taggingTopPaths.get(indicateLineIndex).getY();
            mCanvas.drawLine(scollXNews, topRect.top, scollXNews,
                    topRect.bottom, indexLineVerticalPaint);
            mCanvas.drawLine(scollXNews, bottomRect.top, scollXNews,
                    bottomRect.bottom, indexLineVerticalPaint);

            Path path = new Path();
            path.moveTo(topRect.left, indicateLineY);
            path.lineTo(topRect.right, indicateLineY);
            mCanvas.drawPath(path, indexLineHorizontalPaint);

            QuotationBean mQuotationBean = mDatas.get(indicateLineIndex);
            String timeS = DateUtils.getMinutes(mQuotationBean.getTime(), "HH:mm");
            double price = mQuotationBean.getClose();
            double priceChangeRatio = mQuotationBean.getChangeRatio();// 涨跌幅
            int textColor;
            String priceChangeRatioStr = NumberUtils.getTwoStep(priceChangeRatio * 100) + "%";
            if (priceChangeRatio > 0) {
                textColor = colorRise;
                priceChangeRatioStr = "+" + priceChangeRatioStr;
            } else if (priceChangeRatio < 0) {
                textColor = colorFall;
            } else {
                textColor = popupBorderColor;
            }

            String closeStr = NumberUtils.getTwoStep(price);
            float widthbottom = DrawTextUtils.getTextWidth(timeS + priceChangeRatioStr, sp2px(10)) + dip2px(15);
            float widthLeft = DrawTextUtils.getTextWidth(closeStr, sp2px(10)) + dip2px(10);

            float startX = bottomRect.left;
            if (scollXNews > bottomRect.right - widthbottom / 2) {
                scollXNews = bottomRect.right - widthbottom / 2;
            } else if (scollXNews < bottomRect.left + widthbottom / 2) {
                scollXNews = bottomRect.left + widthbottom / 2;
            }
            float dip_8 = dip2px(8);
            if (indicateLineY - topRect.top < dip2px(8)) {
                indicateLineY = dip_8 + topRect.top;
            }
            mCanvas.drawRect(startX, indicateLineY - dip_8, startX + widthLeft, indicateLineY + dip_8, indicateRectPaint);
            mCanvas.drawRect(scollXNews - widthbottom / 2, bottomRect.bottom - dip2px(15), scollXNews + widthbottom / 2, bottomRect.bottom, indicateRectPaint);

            mCanvas.drawRect(startX, indicateLineY - dip2px(8), startX + widthLeft, indicateLineY + dip2px(8), indicateRectBorderPaint);
            mCanvas.drawRect(scollXNews - widthbottom / 2, bottomRect.bottom - dip2px(15), scollXNews + widthbottom / 2, bottomRect.bottom, indicateRectBorderPaint);
            float x = setTextR(timeS, scollXNews - widthbottom / 2 + dip2px(5), bottomRect.bottom - dip2px(4),
                    mCanvas, Paint.Align.LEFT, textIndicateColor, 10);
            setText(priceChangeRatioStr, x + dip2px(5), bottomRect.bottom - dip2px(4),
                    mCanvas, Paint.Align.LEFT, textColor, 10);
            setText(closeStr, bottomRect.left + widthLeft / 2, indicateLineY + dip2px(5),
                    mCanvas, Paint.Align.CENTER, textIndicateColor, 10);
            //回调数据
            if (mOnClickSurfaceListener != null) {
                mOnClickSurfaceListener.showIndicateQuotation(mQuotationBean);
            }
            drawTop(mCanvas, indicateLineIndex);
        }
    }

    protected void drawTop(Canvas mCanvas, int indicateLineIndex) {
//        mCanvas.drawRect(topRect.left, 0, topRect.right, topRect.top - dip2px(1), mPaint);
        QuotationBean mQuotationBean = mDatas.get(indicateLineIndex);
        double price = mQuotationBean.getClose();
        String closeStr = NumberUtils.getTwoStep(price);
        String timeS = DateUtils.getMinutes(mQuotationBean.getTime(), "HH:mm");
        float x = setTextR(timeS, topRect.left, topRect.top - dip2px(5),
                mCanvas, Paint.Align.LEFT, textDefaultColor, 10);
        x = setTextR("昨收" + NumberUtils.getTwoStep(lastClose), x + dip2px(10), topRect.top - dip2px(5),
                mCanvas, Paint.Align.LEFT, RTAveragePriceLineColor, 10);

        double priceChangeRatio = mQuotationBean.getChangeRatio();// 涨跌幅
        double priceChange = price - lastClose; // 涨跌额
        int textColor;
        String priceChangeRatioStr = NumberUtils.getTwoStep(priceChangeRatio * 100) + "%";
        String priceChangeStr = NumberUtils.getTwoStep(priceChange);
        if (priceChangeRatio > 0) {
            textColor = colorRise;
            priceChangeRatioStr = "+" + priceChangeRatioStr;
            priceChangeStr = "+" + priceChangeStr;
        } else if (priceChangeRatio < 0) {

            textColor = colorFall;
        } else {
            textColor = popupBorderColor;
        }
        if (indicateLineIndex == mDatas.size() - 1) {
            x = setTextR("最新" + closeStr, x + dip2px(10), topRect.top - dip2px(5),
                    mCanvas, Paint.Align.LEFT, textDefaultColor, 10);
        } else {
            x = setTextR("数值" + closeStr, x + dip2px(10), topRect.top - dip2px(5),
                    mCanvas, Paint.Align.LEFT, textDefaultColor, 10);
        }
        x = setTextR(priceChangeStr, x + dip2px(10), topRect.top - dip2px(5),
                mCanvas, Paint.Align.LEFT, textColor, 10);

        x = setTextR(priceChangeRatioStr, x + dip2px(10), topRect.top - dip2px(5),
                mCanvas, Paint.Align.LEFT, textColor, 10);
    }

    @Override
    protected void drawBorder(Canvas mCanvas) {
        mCanvas.drawRect(topRect.left - borderlineWidth, topRect.top - borderlineWidth, topRect.right + borderlineWidth, topRect.bottom + borderlineWidth, mBorderPaint);
        mCanvas.drawRect(bottomRect.left - borderlineWidth, bottomRect.top - borderlineWidth, bottomRect.right + borderlineWidth, bottomRect.bottom + borderlineWidth, mBorderPaint);
    }
}
