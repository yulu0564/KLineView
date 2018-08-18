package com.yulu.klineview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_k_line1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MainActivity.this, KLineActivity.class);
                startActivity(mIntent);
            }
        });
        findViewById(R.id.tv_k_line2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MainActivity.this, KLine2Activity.class);
                startActivity(mIntent);
            }
        });
        findViewById(R.id.tv_time_line1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MainActivity.this, TimeLineActivity.class);
                startActivity(mIntent);
            }
        });
        findViewById(R.id.tv_time_line2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MainActivity.this, TimeLineActivity2.class);
                startActivity(mIntent);
            }
        });
    }
}
