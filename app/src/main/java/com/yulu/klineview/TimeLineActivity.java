package com.yulu.klineview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yulu.klineview.bean.QuotationBean;
import com.yulu.klineview.utils.DateUtils;
import com.yulu.klineview.utils.JsonUtils;
import com.yulu.klineview.view.time.BaseTimeLineView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TimeLineActivity extends AppCompatActivity {
    private BaseTimeLineView mKLineStockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        mKLineStockView = findViewById(R.id.KLineStockView);
        mKLineStockView.setScreen(true);
        mKLineStockView.setStockColumn(240);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        String a = JsonUtils.getJson(this, "time.json");
        Type resultType = new TypeToken<List<List<String>>>() {
        }.getType();
        List<List<String>> datas = new Gson().fromJson(a, resultType);
        List<QuotationBean> quotationBeanList = new ArrayList<>();
        for (List<String> list : datas) {
            QuotationBean mQuotationBean = new QuotationBean();
            mQuotationBean.setTime(DateUtils.getLongTime(list.get(0),"yyyyMMddHHmmss"));
            mQuotationBean.setLastClose(Long.parseLong(datas.get(0).get(1))/100.0f);
            mQuotationBean.setOpen(Long.parseLong(list.get(2))/100.0f);
            mQuotationBean.setClose(Long.parseLong(list.get(3))/100.0f);
            mQuotationBean.setHigh(Long.parseLong(list.get(4))/100.0f);
            mQuotationBean.setLow(Long.parseLong(list.get(5))/100.0f);
            mQuotationBean.setVolume(Long.parseLong(list.get(6)));
            mQuotationBean.setAmount(Long.parseLong(list.get(7)));
            quotationBeanList.add(mQuotationBean);
        }
        mKLineStockView.setColorCanvas(Color.BLACK);
        mKLineStockView.setTextDefaultColor(Color.WHITE);
        mKLineStockView.setData(quotationBeanList);
//        String b = new Gson().toJson(quotationBeanList);
    }
}
