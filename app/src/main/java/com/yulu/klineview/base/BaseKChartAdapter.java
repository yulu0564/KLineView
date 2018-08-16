package com.yulu.klineview.base;

import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.imp.ChartObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseKChartAdapter<T> {
    protected List<T> datas = new ArrayList<>();
    private ChartObserver observer;

    public int getCount() {
        return datas.size();
    }

    public T getItem(int position) {
        return datas.get(position);
    }

    public abstract QuotationBean getData(QuotationBean data, T item, int position);

    public void setData(List<T> data) {
        datas.clear();
        datas.addAll(data);
        if (observer != null) {
            notifyDataSetChanged();
        }
    }

    /**
     * 向头部添加数据
     */
    public void addHeaderData(List<T> data) {
        if (data != null && !data.isEmpty()) {
            if (observer != null) {
                List<QuotationBean> mQuotations = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    mQuotations.add(getData(new QuotationBean(), data.get(i), i));
                }
                observer.mDatas.addAll(mQuotations);
                onRefresh();
            }
            datas.addAll(data);
        }
    }

    /**
     * 向尾部添加数据
     */
    public void addFooterData(List<T> data) {
        if (data != null && !data.isEmpty()) {
            if (observer != null) {
                List<QuotationBean> mQuotations = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    mQuotations.add(getData(new QuotationBean(), data.get(i), i));
                }
                observer.mDatas.addAll(0, mQuotations);
                onRefresh();
            }
            datas.addAll(0, data);
        }
    }

    public void removeData(int position) {
        datas.remove(position);
        if (observer != null) {
            observer.mDatas.remove(position);
            onRefresh();
        }
    }

    /**
     * 改变某个点的值
     *
     * @param position 索引值
     */
    public void changeItem(int position, T data) {
        datas.set(position, data);
        getData(observer.mDatas.get(position), data, position);
        onRefresh();
    }

    public void notifyDataSetChanged() {
        for (int i = 0; i < datas.size(); i++) {
            QuotationBean data;
            if (observer.mDatas.size() > i) {
                data = observer.mDatas.get(i);
                getData(data, getItem(i), i);
            } else {
                data = new QuotationBean();
                observer.mDatas.add(getData(data, getItem(i), i));
            }
        }
        onRefresh();
    }

    public void notifyDataSetChanged(int start, int stop) {
        for (int i = start; i < stop + 1; i++) {
            QuotationBean data;
            if (observer.mDatas.size() > i) {
                data = observer.mDatas.get(i);
                getData(data, getItem(i), i);
            } else {
                data = new QuotationBean();
                observer.mDatas.add(getData(data, getItem(i), i));
            }
        }
        onRefresh();
    }

    public void onRefresh() {
        if (observer != null) {
            observer.onRefresh();
        }
    }

    public void attach(ChartObserver observer) {
        this.observer = observer;
    }
}
