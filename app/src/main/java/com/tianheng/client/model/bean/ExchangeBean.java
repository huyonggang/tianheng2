package com.tianheng.client.model.bean;

/**
 * Created by huyg on 2018/1/6.
 * "leaseBatteryNumber": "1122333",//旧电池编号
 * "emptyBoxNumber": 1,//空箱编号，用于存入旧电池
 * "exchangeBoxNumber": 8,//更换电池的箱子编号
 * "exchangeBatteryNumber": "22334455"//更换电池编号
 */

public class ExchangeBean {
    private String leaseBatteryNumber;
    private int emptyBoxNumber;
    private int exchangeBoxNumber;
    private String exchangeBatteryNumber;

    public String getLeaseBatteryNumber() {
        return leaseBatteryNumber;
    }

    public void setLeaseBatteryNumber(String leaseBatteryNumber) {
        this.leaseBatteryNumber = leaseBatteryNumber;
    }

    public int getEmptyBoxNumber() {
        return emptyBoxNumber;
    }

    public void setEmptyBoxNumber(int emptyBoxNumber) {
        this.emptyBoxNumber = emptyBoxNumber;
    }

    public int getExchangeBoxNumber() {
        return exchangeBoxNumber;
    }

    public void setExchangeBoxNumber(int exchangeBoxNumber) {
        this.exchangeBoxNumber = exchangeBoxNumber;
    }

    public String getExchangeBatteryNumber() {
        return exchangeBatteryNumber;
    }

    public void setExchangeBatteryNumber(String exchangeBatteryNumber) {
        this.exchangeBatteryNumber = exchangeBatteryNumber;
    }
}
