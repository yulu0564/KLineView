package com.yulu.klineview.model;

/**
 * 管理K线图的各种属性
 */
public class TargetManager {
    private static TargetManager instance;

    private TargetManager() {
    }

    public static TargetManager getInstance() {
        if (instance == null) {
            synchronized (TargetManager.class) {
                instance = new TargetManager();
            }
        }
        return instance;
    }

    private int[] biasDefault = {6, 12, 24};
    private int[] bollDefault = {26, 2};  //boll的默认值标准差和宽度
    private int cciDefault = 14;
    private int[] kdjDefault = {9, 3, 3};
    private float[] macdDefault = {12, 26, 9};
    private int[] maDefault = {5, 10, 30};   //默认的5日，10日，30日
    private int[] rsiDefault = {6, 12, 24};

    public int[] getBiasDefault() {
        return biasDefault;
    }

    public void setBiasDefault(int[] biasDefault) {
        this.biasDefault = biasDefault;
    }

    public int[] getBollDefault() {
        return bollDefault;
    }

    public void setBollDefault(int[] bollDefault) {
        this.bollDefault = bollDefault;
    }

    public int getCciDefault() {
        return cciDefault;
    }

    public void setCciDefault(int cciDefault) {
        this.cciDefault = cciDefault;
    }

    public int[] getKdjDefault() {
        return kdjDefault;
    }

    public void setKdjDefault(int[] kdjDefault) {
        this.kdjDefault = kdjDefault;
    }

    public float[] getMacdDefault() {
        return macdDefault;
    }

    public void setMacdDefault(float[] macdDefault) {
        this.macdDefault = macdDefault;
    }

    public int[] getMaDefault() {
        return maDefault;
    }

    public void setMaDefault(int[] maDefault) {
        this.maDefault = maDefault;
    }

    public int[] getRsiDefault() {
        return rsiDefault;
    }

    public void setRsiDefault(int[] rsiDefault) {
        this.rsiDefault = rsiDefault;
    }
}