package com.tianheng.client.model.bean;

/**
 * Created by huyg on 2018/3/20.
 * {
 "type": 1,
 "cabinetNumber": "2223334444",//电池柜编号
 "batteryNumber": "33x332ffss",//电池编号
 "boxNumber": 1//电池所在的箱子编号
 "curPower": 60,//当前电量
 "curVoltage": 220,//当前电压
 "chargerStatus": 0//0未使用1充电中2放电中
 }
 */

public class BatteryInfo {
    private int type;//电池信息
    private String cabinetNumber; //电池柜编号
    private String batteryNumber;//电池编号
    private int boxNumber;//电池所在的箱子编号
    private double curPower;//当前电量
    private double curVoltage;//当前电压
    private int chargerStatus;//充电状态0未使用1充电中2放电中

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;//电池状态

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCabinetNumber() {
        return cabinetNumber;
    }

    public void setCabinetNumber(String cabinetNumber) {
        this.cabinetNumber = cabinetNumber;
    }

    public String getBatteryNumber() {
        return batteryNumber;
    }

    public void setBatteryNumber(String batteryNumber) {
        this.batteryNumber = batteryNumber;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(int boxNumber) {
        this.boxNumber = boxNumber;
    }

    public double getCurPower() {
        return curPower;
    }

    public void setCurPower(double curPower) {
        this.curPower = curPower;
    }

    public double getCurVoltage() {
        return curVoltage;
    }

    public void setCurVoltage(double curVoltage) {
        this.curVoltage = curVoltage;
    }

    public int getChargerStatus() {
        return chargerStatus;
    }

    public void setChargerStatus(int chargerStatus) {
        this.chargerStatus = chargerStatus;
    }
}
