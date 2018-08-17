package com.yulu.klineview.bean;

import com.yulu.klineview.utils.DateUtils;

/**
 * K线数据和分时的数据对象
 */
public class QuotationBean {
    private long time;  //时间
    private String timeS;  //时间处理过的
    private double LastClose;  //前收盘价
    private double open;   //开盘价
    private double close;  //收盘价
    private double high;  //最高价
    private double low;  //最低价
    private double volume;  //成交量
    private double amount;  //成交额

    private double changeRatio; //涨跌幅
    private double changeAmount; //涨跌额
    private double turnoverRate;//换手率

    public QuotationBean() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLastClose() {
        return LastClose;
    }

    public void setLastClose(double lastClose) {
        LastClose = lastClose;
    }

    public double getOpen() {
        return open;
    }

    public int getStatetype(int type) {
        double data = getLastClose();
        switch (type) {
            case 0:
                data = open;
                break;
            case 1:
                data = close;
                break;
            case 2:
                data = high;
                break;
            case 3:
                data = low;
                break;
        }
        if (data > getLastClose()) {
            return 2;
        } else if (data < getLastClose()) {
            return 0;
        } else {
            return 1;
        }
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getChangeRatio() {
        if (changeRatio == 0) {
            return (close - LastClose) / LastClose;
        }
        return changeRatio;
    }

    public void setChangeRatio(double changeRatio) {
        this.changeRatio = changeRatio;
    }

    public double getTurnoverRate() {
        return turnoverRate;
    }

    public void setTurnoverRate(double turnoverRate) {
        this.turnoverRate = turnoverRate;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getTimeS() {
        if (timeS == null) {
            timeS = DateUtils.getMinutes(getTime(), "yyyy/MM/dd");
        }
        return timeS;
    }

    public void setTimeS(String timeS) {
        this.timeS = timeS;
    }
}
