package com.yulu.klineview.bean;

import android.graphics.Paint;

/**
 * Created by yu.lu on 2018/4/17.
 */

public class Tagging {
    private float y;
    private float x;
    private String text;
    private Paint.Align align = Paint.Align.CENTER;
    private int state;  //0:跌1:平2：涨

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Paint.Align getAlign() {
        return align;
    }

    public void setAlign(Paint.Align align) {
        this.align = align;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
