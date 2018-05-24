package com.yulu.klineview.utils;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 文字相关工具类
 */

public class DrawTextUtils {

    /*
     * 精确计算文字的宽度
     * */
    public static float getTextWidth(Paint paint, String str) {
        float iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet +=  Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    public static float getTextWidth(String text, float textSize) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextSize(textSize);
        float width = p.measureText(text);
        return width;
    }

    /**
     * 写字
     *
     * @param text     文字内容
     * @param x        x坐标
     * @param y        y坐标
     * @param canvas   画板
     * @param align    对齐方式
     * @param color    字体颜色
     * @param textSize 字体大小
     */
    public static void setText(String text, float x, float y, Canvas canvas,
                           Paint.Align align, int color, float textSize) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextSize(textSize);
        p.setColor(color);
        p.setTextAlign(align);
        canvas.drawText(text, x, y, p);
    }

    /**
     * @param text     文字内容
     * @param x        x坐标
     * @param y        y坐标
     * @param canvas   画板
     * @param align    对齐方式
     * @param color    字体颜色
     * @param textSize 字体大小
     * @return
     */
    public static float setTextR(String text, float x, float y, Canvas canvas,
                             Paint.Align align, int color, float textSize) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextSize(textSize);
        p.setColor(color);
        p.setTextAlign(align);
        canvas.drawText(text, x, y, p);
        float width = p.measureText(text) + x;
        return width;
    }
}
