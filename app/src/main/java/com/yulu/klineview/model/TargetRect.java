package com.yulu.klineview.model;

import java.io.Serializable;

/**
 * 记录坐标系的上下左右坐标
 */
public class TargetRect implements Serializable {

    public float left;
    public float top;
    public float right;
    public float bottom;

    public TargetRect() {

    }

    public float getHeight(){
        return bottom-top;
    }
    public float getWidth(){
        return right-left;
    }


    public TargetRect(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void setTargetRect(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
}
