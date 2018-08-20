package com.yulu.klineview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yulu.klineview.utils.JsonUtils;
import com.yulu.klineview.view.kview.BaseKlineView;

import java.lang.reflect.Type;
import java.util.List;

public class TimeLineActivity2 extends AppCompatActivity {
    private BaseKlineView mKLineStockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line2);
        mKLineStockView = findViewById(R.id.KLineStockView);
        mKLineStockView.setScreen(true);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        String a = JsonUtils.getJson(this, "time.json");
        Type resultType = new TypeToken<List<List<String>>>() {
        }.getType();
        List<List<String>> datas = new Gson().fromJson(a, resultType);
        datas.addAll(datas);
        TimeAdapter timeAdapter = new TimeAdapter();
        timeAdapter.setData(datas);

        mKLineStockView.setAdapter(timeAdapter);
    }
}
