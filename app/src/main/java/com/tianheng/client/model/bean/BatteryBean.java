package com.tianheng.client.model.bean;

/**
 * Created by huyg on 2018/1/20.
 */

public class BatteryBean {
    private int num;
    private int electricity;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getElectricity() {
        return electricity;
    }

    public void setElectricity(int electricity) {
        this.electricity = electricity;
    }
}
