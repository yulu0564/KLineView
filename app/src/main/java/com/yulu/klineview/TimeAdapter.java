package com.yulu.klineview;

import com.yulu.klineview.base.BaseKChartAdapter;
import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.utils.DateUtils;

import java.util.List;

public class TimeAdapter extends BaseKChartAdapter<List<String>> {
    @Override
    public QuotationBean getData(QuotationBean data, List<String> item, int position) {
        data.setTime(DateUtils.getLongTime(item.get(0),"yyyyMMddHHmmss"));
        data.setLastClose(Long.parseLong(getItem(position>0?position-1:position).get(1))/100.0f);
        data.setOpen(Long.parseLong(getItem(position>0?position-1:position).get(1))/100.0f);
        //        data.setLastClose(Long.parseLong(item.get(2))/100.0f);
//        data.setOpen(Long.parseLong(item.get(2))/100.0f);
        data.setClose(Long.parseLong(item.get(3))/100.0f);
        data.setHigh(Long.parseLong(item.get(4))/100.0f);
        data.setLow(Long.parseLong(item.get(5))/100.0f);
        data.setVolume(Long.parseLong(item.get(6)));
        data.setAmount(Long.parseLong(item.get(7)));
        return data;
    }
}
