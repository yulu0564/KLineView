package com.yulu.klineview.imp;

import com.yulu.klineview.bean.QuotationBean;

/**
 * K线图使用的回调方法
 */
public interface OnClickSurfaceListener {
    boolean onFuQuanClick(int Type);// 点击复权的按钮

    boolean onVOLClick();// 点击VOl的按钮

    boolean onDownload(String time);  //滑动加载更多

    boolean onKLClick();   //单击K线

    void showIndicateQuotation(QuotationBean mStockTopBean);

    void hideIndicateQuotation();

}
