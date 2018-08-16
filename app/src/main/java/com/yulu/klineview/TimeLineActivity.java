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
        TimeAdapter timeAdapter = new TimeAdapter();
        timeAdapter.setData(datas);
        mKLineStockView.setColorCanvas(Color.BLACK);
        mKLineStockView.setTextDefaultColor(Color.WHITE);

        mKLineStockView.setAdapter(timeAdapter);
    }
}
