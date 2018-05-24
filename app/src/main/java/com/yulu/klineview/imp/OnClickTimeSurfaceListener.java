package com.yulu.klineview.imp;

import com.yulu.klineview.bean.QuotationBean;

public interface OnClickTimeSurfaceListener {
    void onTimeClick();
    void showIndicateQuotation(QuotationBean mQuotationBean);
    void hideIndicateQuotation();
}
