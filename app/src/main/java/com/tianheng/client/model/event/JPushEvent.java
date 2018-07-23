package com.tianheng.client.model.event;

import com.tianheng.client.model.bean.ExchangeBean;
import com.tianheng.client.model.bean.JPushBean;

/**
 * Created by huyg on 2018/7/23.
 */
public class JPushEvent {
    public ExchangeBean exchangeBean;
    public String ticket;

    public JPushEvent(ExchangeBean exchangeBean, String ticket) {
        this.exchangeBean = exchangeBean;
        this.ticket = ticket;
    }

    public ExchangeBean getExchangeBean() {
        return exchangeBean;
    }

    public void setExchangeBean(ExchangeBean exchangeBean) {
        this.exchangeBean = exchangeBean;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
