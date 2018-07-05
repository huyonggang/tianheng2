package com.tianheng.client.model.bean;

/**
 * Created by huyg on 2018/1/6.
 */

public class UserBean {

    private String ticket;
    private MemberBean member;
    private ManagerBean manager;

    public ManagerBean getManager() {
        return manager;
    }

    public void setManager(ManagerBean manager) {
        this.manager = manager;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public MemberBean getMember() {
        return member;
    }

    public void setMember(MemberBean member) {
        this.member = member;
    }
}
