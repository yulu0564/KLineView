package com.yulu.klineview.imp;

import com.yulu.klineview.bean.QuotationBean;

import java.util.ArrayList;
import java.util.List;

public interface ChartObserver {

    List<QuotationBean> mDatas = new ArrayList<>(); // 分时数据

    void onRefresh();
}
