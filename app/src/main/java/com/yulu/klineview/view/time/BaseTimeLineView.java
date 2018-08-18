package com.yulu.klineview.view.time;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.yulu.klineview.R;
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
    protected int RTPriceArcColor = 0x00047CC6;// 分时阴影部分
    protected int RTPriceLineColor = 0xFF047CC6;// 分时线的颜色
    protected int RTAveragePriceLineColor = 0xFFE9409F;// 均价线颜色

    protected float borderlineWidth;//边框线宽度

    public BaseTimeLineView(Context context) {
        this(context, null);
    }

    public BaseTimeLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTimeLineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBaseTimeline(attrs);
    }

    /**
     * 初始化画笔
     */
    protected void initBaseTimeline(AttributeSet attrs) {
        if(attrs!=null) {
            TypedArray mTypedArray = mContext.obtainStyledAttributes(attrs,
                    R.styleable.BaseTimeLineView);
            RTPriceArcColor = mTypedArray.getColor(R.styleable.BaseTimeLineView_rTPriceArcColor, RTPriceArcColor);
            RTPriceLineColor = mTypedArray.getColor(R.styleable.BaseTimeLineView_rTPriceLineColor, RTPriceLineColor);
            RTAveragePriceLineColor = mTypedArray.getColor(R.styleable.BaseTimeLineView_rTAveragePriceLineColor, RTAveragePriceLineColor);
        }
        marginNews = dip2px(5);
        centerHeightNews = dip2px(20);
        topHeightNews = dip2px(15);
        bottomHeightNews = dip2px(0);
        borderlineWidth = dip2px(0.5f);
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


    protected OnClickTimeSurfaceListener mOnClickSurfaceListener;

    public void setOnClickSurfaceListener(OnClickTimeSurfaceListener l) {
        mOnClickSurfaceListener = l;
    }

    public Paint mXLinePaint;  //X坐标画笔

    protected void initXLine() {
        if (mXLinePaint == null) {
            mXLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mXLinePaint.setColor(colorCoordinates);
            mXLinePaint.setStrokeWidth(dip2px(0.2f));
            mXLinePaint.setPathEffect(effects1);
            mXLinePaint.setStyle(Paint.Style.STROKE);
        }
    }

    public Paint mBorderPaint;

    protected void initBorderPaint() {
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

    protected void initFallAndFallTimeLine() {
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

    /**
     * 重置画布
     */
    public void resetCanvas() {
        mXLinePaint = null;
        mBorderPaint = null;
    }
}
