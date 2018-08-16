package com.yulu.klineview.view.time;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.yulu.klineview.base.BaseStockView;
import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.bean.Tagging;
import com.yulu.klineview.imp.OnClickTimeSurfaceListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 分时基础数据
 */

public abstract class BaseTimeLineView extends BaseStockView {

    protected double maxTime = 0; // 坐标最大值
    protected double minTime = 0; // 坐标最小值
    protected double maxFT = 0; // 坐标最大值
    protected double minFT = 0; // 坐标最小值

    protected double lastClose; // 前一天的收盘价
    protected int stockColumn = 240;  //分时的时间段数量
    protected float timeWidth; // 分时图间距
    protected int RTPriceArcColor = 0x00047CC6;// 分时阴影部分
    protected int RTPriceLineColor = 0xFF047CC6;// 分时线的颜色
    protected int RTAveragePriceLineColor = 0xFFE9409F;// 均价线颜色

    protected float lastCloseY; // 开盘价Y坐标

    protected float borderlineWidth;//边框线宽度

    public BaseTimeLineView(Context context) {
        this(context, null);
    }

    public BaseTimeLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTimeLineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBaseTimeline();
    }

    /**
     * 初始化画笔
     */
    protected void initBaseTimeline() {
        marginNews = dip2px(5);
        centerHeightNews = dip2px(20);
        topHeightNews = dip2px(15);
        bottomHeightNews = dip2px(0);
        borderlineWidth = dip2px(0.5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mIsDrawing = true;
        this.canvasWidth = w;
        this.canvasHeight = h;
        float remainHeight = canvasHeight - centerHeightNews - topHeightNews - bottomHeightNews - marginNews * 2;
        float kLCanvasHeight = remainHeight * 2 / 3;
        float statrtX = marginNews;
        float endX = canvasWidth - marginNews;
        topRect = new RectF(statrtX, marginNews + topHeightNews, endX, marginNews + topHeightNews + kLCanvasHeight);
        bottomRect = new RectF(statrtX, marginNews + kLCanvasHeight + centerHeightNews + topHeightNews, endX, canvasHeight - marginNews - bottomHeightNews);
        timeWidth = topRect.width() / (stockColumn * 2 - 1);
        initPaths();
    }

    @Override
    protected void onDraw(Canvas mCanvas) {
        mCanvas.drawColor(colorCanvas);
        initDarw();
        initXLine();
        drawAllXLine(mCanvas);
        drawAllYLine(mCanvas);
        if (mDatas.size() > 0) {
            initTimeLine();
            initFallAndFallTimeLine();
            drawTimeLine(mCanvas);
            initIndicatePaint();
            drawIndicateLine(mCanvas);
        }
        initBorderPaint();
        drawBorder(mCanvas);
    }

    /**
     * 初始化绘图
     */
    protected abstract void initDarw();

    /**
     * 绘制图横坐标
     */
    protected abstract void drawAllXLine(Canvas mCanvas);// 绘制图横坐标

    /**
     * 绘制图纵坐标
     */
    protected abstract void drawAllYLine(Canvas mCanvas);// 绘制图纵坐标

    /**
     * 绘制分时图
     */
    protected abstract void drawTimeLine(Canvas mCanvas);

    /**
     * 显示弹出框,现在坐标显示的数值
     */
    protected abstract void drawIndicateLine(Canvas mCanvas);
    /**
     * 边框
     */
    protected abstract void drawBorder(Canvas mCanvas);


    /**
     * 根据数据大小返回Y坐标
     */
    protected float getCutoffTimeY(double price) {
        double priceY = topRect.bottom - topRect.height()
                * (price - minTime) / (maxTime - minTime);
        if (priceY < topRect.top)
            priceY = topRect.top;
        if (priceY > topRect.bottom)
            priceY = topRect.bottom;
        return (float) priceY;
    }

    protected float getCutoffFTY(double price) {
        double priceY = bottomRect.bottom
                - bottomRect.height() * (price - minFT) / (maxFT - minFT);
        if (priceY < bottomRect.top)
            priceY = bottomRect.top;
        if (priceY > bottomRect.bottom)
            priceY = bottomRect.bottom;
        return (float) priceY;
    }

    private boolean isEqual;

    /**
     * 设置坐标的最大和最小值
     */
    public void setTimeMaxAndMin() {
        if (mDatas.size() == 0)
            return;
        if (lastClose == 0) {
            setLastClose(mDatas.get(0).getLastClose());
        }
        if (lastClose == 0) {
            setLastClose(mDatas.get(0).getClose());
        }
        this.maxTime = lastClose;
        this.minTime = lastClose;
        minFT = 0;
        maxFT = mDatas.get(0).getVolume();
        for (int i = 0; i < mDatas.size(); i++) {
            QuotationBean mQuotationBean = mDatas.get(i);
            if (mQuotationBean.getClose() >= 0) {
                minTime = minTime < mQuotationBean.getClose() ? minTime
                        : mQuotationBean.getClose();
                maxTime = maxTime > mQuotationBean.getClose() ? maxTime
                        : mQuotationBean.getClose();
            }
            maxFT = maxFT > mQuotationBean.getVolume() ? maxFT : mQuotationBean
                    .getVolume();
        }
        initTimeMaxAndMin();
    }

    private void initTimeMaxAndMin(){
        if (maxTime - lastClose >= lastClose - minTime) {
            if (maxTime < lastClose * 2) {
                minTime = lastClose * 2 - maxTime;
            } else {
                minTime = 0;
            }
        } else {
            maxTime = lastClose * 2 - minTime;
        }
        isEqual = maxTime == minTime;
        if (isEqual) {
            maxTime = maxTime * 1.1f;
            minTime = minTime * 0.9f;
        }
        if (lastClose > 0) {
            maxTime = (float) (lastClose * Math.ceil(maxTime * 10000 / lastClose) / 10000.0f);
            minTime = (float) (lastClose * Math.floor(minTime * 10000 / lastClose) / 10000.0f);
        }
    }

    protected List<Tagging> taggingTopPaths = new ArrayList<>();  //所有Top坐标点
    protected List<Tagging> taggingAvePaths = new ArrayList<>();//均线
    protected List<Tagging> taggingBottomPaths = new ArrayList<>();//成交量

    private double amuont = 0; // 总成交额
    private double volume = 0; // 总成交量

    private void initPaths() {
        taggingTopPaths.clear();
        taggingAvePaths.clear();
        taggingBottomPaths.clear();
        amuont = 0;
        volume = 0;
        lastCloseY = getCutoffTimeY(lastClose);
        for (int i = 0; i < mDatas.size(); i++) {
            QuotationBean mQuotationBean = mDatas.get(i);
            float x = (2 * i + 1) * timeWidth + topRect.left;
            addTopPath(mQuotationBean, x);
            addAvePrice(mQuotationBean, x);
            addBottomPath(mQuotationBean, x);
        }
    }


    private void initTopPaths() {
        taggingTopPaths.clear();
        taggingAvePaths.clear();
        amuont = 0;
        volume = 0;
        lastCloseY = getCutoffTimeY(lastClose);
        for (int i = 0; i < mDatas.size(); i++) {
            QuotationBean mQuotationBean = mDatas.get(i);
            float x = (2 * i + 1) * timeWidth + topRect.left;
            addTopPath(mQuotationBean, x);
            addAvePrice(mQuotationBean, x);
        }
    }

    private void initBottomPaths() {
        taggingBottomPaths.clear();
        for (int i = 0; i < mDatas.size(); i++) {
            QuotationBean mQuotationBean = mDatas.get(i);
            float x = (2 * i + 1) * timeWidth + topRect.left;
            addBottomPath(mQuotationBean, x);
        }
    }

    private void addTopPath(QuotationBean mQuotationBean, float x) {
        float y = getCutoffTimeY(mQuotationBean.getClose());
        Tagging mTopPath = new Tagging();
        mTopPath.setX(x);
        mTopPath.setY(y);
        taggingTopPaths.add(mTopPath);
    }

    private void addAvePrice(QuotationBean mQuotationBean, float x) {
        amuont += mQuotationBean.getAmount();
        volume += mQuotationBean.getVolume();
        if (volume != 0) {
            double avePrice = amuont / volume;
            Tagging mAvePath = new Tagging();
            mAvePath.setX(x);
            mAvePath.setY(getCutoffTimeY(avePrice));
            taggingAvePaths.add(mAvePath);
        }
    }

    private void addBottomPath(QuotationBean mQuotationBean, float x) {
        Tagging mBottomPath = new Tagging();
        mBottomPath.setX(x - timeWidth / 2);
        mBottomPath.setY(getCutoffFTY(mQuotationBean.getVolume()));
        if (mQuotationBean.getClose() > lastClose) {
            mBottomPath.setState(2);
        } else if (mQuotationBean.getClose() < lastClose) {
            mBottomPath.setState(0);
        } else {
            mBottomPath.setState(1);
        }
        taggingBottomPaths.add(mBottomPath);
    }

//    public void addData(List<QuotationBean> quotationBeanList) {
//        boolean isRefreshTop = false;
//        boolean isRefreshBottom = false;
//        if (mDatas.size() > 0) {
//            if (!isEqual) {
//                QuotationBean mQuotationBean = quotationBeanList.get(mDatas.size() - 1);
//                if (mQuotationBean.getClose() < maxTime && mQuotationBean.getClose() > minTime) {
//                    taggingTopPaths.remove(this.taggingTopPaths.get(this.taggingTopPaths.size() - 1));
//                    taggingAvePaths.remove(this.taggingAvePaths.get(this.taggingAvePaths.size() - 1));
//                    amuont -= mQuotationBean.getAmount();
//                    volume -= mQuotationBean.getVolume();
//                } else {
//                    isRefreshTop = true;
//                }
//                if (mQuotationBean.getVolume() >= maxFT) {
//                    isRefreshBottom = true;
//                }
//            } else {
//                isRefreshTop = true;
//            }
//            quotationBeanList.remove(mDatas.get(mDatas.size() - 1));
//        }
//        int oldSize = mDatas.size();
//        mDatas.addAll(quotationBeanList);
//        if (!(isRefreshTop && isRefreshBottom)) {
//            for (int i = 0; i < quotationBeanList.size(); i++) {
//                QuotationBean mQuotationBean = quotationBeanList.get(i);
//                if (mQuotationBean.getClose() >= 0) {
//                    float x = (2 * (i + oldSize) + 1) * timeWidth + topRect.left;
//                    if (!isRefreshTop) {
//                        if (mQuotationBean.getClose() > maxTime) {
//                            maxTime = mQuotationBean.getClose();
//                            isRefreshTop = true;
//                        } else if (mQuotationBean.getClose() < minTime) {
//                            minTime = mQuotationBean.getClose();
//                            isRefreshTop = true;
//                        } else {
//                            addTopPath(mQuotationBean, x);
//                            addAvePrice(mQuotationBean, x);
//                        }
//                    }
//                    if (!isRefreshBottom) {
//                        if (mQuotationBean.getVolume() > maxFT) {
//                            maxFT = mQuotationBean.getVolume();
//                            isRefreshBottom = true;
//                        } else {
//                            addBottomPath(mQuotationBean, x);
//                        }
//                    }
//                }
//            }
//        }
//        if (isRefreshTop && isRefreshBottom) {
//            initTimeMaxAndMin();
//            initPaths();
//        } else if (isRefreshTop) {
//            initTimeMaxAndMin();
//            initTopPaths();
//        } else if (isRefreshBottom) {
//            initBottomPaths();
//        }
//        invalidate();
//    }

    public int getRTPriceArcColor() {
        return RTPriceArcColor;
    }

    public void setRTPriceArcColor(int RTPriceArcColor) {
        this.RTPriceArcColor = RTPriceArcColor;
    }

    public int getRTPriceLineColor() {
        return RTPriceLineColor;
    }

    public void setRTPriceLineColor(int RTPriceLineColor) {
        this.RTPriceLineColor = RTPriceLineColor;
    }

    public int getRTAveragePriceLineColor() {
        return RTAveragePriceLineColor;
    }

    public void setRTAveragePriceLineColor(int RTAveragePriceLineColor) {
        this.RTAveragePriceLineColor = RTAveragePriceLineColor;
    }


    public int getStockColumn() {
        return stockColumn;
    }

    public void setStockColumn(int stockColumn) {
        this.stockColumn = stockColumn;
    }


    protected OnClickTimeSurfaceListener mOnClickSurfaceListener;

    public void setOnClickSurfaceListener(OnClickTimeSurfaceListener l) {
        mOnClickSurfaceListener = l;
    }

    @Override
    protected void initData() {
        setTimeMaxAndMin();
        isShowIndicateLine = false;
        initPaths();
        invalidate();
    }

    public Paint mXLinePaint;  //X坐标画笔

    private void initXLine() {
        if (mXLinePaint == null) {
            mXLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mXLinePaint.setColor(colorCoordinates);
            mXLinePaint.setStrokeWidth(dip2px(0.2f));
            mXLinePaint.setPathEffect(effects1);
            mXLinePaint.setStyle(Paint.Style.STROKE);
        }
    }

    public Paint mBorderPaint;

    private void initBorderPaint() {
        if (mBorderPaint == null) {
            mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(borderlineWidth);
            mBorderPaint.setColor(colorFrame);
        }
    }

    public Paint mTimeLinePaint;  //X坐标画笔
    public Paint mTimeLineRectPaint;  //闭合区域画笔
    public Paint mAvePaint;//均线画笔

    private void initTimeLine() {
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
            LinearGradient lg = new LinearGradient(topRect.left, topRect.top, topRect.right, topRect.bottom, RTPriceArcColor,
                    RTPriceLineColor, Shader.TileMode.CLAMP);// CLAMP重复最后一个颜色至最后
            mTimeLineRectPaint.setShader(lg);
            mTimeLineRectPaint.setXfermode(new PorterDuffXfermode(
                    android.graphics.PorterDuff.Mode.SRC_ATOP));

        }
        if (mAvePaint == null) {
            mAvePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mAvePaint.setStrokeWidth(dip2px(1));
            mAvePaint.setColor(RTAveragePriceLineColor);
            mAvePaint.setAntiAlias(true);
            mAvePaint.setStyle(Paint.Style.STROKE);
        }
    }

    public Paint mFallPaint;  //跌
    public Paint mRisePaint;  //涨
    public Paint mPingPaint;  //平

    private void initFallAndFallTimeLine() {
        if (mFallPaint == null) {
            mFallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mFallPaint.setColor(colorFall); // 跌
            mFallPaint.setStyle(Paint.Style.FILL);
            mFallPaint.setStrokeWidth(dip2px(1));
        }
        if (mRisePaint == null) {
            mRisePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRisePaint.setColor(colorRise); // 涨
            mRisePaint.setStyle(Paint.Style.FILL);
        }
        if (mPingPaint == null) {
            mPingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPingPaint.setColor(colorPing); // 平
            mPingPaint.setStyle(Paint.Style.FILL);
        }
    }
    protected Paint indicateRectPaint;  //弹框的画笔
    protected Paint indexLineVerticalPaint;//索引线竖

    protected Paint indexLineHorizontalPaint;//索引线横
    protected Paint indicateRectBorderPaint;//弹框的边框
    protected  void initIndicatePaint(){
        if (indicateRectPaint == null) {
            indicateRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            indicateRectPaint.setColor(indicateRectColor);
            indicateRectPaint.setStyle(Paint.Style.FILL);
        }
        if (indexLineVerticalPaint == null) {
            indexLineVerticalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            indexLineVerticalPaint.setStrokeWidth(dip2px(0.8f));
            indexLineVerticalPaint.setColor(indexLineColor);
            indexLineVerticalPaint.setStyle(Paint.Style.FILL);
        }
        if (indexLineHorizontalPaint == null) {
            indexLineHorizontalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            indexLineHorizontalPaint.setStrokeWidth(dip2px(0.8f));
            indexLineHorizontalPaint.setColor(indexLineColor);
            indexLineHorizontalPaint.setPathEffect(effects2);
            indexLineHorizontalPaint.setStyle(Paint.Style.STROKE);
        }
        if (indicateRectBorderPaint == null) {
            indicateRectBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            indicateRectBorderPaint.setStrokeWidth(dip2px(0.7f));
            indicateRectBorderPaint.setColor(indexLineColor);
            indicateRectBorderPaint.setStyle(Paint.Style.STROKE);
        }
    }

    public double getLastClose() {
        return lastClose;
    }

    public void setLastClose(double lastClose) {
        this.lastClose = lastClose;
    }

    /**
     * 重置画布
     */
    public void resetCanvas() {
        mXLinePaint = null;
        mBorderPaint = null;
    }
}
