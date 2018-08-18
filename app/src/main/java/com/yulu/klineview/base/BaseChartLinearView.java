package com.yulu.klineview.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.yulu.klineview.utils.DisplayUtils;
import com.yulu.klineview.utils.DrawImageUtils;
import com.yulu.klineview.utils.DrawTextUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseChartLinearView extends FrameLayout {

    protected Context mContext;

    protected float canvasHeight; // 画布高度
    protected float canvasWidth; // 画布宽度
    private Map<Float, Float> dip2pxMaps;
    private Map<Float, Float> sp2pxMaps;

    public BaseChartLinearView(Context context) {
        super(context);
        initBaseChartView(context);
    }

    public BaseChartLinearView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBaseChartView(context);
    }

    public BaseChartLinearView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBaseChartView(context);
    }


    private void initBaseChartView(Context context) {
        this.mContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dip2pxMaps = new ArrayMap<>();
            sp2pxMaps = new ArrayMap<>();
        } else {
            dip2pxMaps = new HashMap<>();
            sp2pxMaps = new HashMap<>();
        }
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
}
