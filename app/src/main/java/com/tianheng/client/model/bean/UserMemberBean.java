package com.tianheng.client.model.bean;

/**
 * Created by huyg on 2018/7/8.
 */
public class UserMemberBean {

    /**
     * member : {"bindWebchat":false,"checkIdcard":true,"checkMobile":true,"foregiftMoney":199.00,"id":1013982542883196931,"mobile":"13429323525","name":"港港","networkActive":true,"payForegift":true,"payWallet":true,"tradeMoney":7.50,"walletMoney":100.00}
     * ticket : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjb21wYW55SWQiOjkzMzkzNzgzOTIxMTYyMjQwMiwiY2xpZW50VHlwZSI6ImNhYmluZXQiLCJtb2JpbGUiOiIxMzQyOTMyMzUyNSIsImlkIjoxMDEzOTgyNTQyODgzMTk2OTMxLCJ0aW1lIjoxNTMxMDI2NDE0MzQ3fQ.n1pwsFXJp694SkVKelMzpRjiK2MD5tKR3I8CMJr9JvQ
     * code : 2
     */

    private String member;
    private String ticket;
    private String code;

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
