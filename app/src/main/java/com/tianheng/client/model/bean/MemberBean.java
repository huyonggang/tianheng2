package com.tianheng.client.model.bean;

/**
 * Created by huyg on 2018/1/6.
 * "id": "933957205466611713",
 "mobile": "15088620343",//手机号
 "name": "lq",//姓名
 "avatar": "http://ww.xx.png",//头像
 "foregiftMoney": 299,//缴纳押金
 "walletMoney": 1000,//充值金额
 "bindWebchat": true,//是否绑定微信
 "checkMobile": true,//是否绑定手机号
 "checkIdcard": true,//是否进行身份认证
 "payForegift": true,//是否缴纳押金
 "payWallet": true,//是否充值
 "networkActive": true//是否激活
 */


public class MemberBean {
    private String id;
    private String companyId;
    private String mobile;
    private String avatar;
    private int gender;
    private String city;
    private String province;
    private String country;
    private String openId;
    private String name;
    private String idcard;
    private long foregiftMoney;
    private float walletMoney;
    private boolean bindWebchat;
    private boolean checkMobile;
    private boolean checkIdcard;
    private boolean payForegift;
    private boolean payWallet;
    private boolean networkActive;
    private String ticket;
    private float tradeMoney;

    public float getTradeMoney() {
        return tradeMoney;
    }

    public void setTradeMoney(float tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getForegiftMoney() {
        return foregiftMoney;
    }

    public void setForegiftMoney(long foregiftMoney) {
        this.foregiftMoney = foregiftMoney;
    }

    public float getWalletMoney() {
        return walletMoney;
    }

    public void setWalletMoney(float walletMoney) {
        this.walletMoney = walletMoney;
    }

    public boolean isBindWebchat() {
        return bindWebchat;
    }

    public void setBindWebchat(boolean bindWebchat) {
        this.bindWebchat = bindWebchat;
    }

    public boolean isCheckMobile() {
        return checkMobile;
    }

    public void setCheckMobile(boolean checkMobile) {
        this.checkMobile = checkMobile;
    }

    public boolean isCheckIdcard() {
        return checkIdcard;
    }

    public void setCheckIdcard(boolean checkIdcard) {
        this.checkIdcard = checkIdcard;
    }

    public boolean isPayForegift() {
        return payForegift;
    }

    public void setPayForegift(boolean payForegift) {
        this.payForegift = payForegift;
    }

    public boolean isPayWallet() {
        return payWallet;
    }

    public void setPayWallet(boolean payWallet) {
        this.payWallet = payWallet;
    }

    public boolean isNetworkActive() {
        return networkActive;
    }

    public void setNetworkActive(boolean networkActive) {
        this.networkActive = networkActive;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }
}
