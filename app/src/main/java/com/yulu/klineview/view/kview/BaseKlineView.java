package com.yulu.klineview.view.kview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.yulu.klineview.algorithm.BollUtils;
import com.yulu.klineview.algorithm.MAUtils;
import com.yulu.klineview.base.BaseStockView;
import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.model.TargetManager;
import com.yulu.klineview.utils.DateUtils;
import com.yulu.klineview.utils.DrawTextUtils;
import com.yulu.klineview.utils.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * K线基础数据
 */

public abstract class BaseKlineView extends BaseStockView {

    protected List<QuotationBean> quotationBeanList = new ArrayList<QuotationBean>(); // K线数据

    protected int colorAvlData5 = 0xFF05CFCE;// 0x00FFB400;五日平均线颜色值
    protected int colorAvlData10 = 0xFFFAAD4F;// 0x00F5A2FF;十日平均线颜色值
    protected int colorAvlData30 = 0xFFCC00CC;// 0x00105194;三十日平均线颜色值

    protected Map<String, double[]> averageMap = null;   //5、10、30日均线

    protected int TARGET_HEADER_INDEX = 0; // 主图技术指标索引 0代表MA（均线），1代表BOLL（布林通道）
    protected int TARGET_FOOTER_INDEX = 0; // 副图技术指标索引 0：VOL（成交量） 1:MACD（移动平均线）
    // 2：KDJ（随机指标）3：RSI（相对强弱指标） 4：BIAS
    // 5：CCI（顺势指标）


    protected float borderlineWidth;//边框线宽度

    protected int offsetWidth = 0;  //滑动距离

    protected double maxKL = 0; // 坐标最大值
    protected double minKL = 0; // 坐标最小值
    protected double maxFT = 0; // 坐标最大值
    protected double minFT = 0; // 坐标最小值

    public BaseKlineView(Context context) {
        this(context, null);
    }

    public BaseKlineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseKlineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBaseKline();
    }

    /**
     * 初始化画笔
     */
    protected void initBaseKline() {

        marginNews = dip2px(5);
        centerHeightNews = dip2px(20);
        topHeightNews = dip2px(20);
        bottomHeightNews = dip2px(0);
        borderlineWidth = dip2px(0.5f);
        initXLine();
        initDrawKLine();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.canvasWidth = w;
        this.canvasHeight = h;
        float remainHeight = canvasHeight - centerHeightNews - topHeightNews - bottomHeightNews - marginNews * 2;
        float kLCanvasHeight = remainHeight * 2 / 3;
        float statrtX = marginNews;
        float endX = canvasWidth - marginNews;
        topRect = new RectF(statrtX, marginNews + topHeightNews, endX, marginNews + topHeightNews + kLCanvasHeight);
        bottomRect = new RectF(statrtX, marginNews + kLCanvasHeight + centerHeightNews + topHeightNews, endX, canvasHeight - marginNews - bottomHeightNews);
    }

    @Override
    protected void onDraw(Canvas mCanvas) {
        mCanvas.drawColor(colorCanvas);
        initDarw();
        initXLine();
        drawAllXLine(mCanvas);
        drawAllYLine(mCanvas);
        if (quotationBeanList.size() > 0) {
            initDrawKLine();
            drawKLine(mCanvas);
        }
        initBorderPaint();
        drawBorder(mCanvas);
    }

    /**
     * 初始化绘图
     */
    protected abstract void initDarw();

    /**
     * 绘制K线图横坐标
     */
    protected abstract void drawAllXLine(Canvas mCanvas);// 绘制K线图横坐标

    /**
     * 绘制K线图纵坐标
     */
    protected abstract void drawAllYLine(Canvas mCanvas);// 绘制K线图纵坐标

    /**
     * 绘制边框
     */


    /**
     * 边框
     */
    protected abstract void drawBorder(Canvas mCanvas);

    /**
     * 绘制K线
     */
    protected abstract void drawKLine(Canvas mCanvas);


    /**
     * 绘制头部M5等一些的内容
     */
    protected void drawTop(Canvas mCanvas, int indicateLineIndex) {
        double[] initAverageData5 = null;
        double[] initAverageData10 = null;
        double[] initAverageData30 = null;
        if (averageMap != null) {
            initAverageData5 = averageMap.get(MAUtils.MA_5);
            initAverageData10 = averageMap.get(MAUtils.MA_10);
            initAverageData30 = averageMap.get(MAUtils.MA_30);
        }

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
                && initAverageData10[indicateLineIndex] > 0)

        {
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

    /**
     * 绘制头部VOL等一些的内容，现在把vol改为了成交额
     */
    protected void drawCenter(Canvas mCanvas, int indicateLineIndex) {

        if (quotationBeanList.size() > indicateLineIndex) {
//            mCanvas.drawRect(bottomRect.left, bottomRect.top, bottomRect.left + dip2px(100), textY, mPaint);
            double volume = quotationBeanList.get(indicateLineIndex)
                    .getAmount();
            String volumeStr = "成交额:";
            if (volume > 100000000) {
                volumeStr = volumeStr + NumberUtils.getTwoStep(volume / 100000000)
                        + "亿";
            } else if (volume > 10000) {
                volumeStr = volumeStr + NumberUtils.getTwoStep(volume / 10000) + "万";
            } else {
                volumeStr = volumeStr + NumberUtils.getTwoStep(volume);
            }
            setText(volumeStr, bottomRect.left + dip2px(10) + offsetWidth, bottomRect.top + dip2px(12),
                    mCanvas, Paint.Align.LEFT, textDefaultColor, 10);
        }
    }

    public Paint getIndicateRectPaint() {
        return indicateRectPaint;
    }

    public void setIndicateRectPaint(Paint indicateRectPaint) {
        this.indicateRectPaint = indicateRectPaint;
    }


    public void setIndexLineVerticalPaint(Paint indexLineVerticalPaint) {
        this.indexLineVerticalPaint = indexLineVerticalPaint;
    }

    public void setIndexLineHorizontalPaint(Paint indexLineHorizontalPaint) {
        this.indexLineHorizontalPaint = indexLineHorizontalPaint;
    }

    public void setIndicateRectBorderPaint(Paint indicateRectBorderPaint) {
        this.indicateRectBorderPaint = indicateRectBorderPaint;
    }

    private Paint indicateRectPaint;  //弹框的画笔
    private Paint indexLineVerticalPaint;//索引线竖

    private Paint indexLineHorizontalPaint;//索引线横
    private Paint indicateRectBorderPaint;//弹框的边框

    /**
     * 显示弹出框,现在坐标显示的数值
     */
    protected void drawIndicateLine(Canvas mCanvas, int indicateLineIndex, float indicateLineY) {
        if (isShowIndicateLine) {
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
            float scollXNews = scollX + offsetWidth;
            mCanvas.drawLine(scollXNews, topRect.top, scollXNews,
                    topRect.bottom, indexLineVerticalPaint);
            mCanvas.drawLine(scollXNews, bottomRect.top, scollXNews,
                    bottomRect.bottom, indexLineVerticalPaint);
//            mCanvas.drawLine(topRect.left, indicateLineY, topRect.right, indicateLineY, mPaint);
            Path path = new Path();
            path.moveTo(topRect.left + offsetWidth, indicateLineY);
            path.lineTo(topRect.right + offsetWidth, indicateLineY);
            mCanvas.drawPath(path, indexLineHorizontalPaint);
            QuotationBean mQuotationBean = quotationBeanList.get(indicateLineIndex);
            String timeS = mQuotationBean.getTimeS(); //时间
            double close = mQuotationBean.getClose(); // 收盘价
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

            String closeStr = NumberUtils.getTwoStep(close);
            float widthbottom = DrawTextUtils.getTextWidth(timeS + priceChangeRatioStr, sp2px(10)) + dip2px(15);
            float widthLeft = DrawTextUtils.getTextWidth(closeStr, sp2px(10)) + dip2px(10);

            float startX = offsetWidth + bottomRect.left;
            if (scollXNews > bottomRect.right + offsetWidth - widthbottom/2) {
                scollXNews = bottomRect.right + offsetWidth - widthbottom/2;
            }else if(scollXNews<bottomRect.left+widthbottom/2){
                scollXNews = bottomRect.left+widthbottom/2;
            }
            float dip_8 = dip2px(8);
            if(indicateLineY-topRect.top<dip2px(8)){
                indicateLineY = dip_8+topRect.top;
            }
            mCanvas.drawRect(startX, indicateLineY - dip_8, startX + widthLeft, indicateLineY + dip_8, indicateRectPaint);
            mCanvas.drawRect(scollXNews - widthbottom / 2, bottomRect.bottom - dip2px(15), scollXNews + widthbottom / 2, bottomRect.bottom, indicateRectPaint);

            mCanvas.drawRect(startX, indicateLineY - dip2px(8), startX + widthLeft, indicateLineY + dip2px(8), indicateRectBorderPaint);
            mCanvas.drawRect(scollXNews - widthbottom / 2, bottomRect.bottom - dip2px(15), scollXNews + widthbottom / 2, bottomRect.bottom, indicateRectBorderPaint);
            float x = setTextR(timeS, scollXNews - widthbottom / 2 + dip2px(5), bottomRect.bottom - dip2px(4),
                    mCanvas, Paint.Align.LEFT, textIndicateColor, 10);
            setText(priceChangeRatioStr, x + dip2px(5), bottomRect.bottom - dip2px(4),
                    mCanvas, Paint.Align.LEFT, textColor, 10);
            setText(closeStr, bottomRect.left + widthLeft / 2 + offsetWidth, indicateLineY + dip2px(5),
                    mCanvas, Paint.Align.CENTER, textIndicateColor, 10);
            //回调数据
            if (mOnClickSurfaceListener != null) {
                mOnClickSurfaceListener.showIndicateQuotation(mQuotationBean);
            }
            drawTop(mCanvas, indicateLineIndex);
            drawCenter(mCanvas, indicateLineIndex);
        } else {
            drawTop(mCanvas, quotationBeanList.size() - 1);
            drawCenter(mCanvas, quotationBeanList.size() - 1);
        }
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

    protected double[] initAverageData5 = null;
    protected double[] initAverageData10 = null;
    protected double[] initAverageData30 = null;

    public Paint mFallPaint;  //跌
    public Paint mRisePaint;  //涨
    public Paint mPingPaint;  //平
    public Paint avgY5Paint;//五日
    public Paint avgY10Paint;//十日
    public Paint avgY30Paint;//三日

    private void initDrawKLine() {
        if (mFallPaint == null) {
            mFallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mFallPaint.setColor(colorFall); // 跌
            mFallPaint.setStyle(Paint.Style.FILL);
            mFallPaint.setStrokeWidth(dip2px(1));
        }
        if (mRisePaint == null) {
            mRisePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRisePaint.setColor(colorRise); // 涨
            mRisePaint.setStyle(Paint.Style.STROKE);
            mRisePaint.setStrokeWidth(dip2px(1));
        }
        if (mPingPaint == null) {
            mPingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPingPaint.setColor(colorPing); // 平
            mPingPaint.setStyle(Paint.Style.FILL);
            mPingPaint.setStrokeWidth(dip2px(1));
        }

        if (avgY5Paint == null) {
            avgY5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            avgY5Paint.setColor(colorAvlData5);
            avgY5Paint.setStyle(Paint.Style.FILL);
            avgY5Paint.setStrokeWidth(dip2px(0.7f));
        }
        if (avgY10Paint == null) {
            avgY10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            avgY10Paint.setColor(colorAvlData10);
            avgY10Paint.setStyle(Paint.Style.FILL);
            avgY10Paint.setStrokeWidth(dip2px(0.7f));
        }
        if (avgY30Paint == null) {
            avgY30Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            avgY30Paint.setColor(colorAvlData30);
            avgY30Paint.setStyle(Paint.Style.FILL);
            avgY30Paint.setStrokeWidth(dip2px(0.7f));
        }

    }


    public List<QuotationBean> getData() {
        return quotationBeanList;
    }

    public void cleanData() {
        quotationBeanList.clear();
        isMore = false;
        initData();
    }

    public void setData(List<QuotationBean> quotationBeanList) {
        this.quotationBeanList = quotationBeanList;
        closeIndicateLine();

        if (quotationBeanList.size() % 480 == 0) {
            isMore = true;
        } else {
            isMore = false;
        }
        initData();
        invalidate();
    }

    public void addData(List<QuotationBean> quotationBeanList) {
        this.quotationBeanList.addAll(0, quotationBeanList);

        if (quotationBeanList.size() % 480 == 0) {
            isMore = true;
        } else {
            isMore = false;
        }
        initData();
        invalidate();
    }

    public void updateData(List<QuotationBean> quotationBeanList) {
        if (quotationBeanList.size() == 1) {
            this.quotationBeanList.remove(this.quotationBeanList.get(this.quotationBeanList.size() - 1));
            this.quotationBeanList.addAll(quotationBeanList);
            initData();
            invalidate();
        }
    }


    public BaseKlineView setColorAvlData5(int colorAvlData5) {
        this.colorAvlData5 = colorAvlData5;
        return this;
    }

    public BaseKlineView setColorAvlData10(int colorAvlData10) {
        this.colorAvlData10 = colorAvlData10;
        return this;
    }

    public BaseKlineView setColorAvlData30(int colorAvlData30) {
        this.colorAvlData30 = colorAvlData30;
        return this;
    }

    protected boolean isMore = true;

    /**
     * 设置左滑动的时候是否需要加载更多
     */
    public void IsMore(boolean isMore) {
        this.isMore = isMore;
    }

    /**
     * 刷新数据
     */
    public void onRefresh() {
        initData();
        invalidate();
    }

    /**
     * 复权选择
     */
    protected void onFuQuanClick(int Type) {
        if (mOnClickSurfaceListener != null) {
            mOnClickSurfaceListener.onFuQuanClick(Type);
        }
    }

    protected void onDownload(String time) {
        if (mOnClickSurfaceListener != null) {
            mOnClickSurfaceListener
                    .onDownload(time);
        }
    }

    /**
     * 关闭索引
     */
    protected void closeIndicateLine() {
        if (isShowIndicateLine) {
            isShowIndicateLine = false;
            invalidate();
            if (mOnClickSurfaceListener != null) {
                mOnClickSurfaceListener.hideIndicateQuotation();
            }
        }
    }


    /**
     * 副图技术指标索引
     */
    public void setTargetFooterIndex(int targetFooterIndex) {
        this.TARGET_FOOTER_INDEX = targetFooterIndex;
        initData();
    }


    /**
     * 主图技术指标索引
     */
    public void setTargetHeaderIndex(int targetHeaderIndex) {
        this.TARGET_HEADER_INDEX = targetHeaderIndex;
        if (TARGET_HEADER_INDEX == 0) {
            int day5 = TargetManager.getInstance().getMaDefault()[0];
            int day10 = TargetManager.getInstance().getMaDefault()[1];
            int day30 = TargetManager.getInstance().getMaDefault()[2];
            averageMap = MAUtils.getInitAverageData(quotationBeanList, day5,
                    day10, day30);
        } else if (TARGET_HEADER_INDEX == 1) {
            averageMap = BollUtils.getBollData(quotationBeanList, TargetManager
                    .getInstance().getBollDefault()[0], TargetManager
                    .getInstance().getBollDefault()[1]);
        }
    }


    protected void initData() {
        if (TARGET_HEADER_INDEX == 0) {
            int day5 = TargetManager.getInstance().getMaDefault()[0];
            int day10 = TargetManager.getInstance().getMaDefault()[1];
            int day30 = TargetManager.getInstance().getMaDefault()[2];
            averageMap = MAUtils.getInitAverageData(quotationBeanList, day5,
                    day10, day30);
        } else if (TARGET_HEADER_INDEX == 1) {
            averageMap = BollUtils.getBollData(quotationBeanList, TargetManager
                    .getInstance().getBollDefault()[0], TargetManager
                    .getInstance().getBollDefault()[1]);
        }
        initAverageData5 = averageMap.get(MAUtils.MA_5);
        initAverageData10 = averageMap.get(MAUtils.MA_10);
        initAverageData30 = averageMap.get(MAUtils.MA_30);
    }


    /**
     * 根据数据大小返回Y坐标
     */
    protected float getCutoffKLY(double price) {
        double priceY = topRect.bottom - topRect.height()
                * (price - minKL) / (maxKL - minKL);
        if (priceY < topRect.top)
            priceY = topRect.top;
        if (priceY > topRect.bottom)
            priceY = topRect.bottom;
        return (float) priceY;
    }

    protected float getCutoffFTY(double price) {
        float priceY = (float) (bottomRect.bottom - bottomRect.height()
                * (price - minFT) / (maxFT - minFT));
        if (priceY < bottomRect.top)
            priceY = bottomRect.top;
        if (priceY > bottomRect.bottom)
            priceY = bottomRect.bottom;
        return priceY;
    }


    protected Date lastDate; // 上一个时间

    /**
     * 设置坐标时间，如果同一年不显示年份
     */
    public String getStockDate(long time) {
        String newTime = "--";
        Date date = new Date(time);
        if (lastDate == null || !DateUtils.isSameYear(date, lastDate)) {
            SimpleDateFormat newDf = new SimpleDateFormat("yyyy/MM/dd");
            newTime = newDf.format(date);
        } else {
            SimpleDateFormat newDf = new SimpleDateFormat("MM/dd");
            newTime = newDf.format(date);
        }
        lastDate = date;
        return newTime;
    }

    /**
     * 重置画布
     */
    public void resetCanvas() {
        mXLinePaint = null;
        mBorderPaint = null;
        mFallPaint = null;
        mRisePaint = null;
        mPingPaint = null;
        avgY5Paint = null;
        avgY10Paint = null;
        avgY30Paint = null;
        indicateRectPaint = null;
        indexLineVerticalPaint = null;//索引线竖
        indexLineHorizontalPaint = null;//索引线横
        indicateRectBorderPaint = null;//弹框的边框
    }
}
