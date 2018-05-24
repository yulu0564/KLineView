package com.yulu.klineview.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;

import com.yulu.klineview.imp.OnClickSurfaceListener;
import com.yulu.klineview.utils.DisplayUtils;
import com.yulu.klineview.utils.DrawImageUtils;
import com.yulu.klineview.utils.DrawTextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 绘图基类
 */
public abstract class BaseStockView extends View {
    protected Context mContext;
    protected boolean mIsDrawing;
    protected float canvasHeight; // 画布高度
    protected float canvasWidth; // 画布宽度

    protected int colorCanvas = 0xFFFFFFFF; // 画布背景
    protected int colorRise = 0xFFFF6356; // 涨的颜色
    protected int colorFall = 0xFF33AB11; // 跌的颜色
    protected int colorPing = 0xFFBABABA; // 平的颜色
    protected int colorCoordinates = 0xFFBEBEBE; // 坐标轴的颜色
    protected int colorFrame = 0xFFBEBEBE; // 边框的颜色
    protected int borderColor = 0xDD313544; // 白色透明背景
    protected int indexLineColor = 0xFF797979;// 索引线颜色
    protected int popupBorderColor = 0xBB313544; // 弹出边框色
    protected int textIndicateColor = 0xFF4C4C4C;  //字的颜色;
    protected int textDefaultColor = 0xFF4C4C4C;  //字的颜色
    protected int colorTransWhite = 0x99FFFFFF;

    protected int indicateRectColor = 0xFFEEEEEE; //索引弹框北京色

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


    private Map<Float, Float> dip2pxMaps;
    private Map<Float, Float> sp2pxMaps;

    public BaseStockView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public BaseStockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBaseStockView(context);
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
    private void initBaseStockView(Context context) {
        this.mContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dip2pxMaps = new ArrayMap<>();
            sp2pxMaps = new ArrayMap<>();
        } else {
            dip2pxMaps = new HashMap<>();
            sp2pxMaps = new HashMap<>();
        }
        effects1 = new DashPathEffect(new float[]{
                dip2px(2), dip2px(2), dip2px(2), dip2px(2)}, 1);
        effects2 = new DashPathEffect(new float[]{
                dip2px(1.2f), dip2px(1.2f), dip2px(1.2f), dip2px(1.2f)}, 1);
    }

    /**
     * 写字
     */

    protected void setText(String text, float x, float y, Canvas canvas,
                           Paint.Align align, int color, int textSize) {
        DrawTextUtils.setText(text, x, y, canvas, align, color, sp2px(textSize));
    }

    /**
     * 写字带返回坐标
     *
     * @param text 文字内容
     */
    protected float setTextR(String text, float x, float y, Canvas canvas,
                             Paint.Align align, int color, int textSize) {
        return DrawTextUtils.setTextR(text, x, y, canvas, align, color, sp2px(textSize));
    }

    /**
     * 绘图画板上添加图片处理
     *
     * @param resId 图片地址
     */
    protected void drawImage(Canvas mCanvas, int top, int bottom, int left,
                             int right, int resId, int color) {
        Bitmap mBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                resId);
        Rect fqRect = new Rect();
        fqRect.top = top;
        fqRect.bottom = bottom;
        fqRect.right = right;
        fqRect.left = left;
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mBitmap = Bitmap.createScaledBitmap(mBitmap, fqRect.width(),
                fqRect.height(), false);
        mBitmap = DrawImageUtils.drawBg4Bitmap(color, mBitmap);
        mCanvas.drawBitmap(mBitmap, fqRect.left, fqRect.top, mPaint);
    }


    public boolean isScreen() {
        return isScreen;
    }

    public void setScreen(boolean isScreen) {
        this.isScreen = isScreen;
    }


    protected float sp2px(float spValue) {
        Float px = sp2pxMaps.get(spValue);
        if (px == null) {
            px = DisplayUtils.dip2px(mContext, spValue);
            sp2pxMaps.put(spValue, px);
        }
        return px;
    }

    public float dip2px(float dpValue) {
        Float px = dip2pxMaps.get(dpValue);
        if (px == null) {
            px = DisplayUtils.dip2px(mContext, dpValue);
            dip2pxMaps.put(dpValue, px);
        }
        return px;
    }


    public BaseStockView setColorCanvas(int colorCanvas) {
        this.colorCanvas = colorCanvas;
        return this;
    }

    public BaseStockView setColorRise(int colorRise) {
        this.colorRise = colorRise;
        return this;
    }

    public BaseStockView setColorFall(int colorFall) {
        this.colorFall = colorFall;
        return this;
    }

    public BaseStockView setColorPing(int colorPing) {
        this.colorPing = colorPing;
        return this;
    }

    public BaseStockView setColorCoordinates(int colorCoordinates) {
        this.colorCoordinates = colorCoordinates;
        return this;
    }

    public BaseStockView setColorFrame(int colorFrame) {
        this.colorFrame = colorFrame;
        return this;
    }

    public BaseStockView setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public BaseStockView setIndexLineColor(int indexLineColor) {
        this.indexLineColor = indexLineColor;
        return this;
    }

    public BaseStockView setPopupBorderColor(int popupBorderColor) {
        this.popupBorderColor = popupBorderColor;
        return this;
    }

    public BaseStockView setTextDefaultColor(int textDefaultColor) {
        this.textDefaultColor = textDefaultColor;
        return this;
    }

    public BaseStockView setColorTransWhite(int colorTransWhite) {
        this.colorTransWhite = colorTransWhite;
        return this;
    }

    public BaseStockView setColorCenterBg(int colorCenterBg) {
        this.colorCenterBg = colorCenterBg;
        return this;
    }

    public int getTextIndicateColor() {
        return textIndicateColor;
    }

    public void setTextIndicateColor(int textIndicateColor) {
        this.textIndicateColor = textIndicateColor;
    }

    public int getIndicateRectColor() {
        return indicateRectColor;
    }

    public void setIndicateRectColor(int indicateRectColor) {
        this.indicateRectColor = indicateRectColor;
    }




    protected OnClickSurfaceListener mOnClickSurfaceListener;

    /**
     * 触屏事件回调
     */
    public void setOnClickSurfaceListener(OnClickSurfaceListener l) {
        mOnClickSurfaceListener = l;
    }
}
