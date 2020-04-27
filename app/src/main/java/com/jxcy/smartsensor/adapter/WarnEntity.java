package com.jxcy.smartsensor.adapter;

import java.io.Serializable;

public class WarnEntity implements Serializable {
    private String warnDes;
    private float warnValue = 37.0f;
    private int warnType;

    public String getWarnDes() {
        return warnDes;
    }

    public void setWarnDes(String warnDes) {
        this.warnDes = warnDes;
    }

    public float getWarnValue() {
        return warnValue;
    }

    public void setWarnValue(float warnValue) {
        this.warnValue = warnValue;
    }

    public int getWarnType() {
        return warnType;
    }

    public void setWarnType(int warnType) {
        this.warnType = warnType;
    }
}
