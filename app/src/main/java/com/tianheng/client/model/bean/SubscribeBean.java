package com.tianheng.client.model.bean;

/**
 * Created by huyg on 2018/7/22.
 */
public class SubscribeBean {
    private String ticket;
    private ExchangeBean exchangeModel;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public ExchangeBean getExchangeModel() {
        return exchangeModel;
    }

    public void setExchangeModel(ExchangeBean exchangeModel) {
        this.exchangeModel = exchangeModel;
    }
}
