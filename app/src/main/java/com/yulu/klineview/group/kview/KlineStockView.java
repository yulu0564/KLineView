package com.yulu.klineview.group.kview;

import android.content.Context;
import android.util.AttributeSet;

import com.yulu.klineview.group.BaseUpsDownView;

public class KlineStockView extends BaseUpsDownView {

    public KlineStockView(Context context) {
        this(context, null);
    }

    public KlineStockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KlineStockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
