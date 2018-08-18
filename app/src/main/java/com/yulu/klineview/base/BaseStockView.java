package com.yulu.klineview.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.DashPathEffect;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;

import com.yulu.klineview.R;
import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.imp.ChartObserver;
import com.yulu.klineview.imp.OnClickSurfaceListener;

import java.util.List;

/**
 * 绘图基类
 */
public abstract class BaseStockView extends BaseChartView implements ChartObserver {

    protected boolean mIsDrawing;

    protected int colorCanvas = 0xFFFFFFFF; // 画布背景
    protected int colorRise = 0xFFFF6356; // 涨的颜色
    protected int colorFall = 0xFF33AB11; // 跌的颜色
    protected int colorPing = 0xFFBABABA; // 平的颜色
    protected int colorCoordinates = 0xFFBEBEBE; // 坐标轴的颜色
    protected int textDefaultColor = 0xFF4C4C4C;  //字的颜色
    protected int colorFrame = 0xFFBEBEBE; // 边框的颜色
    protected int borderColor = 0xDD313544; // 白色透明背景
    protected int indexLineColor = 0xFF797979;// 索引线颜色
    protected int textIndicateColor = 0xFF4C4C4C;  //字的颜色;
    protected int popupBorderColor = 0xBB313544; // 弹出边框色
    protected int colorTransWhite = 0x99FFFFFF;

    protected int indicateRectColor = 0xFFEEEEEE; //索引弹框背景色

    protected int colorCenterBg = 0xFFF2F2F2; // 中间的背景
    protected boolean isShowIndicateLine = false;// 是否显示分时图的指示线
    protected float scollX = 0; // 触屏的位置
    protected float lastX = 0; // 绘图时X的历史值
    protected boolean isScreen = false;// 是否全屏显示

    protected float marginNews;  //绘图间隙
    protected float centerHeightNews;//中间绘图区域高度
    protected float topHeightNews; //头部绘图区域高度
    protected float bottomHeightNews; //底部绘图区域高度

    protected PathEffect effects1, effects2;
    protected RectF topRect = new RectF(), bottomRect = new RectF();  //上下两个面板

    public List<QuotationBean> getData() {
        return mDatas;
    }

    public BaseStockView(Context context, AttributeSet attrs) {
        this(context, null, 0);
        initBaseStockView(attrs);
    }

    public BaseStockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBaseStockView(attrs);
    }

    public BaseStockView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //处理 wrap_content问题
        int b = MeasureSpec.EXACTLY;
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            int defaultDimension = (int) dip2px(100);
            setMeasuredDimension(defaultDimension, defaultDimension * 5 / 6);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(heightSpecSize * 6 / 5, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, widthSpecSize * 5 / 6);
        }

    }

    private void initBaseStockView(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray mTypedArray = mContext.obtainStyledAttributes(attrs,
                    R.styleable.BaseStockView);
            colorCanvas = mTypedArray.getColor(R.styleable.BaseStockView_colorCanvas, colorCanvas);
            colorRise = mTypedArray.getColor(R.styleable.BaseStockView_colorRise, colorRise);
            colorFall = mTypedArray.getColor(R.styleable.BaseStockView_colorFall, colorFall);
            colorPing = mTypedArray.getColor(R.styleable.BaseStockView_colorPing, colorPing);
            colorCoordinates = mTypedArray.getColor(R.styleable.BaseStockView_colorCoordinates, colorCoordinates);
            colorFrame = mTypedArray.getColor(R.styleable.BaseStockView_colorFrame, colorFrame);
            borderColor = mTypedArray.getColor(R.styleable.BaseStockView_borderColor, borderColor);
            indexLineColor = mTypedArray.getColor(R.styleable.BaseStockView_indexLineColor, indexLineColor);
            popupBorderColor = mTypedArray.getColor(R.styleable.BaseStockView_popupBorderColor, popupBorderColor);
            textDefaultColor = mTypedArray.getColor(R.styleable.BaseStockView_textDefaultColor, textDefaultColor);
            textIndicateColor = mTypedArray.getColor(R.styleable.BaseStockView_textIndicateColor, textIndicateColor);
            colorTransWhite = mTypedArray.getColor(R.styleable.BaseStockView_colorTransWhite, colorTransWhite);
            indicateRectColor = mTypedArray.getColor(R.styleable.BaseStockView_indicateRectColor, indicateRectColor);
            colorCenterBg = mTypedArray.getColor(R.styleable.BaseStockView_colorCenterBg, colorCenterBg);
        }

        effects1 = new DashPathEffect(new float[]{
                dip2px(2), dip2px(2), dip2px(2), dip2px(2)}, 1);
        effects2 = new DashPathEffect(new float[]{
                dip2px(1.2f), dip2px(1.2f), dip2px(1.2f), dip2px(1.2f)}, 1);
    }

    public boolean isScreen() {
        return isScreen;
    }

    public void setScreen(boolean isScreen) {
        this.isScreen = isScreen;
    }


    public BaseStockView setColorCanvas(@ColorInt int colorCanvas) {
        this.colorCanvas = colorCanvas;
        return this;
    }

    public BaseStockView setColorRise(@ColorInt int colorRise) {
        this.colorRise = colorRise;
        return this;
    }

    public BaseStockView setColorFall(@ColorInt int colorFall) {
        this.colorFall = colorFall;
        return this;
    }

    public BaseStockView setColorPing(@ColorInt int colorPing) {
        this.colorPing = colorPing;
        return this;
    }

    public BaseStockView setColorCoordinates(@ColorInt int colorCoordinates) {
        this.colorCoordinates = colorCoordinates;
        return this;
    }

    public BaseStockView setColorFrame(@ColorInt int colorFrame) {
        this.colorFrame = colorFrame;
        return this;
    }

    public BaseStockView setBorderColor(@ColorInt int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public BaseStockView setIndexLineColor(@ColorInt int indexLineColor) {
        this.indexLineColor = indexLineColor;
        return this;
    }

    public BaseStockView setPopupBorderColor(@ColorInt int popupBorderColor) {
        this.popupBorderColor = popupBorderColor;
        return this;
    }

    public BaseStockView setTextDefaultColor(@ColorInt int textDefaultColor) {
        this.textDefaultColor = textDefaultColor;
        return this;
    }

    public BaseStockView setColorTransWhite(@ColorInt int colorTransWhite) {
        this.colorTransWhite = colorTransWhite;
        return this;
    }

    public BaseStockView setColorCenterBg(@ColorInt int colorCenterBg) {
        this.colorCenterBg = colorCenterBg;
        return this;
    }

    public int getTextIndicateColor() {
        return textIndicateColor;
    }

    public void setTextIndicateColor(@ColorInt int textIndicateColor) {
        this.textIndicateColor = textIndicateColor;
    }

    public int getIndicateRectColor() {
        return indicateRectColor;
    }

    public void setIndicateRectColor(@ColorInt int indicateRectColor) {
        this.indicateRectColor = indicateRectColor;
    }


    /**
     * 刷新数据
     */
    @Override
    public void onRefresh() {
        initData();
        invalidate();
    }

    protected abstract void initData();


    public void setAdapter(BaseKChartAdapter adapter) {
        adapter.attach(this);
        adapter.notifyDataSetChanged();
    }

    protected OnClickSurfaceListener mOnClickSurfaceListener;

    /**
     * 触屏事件回调
     */
    public void setOnClickSurfaceListener(OnClickSurfaceListener l) {
        mOnClickSurfaceListener = l;
    }
}
