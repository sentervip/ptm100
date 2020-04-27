package com.jxcy.smartsensor.adapter;

import java.io.Serializable;

public class UnitEntity implements Serializable {
    private String unitValue;
    private boolean isCheck;
    private int unit_num;

    public String getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getUnit_num() {
        return unit_num;
    }

    public void setUnit_num(int unit_num) {
        this.unit_num = unit_num;
    }
}
