package com.yulu.klineview.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 绘图画板上添加图片处理
 */

public class DrawImageUtils {


    /**
     * 解决背景的问题
     */
    public static Bitmap drawBg4Bitmap(int color, Bitmap orginBitmap) {
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(orginBitmap.getWidth(),
                orginBitmap.getHeight(), orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(),
                paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        return bitmap;
    }
}
