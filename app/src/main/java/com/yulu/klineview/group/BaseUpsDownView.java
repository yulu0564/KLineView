package com.yulu.klineview.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.yulu.klineview.R;
import com.yulu.klineview.base.BaseChartView;

/**
 * 带涨跌的view
 */
public class BaseUpsDownView extends BaseChartView {
    protected int colorRise = 0xFFFF6356; // 涨的颜色
    protected int colorFall = 0xFF33AB11; // 跌的颜色
    protected int colorPing = 0xFFBABABA; // 平的颜色
    protected int colorCoordinates = 0xFFBEBEBE; // 坐标轴的颜色
    protected int textDefaultColor = 0xFF4C4C4C;  //字的颜色
    public BaseUpsDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBaseUpsDownView(attrs);
    }

    public BaseUpsDownView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBaseUpsDownView(attrs);
    }

    public BaseUpsDownView(Context context) {
        super(context);
    }

    private void initBaseUpsDownView(AttributeSet attrs){
        if (attrs != null) {
            TypedArray mTypedArray = mContext.obtainStyledAttributes(attrs,
                    R.styleable.BaseStockView);
            colorRise = mTypedArray.getColor(R.styleable.BaseStockView_colorRise, colorRise);
            colorFall = mTypedArray.getColor(R.styleable.BaseStockView_colorFall, colorFall);
            colorPing = mTypedArray.getColor(R.styleable.BaseStockView_colorPing, colorPing);
            textDefaultColor = mTypedArray.getColor(R.styleable.BaseStockView_textDefaultColor, textDefaultColor);
            colorCoordinates = mTypedArray.getColor(R.styleable.BaseStockView_colorCoordinates, colorCoordinates);
        }
    }
}
